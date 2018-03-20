/**
 对文件各项进行计数
 **/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import sun.security.util.Length;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.awt.*;
import java.util.regex.Pattern;

public class Filecount {
    public int Charcount = 0;//字符计数
    public int Wordcount = 0;//单词计数
    public int Linecount = 0;//行计数
    public int Spacelinecount = 0;//空行计数
    public int Codelinecount = 0;//代码行计数
    public int Notelinecount = 0;//注释行计数
    public String Filename = "";//记录文件名
	List<String> filePath = new ArrayList<String>();
    File afile;
    File[] files;

    public void Filecounter(String path) throws IOException {
        String str = "";
        //打开文件输入流
        FileInputStream fis = new FileInputStream(path);
        //字符流写入缓冲区
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        //  换取文件名以备输出
        String[] Strings = path.split("\\\\");
        Filename = Strings[Strings.length - 1];
        boolean note = false;
		String Nodebegin="\\s*/\\*.*";
		String Nodeend=".*\\*/\\s*";
        //readLine()每次读取一行，转化为字符串，br.readLine()为null时，不执行
        while ((str = br.readLine()) != null) {
            str += '\n';//readLine()不会读取每一行最后的换行符，所以这里我们手动给每一行加上换行符
            Charcount += str.length();//字符计数

            str = str.trim();//用trim函数去除每一行第一个字符前的空格，以便之后所有的计数操作
            String[] wordc = str.split(" |,");//按空格或逗号进行分词操作
            Wordcount += wordc.length;//单词计数

            Linecount++;//行计数
	       
		    /*if((str==""||str.length()<=1)&&（label==0）{
				Spacelinecount++;//空行的检测与计数
			else if((str.length()>2&&str.substring(1,3).equals("//"))||str.substring(0,2).equals("//")&&label==0)
				Notelinecount++;
			else if((str.length()>2&&str.substring(1,3).equals("/*"))||str.substring(0,2).equals("/*")&&label==0){
				Notelinecount++;
			else*/
			if(str.matches("//.*")){
				Notelinecount++;
			}
			else if(str.matches("^/\\*.*\\*/$")){
				Notelinecount++;
			}
			else if(note){
				Notelinecount++;
				if(str.matches(".*\\*/$")){
					note=false;
				}
			}
			else if(str.matches("^/\\*.*[^\\*/$]")){
				Notelinecount++;
				note=true;
			}
			else if(!note&&str.matches("[\\s&&[^\\n]]*")){
				Spacelinecount++;
			}
			else{
				Codelinecount++;
			}
			
        }
        isr.close();
    }

    public void allPath(String dir, String fileClass) {
        File afile = new File(dir);
        //listFiles()方法是返回某个目录下所有文件和目录的绝对路径，返回的是File数组
        File[] files = afile.listFiles();
        //如果目录为空，直接退出
        if (files == null){
            return;
        }
        else {
            //遍历文件夹内所有文件
            for (File file : files) {
                //isFile()判断给定文件名是否为正常的文件
                if (file.isFile()) {
                    //getpath()得到缩写的路径，根据当前目录位置可以缩写路径。得到相对路径
                    String aPath = file.getPath();
                    //用i记录文件名中“.”的位置，以获取后缀名
                    int i = aPath.length()-1;
                    for(; i>=0; i--){
                        if(aPath.charAt(i) == '.')
                            break;
                        if(i==0){
                            i=1;
                            break;
                        }
                    }
                    //判断后缀名是否等于fileClass
                    if(aPath.substring(i, aPath.length()).equals(fileClass))
                        filePath.add(file.getPath());
                }
                else if (file.isDirectory()) {
                    allPath(file.getAbsolutePath(),fileClass);
                }
            }
        }
    }
    public static String Output(String[] args, Filecount counter, String buffer){
        int notechar = 0;//根据用户的输入标记哪些项目要被输出
        int noteword = 0;
        int noteline = 0;
		int notea = 0;
        for (String span:args) {
            if (span.equals("-c")) {
                notechar = 1;
            }
            else if (span.equals("-w")) {
                noteword = 1;
            }
            else if (span.equals("-l")) {
                noteline = 1;
            }
			else if(span.equals("-a")){
				notea = 1;
			}
        }
        if (notechar == 1) {
            System.out.println(counter.Filename + ",字符数：" + counter.Charcount);
            buffer += counter.Filename + ",字符数：" + counter.Charcount + "\r\n";
        }
        if (noteword == 1) {
            System.out.println(counter.Filename + ",单词数：" + counter.Wordcount);
            buffer += counter.Filename + ",单词数：" + counter.Wordcount + "\r\n";
        }
        if (noteline == 1) {
            System.out.println(counter.Filename + ",行数：" + counter.Linecount);
            buffer += counter.Filename + ",行数：" + counter.Linecount + "\r\n";
        }
		if(notea == 1) {
			System.out.println(counter.Filename+",代码行/空行/注释行："+counter.Codelinecount+"/"+counter.Spacelinecount+"/"+counter.Notelinecount);
			buffer+=counter.Filename+",代码行/空行/注释行："+counter.Codelinecount+"/"+counter.Spacelinecount+"/"+counter.Notelinecount+"\r\n";
        }
		return buffer;
    }
    public static void main(String[] args) throws IOException {
        String path = "E:\\wordc\\src\\Filecount.java";//需要统计的文件的路径
        String outputpath=".\\result.txt";//输出文件的路径
        String configg="-\\w";//用于匹配参数
        String outputBuffer = "";//文件输出缓冲区，将缓冲区的文件输出到result.txt
        int lable=0;//定义工作模式：labal=0为默认统计，labal=1为递归统计
        int changeoutput=0;//当此变量等于1时，意味着有-o参数，需要把buffer区的文件输出到指定的文件（例如output.txt）
        //遍历参数数组args[]
        for(int i=0;i<args.length;i++){
            if(!(Pattern.matches(configg,args[i]))){
                if(args[i-1].equals("-o"))
                {
                    outputpath=args[i];
                    changeoutput=1;
                }
                else{
                    path = args[i];
                }
            }
        }
        if(args[0].equals("-s")){
            lable=1;
        }
        else{
            lable=0;
        }
        if(lable==1){
            String[] Strings=path.split("\\\\");
            String fileClass=Strings[Strings.length-1];
            String dir=path.substring(0,path.length()-fileClass.length());
            int i = fileClass.length()-1;
            for(; i>=0; i--){
                if(fileClass.charAt(i) == '.')
                    break;
                if(i == 0){
                    i=1;
                    break;
                }
            }
            fileClass=fileClass.substring(i);
            Filecount s=new Filecount();
            s.allPath(dir,fileClass);
            for(String list:s.filePath){
               // System.out.println("File name："+list);
                //outputBuffer+="File name:"+list+"\r\n";
                Filecount counter=new Filecount();
                counter.Filecounter(list);
                outputBuffer=Output(args,counter,outputBuffer);
            }
        }
        if(lable==0){
            Filecount counter = new Filecount();
            counter.Filecounter(path);
            outputBuffer = Output(args, counter, outputBuffer);
        }
        if(changeoutput==1){
            File bfile = new File(outputpath);
            bfile.createNewFile();
            BufferedWriter output = new BufferedWriter(new FileWriter(bfile));
            output.write(outputBuffer);
            output.flush();
            output.close();
        }
    }
}

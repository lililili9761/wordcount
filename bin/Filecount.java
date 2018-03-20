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

    public void Filecounter(String path,int isstop,String[] Stoplist) throws IOException {
        String str = "";
        FileInputStream fis = new FileInputStream(path);//打开文件输入流
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);//字符流写入缓冲区
        String[] Strings = path.split("\\\\");//将文件路径按\\进行分词操作
        Filename = Strings[Strings.length - 1]; 
		//文件路径分词后得到的字符串数组的最后一个是文件名，获取文件名以备输出
        boolean note = false;//用于纪录/**/类型的注释的开始与结束
       
	   //readLine()每次读取一行，转化为字符串，br.readLine()为null时，不执行
        while ((str = br.readLine()) != null) {
            Charcount += str.length();//字符计数
            str = str.trim();//用trim函数去除每一行第一个字符前的空格，以便之后所有的计数操作
            String[] wordc = str.split(" |,");//根据需求按空格或逗号进行分词操作，将得到的单词放入一个字符串数组
            Wordcount += wordc.length;//单词计数：字符串数组的长度就是单词的个数
			//当需要对停词表中的词进行处理时
			if(isstop == 1){
				for(String w : wordc){
					for(String words : Stoplist){
						//equalsIngnoreCase()与equal()不同，是不区分大小写的比较
						if(w.equalsIgnoreCase(words)){
							Wordcount--;//如果Stoplist中的词与wordc中的词匹配，将单词数减一
						}
					}
				}
			}
            Linecount++;//行计数
			Charcount=Charcount+Linecount-1;
			/*
			这里是因为readLine()获得的一行不包括该行末尾的换行符
			前面的Charcount仅仅计算了一行中除了换行符外的字符
			在计算出行数后，由于最后一行不包含换行符，而前面所有行都包含换行符，所以做出这样的计算
			*/
	        if(str.matches("//.*")){//正则表达式的使用
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
			else if(!note&&(str.matches("[\\s&&[^\\n]]*"))||(str.matches("[{}]")))
			{
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
		String Stoplistpath = null;
		String[] Stoplist = null;
        String configg="-\\w";//用于匹配"-+字母"参数的正则表达式
        String outputBuffer = "";//文件输出缓冲区，将缓冲区的文件输出到result.txt
        int lable=0;//定义工作模式：labal=0为默认统计，labal=1为递归统计
        int isstop=0;//判别是否需要考虑停用词
		int changeoutput=0;//当此变量等于1时，意味着有-o参数，需要把buffer区的文件输出到指定的文件（例如output.txt）
        //遍历参数数组args[]
        for(int i=0;i<args.length;i++){
            if(!(Pattern.matches(configg,args[i]))){
				if(args[i-1].equals("-e")){
					Stoplistpath=args[i];
					isstop=1;
				}
                if(args[i-1].equals("-o")){
                    outputpath=args[i];
                    changeoutput=1;
                }
                else{
                    path = args[i];
                }
            }
        }
		//首先对停用表进行处理
		if(isstop==1){
			//打开文件输入流
			FileInputStream stop = new FileInputStream(Stoplistpath);
			//字符流写入缓冲区
			InputStreamReader Stopstream = new InputStreamReader(stop);
			BufferedReader Stopbuffer = new BufferedReader(Stopstream);
			String Stopstring="";
			while((Stopstring=Stopbuffer.readLine())!=null){
				Stoplist=Stopstring.split(" ");
			}
		}
		//接着对工作模式进行判断
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
                Filecount counter = new Filecount();
                counter.Filecounter(list,isstop,Stoplist);
                outputBuffer=Output(args,counter,outputBuffer);
            }
        }
        if(lable==0){
            Filecount counter = new Filecount();
            counter.Filecounter(path,isstop,Stoplist);
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
		if(changeoutput==0){
			String soutputpath=".\\result.txt";//当没有-o时的输出路径
			File sfile = new File(soutputpath);
			sfile.createNewFile();
			BufferedWriter soutput = new BufferedWriter(new FileWriter(sfile));
			soutput.write(outputBuffer);
			soutput.flush();
			soutput.close();
		}
    }
}

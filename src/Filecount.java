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
        int note = 0;
        //readLine()每次读取一行，转化为字符串，br.readLine()为null时，不执行
        while ((str = br.readLine()) != null) {
            str += '\n';//readLine()不会读取每一行最后的换行符，所以这里我们手动给每一行加上换行符
            Charcount += str.length();//字符计数

            str = str.trim();//用trim函数去除每一行第一个字符前的空格
            String[] wordc = str.split(" |,");//按空格或逗号进行分词操作
            Wordcount += wordc.length;//单词计数

            Linecount++;//行计数
        }
        isr.close();
    }
        public static String Output(String[] args, Filecount counter, String buffer){
            int notechar = 0;//根据用户的输入标记哪些项目要被输出
            int noteword = 0;
            int noteline = 0;
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
            return buffer;
        }
        public static void main(String[] args) throws IOException {
            String path = "E:/wordc/src/Filecount.java";
            String outputBuffer = "";
            Filecount counter = new Filecount();
            counter.Filecounter(path);
            outputBuffer = Output(args, counter, outputBuffer);
        }
}
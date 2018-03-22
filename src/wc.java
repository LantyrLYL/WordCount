import java.io.*;
import java.util.ArrayList;

public class wc {
    public static void main(String[] args) throws Exception{
        if(args.length<2){
            throw new IllegalArgumentException("输入足够的参数");
        }
        if(!args[0].equals("-s")&&!args[0].equals("-c")&&!args[0].equals("-w")&&!args[0].equals("-l")&&!args[0].equals("-a")){
            throw new IllegalArgumentException("输入正确的参数");
        }

        int i=0,flagc=0,flagw=0,flagl=0,flago=0,flaga=0,flags=0,flage=0;
        while(args[i].equals("-s")||args[i].equals("-c")||args[i].equals("-w")||args[i].equals("-l")||args[i].equals("-a"))
        {
            if(args[i].equals("-s")){////是否输入了参数-s
                flags=1;
            }
            else if(args[i].equals("-c")){//是否输入了参数-c
                flagc=1;
            }
            else if(args[i].equals("-w")){//是否输入了参数-w
                flagw=1;
            }
            else if(args[i].equals("-l")) {//是否输入了参数-l
                flagl=1;
            }
            else if(args[i].equals("-a")) {//是否输入了参数-a
                flaga=1;
            }
            else {
                break;
            }
            i++;
        }
        if(flagc==0&&flagw==0&&flagl==0&&flaga==0)
            throw new IllegalArgumentException("没有确定统计内容，输入正确的参数");
        if(i==args.length)
            throw new IllegalArgumentException("没有确定被统计文件，输入正确的参数");

        String filepath=null;//默认被统计文件路径
        String extrapath=null;//默认停用词表路径
        String outpath="result.txt";//默认结果保存路径
        filepath=args[i];//被统计文件路径
        i++;
        while(i<args.length){
            if(args[i].equals("-o")&&flago==0){//是否输入了参数-o
                i++;
                flago=1;
                if(i==args.length){
                    throw new IllegalArgumentException("输入正确的参数");
                }
                outpath=args[i];
            }
            else if(args[i].equals("-e")&&flage==0){//是否输入了参数-e
                i++;
                flage=1;
                if(i==args.length){
                    throw new IllegalArgumentException("输入正确的参数");
                }
                extrapath=args[i];
            }
            i++;
        }

        ArrayList<String> extraList=new ArrayList<String>(0);//取出文件中的停用词
        if(flage==1){
            InputStreamReader isr0 = new InputStreamReader(new FileInputStream(extrapath));
            BufferedReader br0 = new BufferedReader(isr0);
            if(flage==1)
            {
                String str=null;
                while((str=br0.readLine())!=null){
                    String[] tmp= str.split(" ");//停用词以空格分割
                    for(i=0;i<tmp.length;i++)
                        if(!tmp[i].isEmpty())
                            extraList.add(tmp[i]);
                }
            }
            isr0.close();
        }

        StringBuilder p=new StringBuilder();
        ArrayList<String> filePath=new ArrayList<String>();
        File file=null;
        if(flags==0)//假如只统计一个文件，直接扫描该文件
            p.append(wordcount(flagc,flagw,flagl,flaga,filepath,extraList));
        else{//假如递归统计目录下符合条件的文件

            file=new File(System.getProperty("user.dir"));
            String suff=filepath.substring(filepath.lastIndexOf("*")+1);//取出文件后缀
            filePath=getAllFilePaths(file,filePath);
            for(i=0;i<filePath.size();i++){//统计每个符合条件的文件的内容
                if(filePath.get(i).endsWith(suff)){
                    p.append(wordcount(flagc,flagw,flagl,flaga,filePath.get(i),extraList));
                }
            }
        }
        String res=p.toString();
        File myFile=new File(outpath);//生成结果文件
        BufferedWriter out = new BufferedWriter(new FileWriter(myFile));
        out.write(res);
        out.close();
    }

	//获取当前目录和子目录下所有文件
    public static ArrayList<String> getAllFilePaths(File filepath,ArrayList<String> filePaths){
        File[] array=filepath.listFiles();
        if(array==null)
            return filePaths;
        for(File f:array){
            if(f.isDirectory()){//递归读取子目录下内容
                filePaths.add(f.getPath());
                getAllFilePaths(f,filePaths);
            }else{
                filePaths.add(f.getPath());
            }
        }
        return filePaths;
    }

	//文件扫描并统计
    public static String wordcount(int flagc,int flagw,int flagl,int flaga,String path,ArrayList<String> extraList) throws IOException{
        int i=0;
        InputStreamReader isr = new InputStreamReader(new FileInputStream(path));
        BufferedReader br = new BufferedReader(isr);
        int numChar,numWord,numLine,numLineCode,numLineAnn,numLineEmpty,flagann;
        numChar=numWord=numLine=numLineCode=numLineAnn=numLineEmpty=flagann=0;
        String str=null;int flagex=0,j=0;
        while((str=br.readLine())!=null){
            char[] chars=str.toCharArray();
            for( i=0;i<chars.length;i++) {//统计每行字符数并加到字符总数里
                numChar++;
            }
            numChar++;//加上每一行的换行
            String[] tmp= str.split(" |,");
            for(i=0;i<tmp.length;i++){
                if(tmp[i].length()!=0){
                    if(extraList.size()!=0){//假如使用了停用词表
                        flagex=0;
                        for(j=0;j<extraList.size();j++) {
                            if (tmp[i].equals(extraList.get(j))) {
                                flagex = 1;
                                break;
                            }
                        }
                        if(flagex!=1) {//只统计不在停用词表里的单词
                            numWord++;
                        }
                    }
                    else{
                        numWord++;
                    }
                }
            }

            if(chars.length>1) {//判断注释行
                if(chars[0]=='/'&&chars[1]=='/'){//“//”开头注释行
                    numLineAnn++;
                }
                else if(chars[0]=='/'&&chars[1]=='*'){//“/*”开头注释行
                    numLineAnn++;
                    flagann=1;
                }
                else if(chars.length>2) {
                    if ((chars[0] == '{' || chars[0] == '}')) {//“{//”开头注释行
                        if (chars[1] == '/' && chars[2] == '/'){
                            numLineAnn++;
                        }
                        else if (chars[1] == '/' && chars[2] == '*') {//“{/*”开头注释行
                            numLineAnn++;
                            flagann = 1;
                        }
                        else if(flagann!=1) {//否则就是代码行
                            numLineCode++;
                        }
                    }
                    else if(flagann!=1) {
                        numLineCode++;
                    }
                }
                else if(flagann!=1) {
                    numLineCode++;
                }
                else {//在“/*”之后“*/”之前的注释
                    numLineAnn++;
                    for (i = 0; i < chars.length; i++) {//判断“/*”注释的结束“*/”
                        if (chars[i] == '*') {
                            if (i + 1 < chars.length)
                                if (chars[i + 1] == '/')
                                    flagann = 0;
                        }
                    }
                }
            }
            else {//空行
                numLineEmpty++;//不超过一个可显示字符的空行
            }
            numLine++;
        }
        numChar--;//减去最后一行末尾不存在换行
        isr.close();
        StringBuilder a=new StringBuilder();//生成统计结果
        if(flagc==1)
            a.append(path+",字符数:"+numChar+"\r\n");
        if(flagw==1)
            a.append(path+",单词数:"+numWord+"\r\n");
        if(flagl==1)
            a.append(path+",行数:"+numLine+"\r\n");
        if(flaga==1)
            a.append(path+",代码行/空行/注释行:"+numLineCode+"/"+numLineEmpty+"/"+numLineAnn+"\r\n");
        String res=a.toString();
        return res;
    }
}
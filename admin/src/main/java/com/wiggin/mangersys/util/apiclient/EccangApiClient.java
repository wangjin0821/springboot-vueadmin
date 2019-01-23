package com.wiggin.mangersys.util.apiclient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Component;

@Component
public class EccangApiClient {
	
	public void getProductList() throws Exception {
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
        URL url = new URL(wsdl);
        HttpURLConnection openConnection = (HttpURLConnection) url.openConnection();
        openConnection.setConnectTimeout(timeout);
        openConnection.setUseCaches(false);
        openConnection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        openConnection.setRequestMethod("POST");
        openConnection.setDoOutput(true);
        openConnection.setDoInput(true);
        openConnection.setReadTimeout(timeout);
        
        DataOutputStream dataOutputStream = new DataOutputStream(openConnection.getOutputStream());
		dataOutputStream.write(sb.toString().getBytes("utf-8"));
		dataOutputStream.flush();
		
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openConnection.getInputStream(), "utf-8"));
		String line = null;
		StringBuffer strBuf = new StringBuffer();
		while((line = bufferedReader.readLine()) != null) {
			strBuf.append(line);
		}
		bufferedReader.close();
		dataOutputStream.close();
		
		System.out.println(strBuf.toString());
		
    }
}

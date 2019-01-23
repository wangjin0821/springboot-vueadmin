package com.wiggin.mangersys.util.apifeignclient.eccang;

import org.springframework.stereotype.Component;

import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

@Component
public interface EccangClient {
	
	@RequestLine("POST /default/svc-open/web-service-v2")
	@Headers("Content-Type: application/xml")
	@Body("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" + 
			"    xmlns:ns1=\"http://www.example.org/Ec/\">\n" + 
			"    <SOAP-ENV:Body>\n" + 
			"        <ns1:callService>\n" + 
			"            <paramsJson>{paramsJson}</paramsJson>\n" + 
			"            <userName>{userName}</userName>\n" + 
			"            <userPass>{userPass}</userPass>\n" + 
			"            <service>{serviceName}</service>\n" + 
			"        </ns1:callService>\n" + 
			"    </SOAP-ENV:Body>\n" + 
			"</SOAP-ENV:Envelope>")
	String sendRequest(@Param("userName") String userName, @Param("userPass") String userPass, @Param("serviceName") String serviceName, @Param("paramsJson") String paramsJson);
}

package com.wiggin.mangersys.util.apifeignclient.eccang;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wiggin.mangersys.util.Page;
import com.wiggin.mangersys.util.apifeignclient.eccang.bean.EccangProductRequest;
import com.wiggin.mangersys.util.apifeignclient.eccang.bean.EccangProductResponse;

import feign.Feign;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ConfigurationProperties(prefix = "eccang")
@Data
public class EccangApi {
	
	private String userName;
	private String userPass;
	private String apiService;
	private Integer pageSize;
	
	/**
	 * 
	 */
	public Page<EccangProductResponse> getProductList(EccangProductRequest productReq) {
		productReq.setPageSize(pageSize);
		Object json = JSON.toJSON(productReq);
		log.info("productReq=>{}", json.toString());
		String sendRequest = this.sendRequest("getProductList", json.toString());
		JSONObject parseObject = JSON.parseObject(sendRequest);
		Integer code = parseObject.getInteger("code");
		if (new Integer(200).equals(code)) {
			Integer totalCount = parseObject.getInteger("totalCount");
			Integer page = parseObject.getInteger("page");
			String message = parseObject.getString("message");
			String dataJsonStr = parseObject.getString("data");
			List<EccangProductResponse> eccangProductList = JSONObject.parseArray(dataJsonStr, EccangProductResponse.class);
			log.info("totalCount=>{}, message={}, currentPage=>{}, size=>{}", totalCount, message, page, eccangProductList.size());
			return new Page<>(totalCount, 0, eccangProductList);
		}
		return null;
	}
	
	
	/**
	 * 
	 * @param serviceName
	 * @param paramsJson
	 * @return
	 */
	private String sendRequest(String serviceName, String paramsJson) {
		EccangClient client = Feign.builder()
				.target(EccangClient.class, apiService);
		String response = client.sendRequest(userName, userPass, serviceName, paramsJson);
		//log.info("response=>{}", response);
		String parseSoapResponse = parseSoapResponse(response);
		//log.info("parseSoapResponse=>{}", parseSoapResponse);
		return parseSoapResponse;
	}
	
	
	/**
	 * 
	 * @param response
	 * @return
	 */
	private String parseSoapResponse(String response) {
		String responJsonStr = null;
		try {
			MessageFactory msgFactory = MessageFactory.newInstance();
			try {
				SOAPMessage soapMessage = msgFactory.createMessage(new MimeHeaders(), new ByteArrayInputStream(response.getBytes("utf-8")));
				soapMessage.saveChanges();
				SOAPBody soapBody = soapMessage.getSOAPBody();
				Iterator childElements = soapBody.getChildElements();
				if (childElements.hasNext()) {
		            SOAPElement element = (SOAPElement) childElements.next();
		            responJsonStr = element.getTextContent();
		        }
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SOAPException e) {
			e.printStackTrace();
		}
		return responJsonStr;
	}
}

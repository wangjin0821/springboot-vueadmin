package com.wiggin.mangersys.util.apifeignclient.eccang;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.wiggin.mangersys.config.BusinessException;
import com.wiggin.mangersys.config.ExceptionCodeEnum;
import com.wiggin.mangersys.util.BeanUtil;
import com.wiggin.mangersys.util.Page;
import com.wiggin.mangersys.util.apifeignclient.eccang.bean.EcApiResponse;
import com.wiggin.mangersys.util.apifeignclient.eccang.bean.EcProductCategoryResponse;
import com.wiggin.mangersys.util.apifeignclient.eccang.bean.EcProductRequest;
import com.wiggin.mangersys.util.apifeignclient.eccang.bean.EcProductResponse;
import com.wiggin.mangersys.util.apifeignclient.eccang.bean.EcProductSaleStatusResponse;

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
     * 获取产品列表
     * 
     * @param productReq
     * @return
     */
    public Page<EcProductResponse> getProductList(EcProductRequest productReq) {
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
            List<EcProductResponse> eccangProductList = JSONObject.parseArray(dataJsonStr, EcProductResponse.class);
            log.info("totalCount=>{}, message={}, currentPage=>{}, size=>{}", totalCount, message, page, eccangProductList.size());
            return new Page<>(totalCount, 0, eccangProductList);
        }
        return null;
    }


    /**
     * 获取单个产品
     * 
     * @param sku
     * @return
     */
    public EcProductResponse getProductBySku(String sku) {
        ConcurrentMap<String, Object> requestParam = Maps.newConcurrentMap();
        requestParam.put("productSku", sku);
        String jsonString = BeanUtil.toJsonString(requestParam);
        String sendRequest = sendRequest("getProductBySku", jsonString);
        JSONObject parseObject = JSON.parseObject(sendRequest);
        Integer code = parseObject.getInteger("code");
        if (new Integer(200).equals(code)) {
            String dataJsonStr = parseObject.getString("data");
            log.info("dataJsonStr={}", dataJsonStr);
            List<EcProductResponse> parseArray = BeanUtil.parseArray(dataJsonStr, EcProductResponse.class);
            if (CollectionUtils.isNotEmpty(parseArray)) {
            	return parseArray.get(0);
            }
            return null;
        } else {
            String message = parseObject.getString("message");
            throw new BusinessException(ExceptionCodeEnum.SERVICE_ERROR.getCode(), message);
        }
    }


    /**
     * 获取产品销售状态
     */
    public List<EcProductSaleStatusResponse> getSaleStatus() {
        String sendRequest = sendRequest("getSaleStatus", "{}");
        EcApiResponse response = BeanUtil.parseObject(sendRequest, EcApiResponse.class);
        if (response.getData() != null) {
            String dataJsonStr = response.getData();
            log.info("dataJsonStr => {}", dataJsonStr);
            return BeanUtil.parseArray(dataJsonStr, EcProductSaleStatusResponse.class);
        } else {
            throw new BusinessException(ExceptionCodeEnum.SERVICE_ERROR.getCode(), "网络异常获取数据失败");
        }
    }


    /**
     * 获取分类
     * 
     * @return
     */
    public List<EcProductCategoryResponse> getProductCategoryBase() {
        String sendRequest = sendRequest("getProductCategoryBase", "{}");
        EcApiResponse response = BeanUtil.parseObject(sendRequest, EcApiResponse.class);
        log.info("sendRequest ={}, response => {}", sendRequest, response);
        if ("Success".equals(response.getAsk())) {
            String dataJsonStr = response.getData();
            log.info("dataJsonStr => {}", dataJsonStr);
            List<EcProductCategoryResponse> cateList = BeanUtil.parseArray(dataJsonStr, EcProductCategoryResponse.class);
            log.info("cateList.size=>{}", cateList.size());
            return cateList;
        } else {
            throw new BusinessException(ExceptionCodeEnum.SERVICE_ERROR.getCode(), response.getMeesage());
        }
    }


    /**
     * 发送请求接口
     * 
     * @param serviceName
     * @param paramsJson
     * @return
     */
    private String sendRequest(String serviceName, String paramsJson) {
        EccangClient client = Feign.builder().target(EccangClient.class, apiService);
        String response = client.sendRequest(userName, userPass, serviceName, paramsJson);
        // log.info("response=>{}", response);
        String parseSoapResponse = parseSoapResponse(response);
        // log.info("parseSoapResponse=>{}", parseSoapResponse);
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

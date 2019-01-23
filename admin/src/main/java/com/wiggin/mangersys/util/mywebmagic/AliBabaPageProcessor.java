package com.wiggin.mangersys.util.mywebmagic;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

@Slf4j
public class AliBabaPageProcessor implements PageProcessor {

	private Site site = Site.me()
			// .addHeader(":authority", "detail.1688.com")
			.addHeader(":method", "GET").addHeader(":scheme", "https").setUserAgent(
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {
		String title = page.getHtml().$("#mod-detail-title .d-title", "text").toString();
		String price = page.getHtml().$("#mod-detail-price .ladder-3-1 .value", "text").toString();
		List<String> dataImgList = page.getHtml().$("#dt-tab li", "data-imgs").all();
		log.info("title=>{}", title);
		log.info("price=>{}", price);
		log.info("dataImgList=>{}", dataImgList);
		if (CollectionUtils.isNotEmpty(dataImgList)) {
			dataImgList.forEach(imgStr -> {
				JSONObject parseObject = JSON.parseObject(imgStr);
				String imgUrl = (String) parseObject.get("original");
				download(imgUrl);
			});
		}
	}

	public static void main(String[] args) {
		Spider.create(new AliBabaPageProcessor())
				.addUrl("https://detail.1688.com/offer/573973472087.html?spm=a261y.7663282.1998411376.3.45106455jRPnR4&scm=1007.19151.108963.0")
				.thread(1).run();
	}
	
	
	public static void download(String urlStr) {
		// 截取图片的名称
        String fileName = urlStr.substring(urlStr.lastIndexOf("/"));
        
        //创建文件的目录结构
        StringBuilder filePath = new StringBuilder("F://xianjin/pics/");
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String format = dateFormat.format(new Date());
        filePath.append(format);
        filePath.append("/");
        System.out.println(filePath);
        File dirPath = new File(filePath.toString());
        if(!dirPath.exists()){// 判断文件夹是否存在，如果不存在就创建一个文件夹
        	dirPath.mkdirs();
        }
        
        FileOutputStream fileOutputStream = null;
        ByteArrayOutputStream byteArrayOutputStream;
		try {
			URL url = new URL(urlStr);
			
			DataInputStream dataInputStream = new DataInputStream(url.openStream());
			
			File file = new File(dirPath + fileName);
			fileOutputStream = new FileOutputStream(file);
			byteArrayOutputStream = new ByteArrayOutputStream();
			
			byte[] buffer = new byte[1024];
			int length;
			
			while((length = dataInputStream.read(buffer)) > 0) {
				byteArrayOutputStream.write(buffer, 0, length);
			}
			fileOutputStream.write(byteArrayOutputStream.toByteArray());
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (Objects.nonNull(fileOutputStream)) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (Objects.nonNull(fileOutputStream)) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}

}

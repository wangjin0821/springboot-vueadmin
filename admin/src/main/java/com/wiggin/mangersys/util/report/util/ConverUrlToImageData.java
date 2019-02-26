package com.wiggin.mangersys.util.report.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ConverUrlToImageData implements DataConvert {

    @Override
    public byte[] convert(String source) {
        if (source != null) {
            /*
             * ByteArrayOutputStream baos = new ByteArrayOutputStream(); try {
             * // File file = new File("E:\\picture\\S003710-1.jpg");
             * BufferedImage input = ImageIO.read(new URL(source)); if (input !=
             * null) { ImageIO.write(input, "JPG", baos); return
             * baos.toByteArray(); } } catch (MalformedURLException e) {
             * log.error("source={}, getfail, e=>{}", source, e); } catch
             * (IOException e) { log.error("source={}, getfail, e=>{}", source,
             * e); }
             */
            try {
                log.info("source=>{}", source);
                URL url = new URL(source);
                try {
                    HttpURLConnection openConnection = (HttpURLConnection) url.openConnection();
                    openConnection.setRequestMethod("GET");
                    openConnection.setConnectTimeout(10 * 1000);
                    openConnection.setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11");

                    openConnection.connect();
                    InputStream inputStream = openConnection.getInputStream();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, len);
                    }
                    inputStream.close();
                    return byteArrayOutputStream.toByteArray();
                } catch (IOException e) {
                    log.error("source={}, getfail, e=>{}", source, e);
                }
            } catch (MalformedURLException e) {
                log.error("source={}, getfail, e=>{}", source, e);
            }
        }
        return null;
    }
}

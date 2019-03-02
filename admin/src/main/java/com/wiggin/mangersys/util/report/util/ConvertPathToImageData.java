package com.wiggin.mangersys.util.report.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ConvertPathToImageData implements DataConvert {

    @Override
    public byte[] convert(String source) {
        if (source != null) {
            log.info("source=>{}", source);
            try {
                File file = new File(source);
                return Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                log.error("source={}, getfail, e=>{}", source, e);
            } catch (Exception e) {
                log.error("source={}, getfail, e=>{}", source, e);
            }
        }

        return null;
    }

}

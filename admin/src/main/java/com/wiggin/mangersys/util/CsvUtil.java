package com.wiggin.mangersys.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wiggin.mangersys.util.report.vo.ExportVO;

import au.com.bytecode.opencsv.CSVReader;


public class CsvUtil {

    /**
     * 创建csv
     * 
     * @param baseExportVO
     * @param list
     * @param file
     * @throws IOException
     */
    public static void createCsv(List<Map<String, Object>> list, File file, List<ExportVO> exportVOs) throws IOException {
        boolean falg = false;
        try (FileInputStream fileInputStream = new FileInputStream(file); InputStreamReader isr = new InputStreamReader(fileInputStream, "GB2312");) {
            @SuppressWarnings("resource")
            CSVReader csvReader = new CSVReader(isr);
            List<String[]> csvlist = csvReader.readAll();

            if (!CollectionUtils.isEmpty(csvlist)) {
                falg = true;
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, falg), "GBK"), 1024);) {
            if (!falg) {
                createTitles(exportVOs, bw);
            }

            createContent(list, exportVOs, bw);

            bw.flush();
        }
    }


    /**
     * 创建表头
     * 
     * @param titles
     * @param row
     * @return 返回下一行的行数 如复杂的导出格式需要重新或者重新修改兼容
     * @throws IOException
     */
    public static void createTitles(List<ExportVO> exportVOs, BufferedWriter bw) throws IOException {
        StringBuilder sb = new StringBuilder();

        for (ExportVO exportVO : exportVOs) {
            String rowStr = sb.append("\"").append(exportVO.getTitle()).append("\",").toString();
            bw.write(rowStr);

            sb.setLength(0);
        }

        bw.newLine();
    }


    /**
     * 创建文件内容
     * 
     * @param list
     * @param exportVOs
     * @param bw
     * @return
     * @throws IOException
     */
    public static Map<String, BigDecimal> createContent(List<Map<String, Object>> list, List<ExportVO> exportVOs, BufferedWriter bw) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> map : list) {
            for (ExportVO exportVO : exportVOs) {
                sb.append("\"");
                sb.append(String.valueOf(ObjectUtils.defaultIfNull(map.get(exportVO.getField()), "")));

                sb.append("\",");
            }

            sb.setLength(sb.length() - 1);
            bw.write(sb.toString());
            sb.setLength(0);

            bw.newLine();
        }

        return null;
    }


    /**
     * 将csv数据转成List<Map<String, String>> 形式
     * 
     * @param is
     * @param templateColumns
     * @return
     * @throws IOException
     */
    public static List<Map<String, String>> getCsvData(InputStream is, List<String> templateColumns, Map<String, String> columnMap) throws IOException {
        InputStreamReader isr = new InputStreamReader(is, "GB2312");
        @SuppressWarnings("resource")
        CSVReader csvReader = new CSVReader(isr);

        List<String[]> csvlist = csvReader.readAll();

        String[] titles = csvlist.get(0);

        if (titles.length != templateColumns.size()) {
//            throw new ServiceException(SysRespCodeEnum.PARAMS_ERR.code, "导入文件列和模板不一致!请重新下载模板!");
        }

        for (String title : titles) {
            if (!templateColumns.contains(title)) {
//                throw new ServiceException(SysRespCodeEnum.PARAMS_ERR.code, "导入文件列和模板列数据不一致!");
            }
        }

        List<Map<String, String>> list = Lists.newArrayListWithCapacity(csvlist.size() - 1);
        for (int i = 1; i < csvlist.size(); i++) {
            Map<String, String> map = Maps.newHashMapWithExpectedSize(titles.length);
            String[] s = csvlist.get(i);

            for (int j = 0; j < s.length; j++) {
                map.put(columnMap.get(titles[j]), StringUtils.trim(s[j]));
            }

            list.add(map);
        }

        return list;
    }


    /**
     * 往文件添加错误信息
     * 
     * @param is
     * @param messages
     * @param file
     * @throws IOException
     */
    public static void appendCsvMessages(InputStream is, List<String> messages, File file) throws IOException {
        InputStreamReader isr = new InputStreamReader(is, "GB2312");
        @SuppressWarnings("resource")
        CSVReader csvReader = new CSVReader(isr);

        List<String[]> csvlist = csvReader.readAll();
        String[] titles = csvlist.get(0);
        StringBuilder sb = new StringBuilder();

        for (String title : titles) {
            sb.append(title + ",");
        }

        sb.append("错误内容");

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "GBK"), 1024);) {
            bw.write(sb.toString());
            bw.newLine();

            for (int i = 1; i < csvlist.size(); i++) {
                sb.setLength(0);
                for (String s : csvlist.get(i)) {
                    sb.append(s + ",");
                }

                sb.append(messages.get(i - 1));
                bw.write(sb.toString());
                bw.newLine();
            }

            bw.flush();
        }
    }
}

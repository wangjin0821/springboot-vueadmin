package com.wiggin.mangersys.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.wiggin.mangersys.constant.FileTypeEnum;
import com.wiggin.mangersys.util.report.vo.ExportVO;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import net.sf.jasperreports.export.SimpleXmlExporterOutput;
import net.sf.jasperreports.export.WriterExporterOutput;


/**
 * Jasper 工具类
 * 
 * @author weiem
 *
 */
public class JasperHelperUtil {

    /**
     * 创建JRAbstractExporter
     * 
     * @param fileTypeEnum
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static JRAbstractExporter getJRExporter(FileTypeEnum fileTypeEnum) {
        JRAbstractExporter exporter = null;

        switch (fileTypeEnum) {
        case XLS:
            exporter = new JRXlsExporter();
            break;
        case XLSX:
            exporter = new JRXlsxExporter();
            break;
        case CSV:
            exporter = new JRCsvExporter();
            break;
        }

        return exporter;
    }


    /**
     * 生成文件内容
     * 
     * @param fileUri
     * @param listMap
     * @param list
     * @return
     * @throws Exception
     */
    public static JasperPrint getJasperPrint(String fileUri, List<Map<String, Object>> listMap, List<ExportVO> list) throws Exception {
        Map<String, Object> parameters = Maps.newHashMap();

        String reportModelFile = System.getProperty("user.dir") + "/src/main/resources/config/template/jasper/" + fileUri + ".jasper";

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new FileInputStream(reportModelFile));
        Map<String, Object> title = listMap.get(0);

        for (ExportVO exportVO : list) {
            title.put(exportVO.getField() + "Title", exportVO.getTitle());
        }

        JRDataSource dataSource = new JRBeanCollectionDataSource(listMap, false);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        return jasperPrint;
    }


    /**
     * 设置Jasper配置
     * 
     * @param exporter
     * @param fileType
     */
    @SuppressWarnings({ "rawtypes", "unchecked", "incomplete-switch" })
    public static void setConfiguration(JRAbstractExporter exporter, Integer fileType) {
        FileTypeEnum fileTypeEnum = FileTypeEnum.getFileTypeEnum(fileType);
        switch (fileTypeEnum) {
        case XLS:
        case XLSX:
            exporter.setConfiguration(excelConfig());

            break;
        }
    }


    /**
     * excel去除空白
     * 
     * @return
     */
    public static SimpleXlsReportConfiguration excelConfig() {
        SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();

        // 删除记录最下面的空行
        configuration.setRemoveEmptySpaceBetweenRows(Boolean.TRUE);
        // 一页一个sheet
        configuration.setOnePagePerSheet(Boolean.FALSE);
        // 显示边框 背景白色
        configuration.setWhitePageBackground(Boolean.FALSE);

        return configuration;
    }


    /**
     * 将JRAbstractExporter转成file
     * 
     * @param exporter
     * @param fileTypeEnum
     * @param file
     * @throws IOException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void setOutputStream(JRAbstractExporter exporter, FileTypeEnum fileTypeEnum, File file) throws IOException {
        FileOutputStream fos = null;
        FileWriter fw = null;
        switch (fileTypeEnum) {
        case XLS:
        case XLSX:
            fos = new FileOutputStream(file);
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(fos));
            break;

        case CSV:
            fw = new FileWriter(file);
            WriterExporterOutput output = new SimpleXmlExporterOutput(fw);
            exporter.setExporterOutput(output);
            break;
        }
    }
}

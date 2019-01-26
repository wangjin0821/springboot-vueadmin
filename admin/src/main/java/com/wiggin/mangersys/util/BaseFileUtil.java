package com.wiggin.mangersys.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.google.common.collect.Lists;
import com.wiggin.mangersys.config.BusinessException;
import com.wiggin.mangersys.config.ExceptionCodeEnum;
import com.wiggin.mangersys.constant.FileTypeEnum;
import com.wiggin.mangersys.util.report.vo.BaseExportVO;
import com.wiggin.mangersys.util.report.vo.ExportVO;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.export.SimpleExporterInput;


/**
 * 文件工具类
 * 
 * @author weiem
 *
 */
@Slf4j
public class BaseFileUtil {

    private static final String PATH = "temp/temporaryfile";

    // 压缩文件缓存大小
    public static final int ZIP_BUFFER = 1024;


    /**
     * 导出生成文件
     * 
     * @param baseExportVO
     * @param list
     * @param fieldType
     * @return
     * @throws Exception
     */
    public static File generateFile(BaseExportVO baseExportVO, List<Map<String, Object>> list, Map<String, String> fieldType, List<ExportVO> exportVOs, File file, String sheetName)
            throws Exception {
        if (baseExportVO != null) {
            Integer fileType = ObjectUtils.defaultIfNull(baseExportVO.getFileType(), 1);
            FileTypeEnum fileTypeEnum = FileTypeEnum.getFileTypeEnum(fileType);

            try {
                if (StringUtils.isNotBlank(baseExportVO.getTemplateName())) {
                    @SuppressWarnings("rawtypes")
                    JRAbstractExporter exporter = JasperHelperUtil.getJRExporter(fileTypeEnum);
                    exporter.setExporterInput(new SimpleExporterInput(
                        JasperHelperUtil.getJasperPrint(baseExportVO.getTemplateName(), list, JSONArray.parseArray(baseExportVO.getList(), ExportVO.class))));
                    JasperHelperUtil.setConfiguration(exporter, fileType);

                    JasperHelperUtil.setOutputStream(exporter, fileTypeEnum, file);
                    exporter.exportReport();

                    return file;
                }

                exportWrite(baseExportVO, list, file, fieldType, exportVOs, sheetName);
            } catch (Exception e) {
                throw new BusinessException(ExceptionCodeEnum.EXPORT_DATA_ERROR_CODE.getCode(), "生成导出文件失败:" + e.getMessage());
            } finally {
                file.deleteOnExit();
            }

            return file;
        }

        return null;
    }


    /**
     * 创建文件
     * 
     * @param fileName
     * @return
     * @throws IOException
     */
    public static File createFile(String fileName) throws IOException {
        String dir = System.getProperty("java.io.tmpdir");
        String path = dir + "/" + PATH;
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
        File file = new File(f, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }

        return file;
    }


    /**
     * 将数据写入文件
     * 
     * @param baseExportVO
     * @param list
     * @param file
     * @param fieldType
     * @return
     * @throws IOException
     */
    public static File exportWrite(BaseExportVO baseExportVO, List<Map<String, Object>> list, File file, Map<String, String> fieldType, List<ExportVO> exportVOs, String sheetName)
            throws IOException {
        FileTypeEnum fileTypeEnum = FileTypeEnum.getFileTypeEnum(baseExportVO.getFileType());

        switch (fileTypeEnum) {
        case XLS:
        case XLSX:
            ExcelUtil.createExcel(list, fieldType, file, fileTypeEnum, exportVOs, sheetName);
            break;

        case CSV:
            CsvUtil.createCsv(list, file, exportVOs);
            break;
        }

        return file;
    }


    /**
     * 在导入文件中加入错误信息并生成新文件
     * 
     * @param is
     * @param suffix
     * @param messages
     * @param sheet
     * @return
     * @throws IOException
     */
    public static File generateMessagesFile(InputStream is, String suffix, List<String> messages, String sheet) throws IOException {
        File file = createFile(IdWorker.getId() + "." + suffix);
        if (StringUtils.equals(suffix, "csv")) {
            CsvUtil.appendCsvMessages(is, messages, file);
        } else {
            ExcelUtil.appendExcelMessages(is, suffix, messages, file, sheet);
        }

        return file;
    }


    /**
     * 生成导入失败文件
     * 
     * @param baseExportVO
     * @param list
     * @param fieldType
     * @param exportVOs
     * @return
     * @throws Exception
     */
    public static void generateImportFailFile(BaseExportVO baseExportVO, List<Map<String, Object>> list, Map<String, String> fieldType, List<ExportVO> exportVOs, File file,
            String sheetName) throws Exception {
        if (baseExportVO != null) {
            try {
                exportWrite(baseExportVO, list, file, fieldType, exportVOs, sheetName);
            } catch (Exception e) {
                file.delete();
                throw new BusinessException(ExceptionCodeEnum.EXPORT_DATA_ERROR_CODE.getCode(), "生成导入失败文件时发生错误:" + e.getMessage());
            }
        }
    }


    /**
     * 
     * @param zipFileName
     * @param filePaths
     * @return
     * @throws Exception
     */
    public static File zipFileName(String zipFileName, List<String> filePaths) throws Exception {
        List<File> files = Lists.newArrayListWithCapacity(filePaths.size());

        for (String path : filePaths) {
            File file = new File(path);
            files.add(file);
        }

        return zipFile(zipFileName, files);
    }


    /**
     * 
     * @param zipFileName
     * @param files
     * @return
     * @throws Exception
     */
    public static File zipFile(String zipFileName, List<File> files) throws Exception {
        if (!CollectionUtils.isEmpty(files)) {
            File zipFile = createFile(zipFileName);

            try (FileOutputStream fileOutputStream = new FileOutputStream(zipFile); ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);) {
                // 创建 ZipEntry 对象
                ZipEntry zipEntry = null;

                for (File file : files) {
                    try (FileInputStream fileInputStream = new FileInputStream(file);) {
                        zipEntry = new ZipEntry(file.getName());
                        zipOutputStream.putNextEntry(zipEntry);

                        // 该变量记录每次真正读的字节个数
                        int len;
                        // 定义每次读取的字节数组
                        byte[] buffer = new byte[1024];
                        while ((len = fileInputStream.read(buffer)) > 0) {
                            zipOutputStream.write(buffer, 0, len);
                        }
                    }
                }

                zipOutputStream.closeEntry();

                return zipFile;
            } catch (IOException e) {
                zipFile.deleteOnExit();
                throw new BusinessException(ExceptionCodeEnum.EXPORT_DATA_ERROR_CODE.getCode(), "压缩文件失败:" + e.getMessage());
            }
        }

        return null;
    }


    /**
     * 遍历目录
     * 
     * @param dir
     */
    public static void listDirectory(File dir, Collection<String> filePathList) {
        if (!dir.exists()) {
            throw new IllegalArgumentException("目录" + dir + "不存在！");
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException(dir + "不是一个目录！");
        }
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return !".".equals(file.getName().substring(0, 1));
            }
        };
        File files[] = dir.listFiles(filter);
        boolean isAllFiles = true;
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    isAllFiles = false;
                    listDirectory(file, filePathList);
                }
            }
            if (isAllFiles) {
                filePathList.add(dir.getAbsolutePath());
            }
        }
    }


    public static void main(String[] args) {
        File dir = new File("E:\\picture");
        List<String> fileList = Lists.newLinkedList();
        BaseFileUtil.listDirectory(dir, fileList);
        log.info("size => {}, list => {}", fileList.size(), fileList);
        /*FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile() && "png".equals(file.getName().substring(file.getName().lastIndexOf(".") + 1));
            }
        };
        BaseFileUtil.dirMap.forEach((filePath, dir) -> {
            if (StringUtils.lastIndexOf(filePath, "UserInfo") > 0) {
                File[] listFiles = dir.listFiles(filter);
                log.info("listFiles => {}, {}", listFiles.length, JSON.toJSONString(listFiles));
            }
        });*/
    }
}

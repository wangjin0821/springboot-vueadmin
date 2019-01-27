package com.wiggin.mangersys.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wiggin.mangersys.constant.FileTypeEnum;
import com.wiggin.mangersys.util.report.vo.ExportVO;


/**
 * excel工具类
 * 
 * @author weiem
 *
 */
public class ExcelUtil {

    private static Workbook WB;
    
    private static final short rowHeight = 100 * 25;
    
    private static final int columnWidth = 25 * 256;
    
    /**
     * 创建excel文件
     * 
     * @param baseExportVO
     * @param list
     * @param fieldType
     * @param file
     * @param fileTypeEnum
     * @throws IOException
     */
    public static void createExcel(List<Map<String, Object>> list, Map<String, String> fieldType, File file, FileTypeEnum fileTypeEnum, List<ExportVO> exportVOs, String sheetName)
            throws IOException {

        if (file != null) {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                if (fileTypeEnum == FileTypeEnum.XLSX) {
                    WB = new XSSFWorkbook(fileInputStream);
                } else {
                    WB = new HSSFWorkbook(fileInputStream);
                }
            } catch (Exception e) {
                if (fileTypeEnum == FileTypeEnum.XLSX) {
                    WB = new XSSFWorkbook();
                } else {
                    WB = new HSSFWorkbook();
                }
            }
        } else {
            if (fileTypeEnum == FileTypeEnum.XLSX) {
                WB = new XSSFWorkbook();
            } else {
                WB = new HSSFWorkbook();
            }
        }
        
        Sheet sheet = WB.getSheet(sheetName);

        CellStyle contextstyle = WB.createCellStyle();
        DataFormat format = WB.createDataFormat();
        int line = 0;
        if (sheet == null) {
            if (StringUtils.isNotBlank(sheetName)) {
                sheet = WB.createSheet(sheetName);
            } else {
                sheet = WB.createSheet();
            }
            
            line = createTitles(sheet, exportVOs);
        } else {
            line = sheet.getLastRowNum();
        }
        Map<String, BigDecimal> map = createContent(list, exportVOs, line, sheet, contextstyle, format, fieldType);

        createSum(exportVOs, map, sheet, contextstyle);
        try (FileOutputStream fos = new FileOutputStream(file);) {
            WB.write(fos);
        }

        WB.close();
    }


    /**
     * 创建表头
     * 
     * @param titles
     * @param row
     * @return 返回下一行的行数 如复杂的导出格式需要重新或者重新修改兼容
     */
    public static int createTitles(Sheet sheet, List<ExportVO> exportVOs) {
        Row row = sheet.createRow(0);
        Cell cell = null;

        CellStyle style = createDownImportTemplateTitleStyle(WB);
        style.setFont(createDownImportTemplateTitleFont(WB));

        for (int i = 0; i < exportVOs.size(); i++) {
            sheet.setColumnWidth(i, 5000);
            cell = row.createCell(i);
            cell.setCellStyle(style);
            cell.setCellValue(exportVOs.get(i).getTitle());
        }

        return 1;
    }


    /**
     * 创建数据网格
     * 
     * @param format
     * @param fieldType
     * @param titles
     * @param row
     * @return 返回下一行的行数 如复杂的导出格式需要重新或者重新修改兼容
     */
    public static Map<String, BigDecimal> createContent(List<Map<String, Object>> list, List<ExportVO> exportVOs, int line, Sheet sheet, CellStyle contextstyle, DataFormat format,
            Map<String, String> fieldType) {
        Map<String, BigDecimal> sum = Maps.newHashMap();

        if (list != null) {
            Drawing<?> patriarch = null;
            if (WB instanceof XSSFWorkbook) {
                patriarch = (XSSFDrawing) sheet.createDrawingPatriarch();
            } else if (WB instanceof HSSFWorkbook) {
                patriarch = (HSSFPatriarch) sheet.createDrawingPatriarch();
            }
            for (Map<String, Object> map : list) {
                Row row = sheet.createRow(line);
                for (int i = 0; i < exportVOs.size(); i++) {
                    ExportVO exportVO = exportVOs.get(i);

                    Object object = ObjectUtils.defaultIfNull(map.get(exportVO.getField()), "");
                    String value = String.valueOf(object);

                    /*
                     * 将内容按顺序赋给对应的列对象
                     */
                    Cell cell = row.createCell(i);

                    if (fieldType != null && StringUtils.isNotBlank(fieldType.get(exportVO.getField())) && StringUtils.isNotBlank(value)) {
                        String type = fieldType.get(exportVO.getField());
                        switch (type) {
                        case "java.lang.Integer":
                            contextstyle.setDataFormat(format.getFormat("0"));
                            cell.setCellValue(Integer.parseInt(value));
                            break;

                        case "java.math.BigDecimal":
                            contextstyle.setDataFormat(format.getFormat("0.00"));
                            cell.setCellValue(value);
                            break;
                            
                        case "specialDataConvert":
                        	if (object != null && patriarch != null) {
                        		sheet.setColumnWidth(i, columnWidth);
                            	row.setHeight(rowHeight);
                            	if (WB instanceof XSSFWorkbook) {
                                    XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0,(short) i, line, (short) (i + 1), line + 1);
                                    anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE); 
                                    patriarch.createPicture(anchor, WB.addPicture((byte[]) object, HSSFWorkbook.PICTURE_TYPE_JPEG));
                            	} else if (WB instanceof HSSFWorkbook) {
                                    HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0,(short) i, line, (short) (i + 1), line + 1);
                                    anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE); 
                                    patriarch.createPicture(anchor, WB.addPicture((byte[]) object, HSSFWorkbook.PICTURE_TYPE_JPEG));
                                }
                            	
                            	continue;
                        	}
                        	
                        default:
                            contextstyle.setDataFormat(format.getFormat("@"));
                            cell.setCellValue(String.valueOf(value));
                            break;
                        }
                    } else {
                        contextstyle.setDataFormat(format.getFormat("@"));
                        cell.setCellValue(String.valueOf(value));
                    }

                    cell.setCellStyle(contextstyle);

                    if (exportVO.isColumnSum()) {
                        sum.put(exportVO.getField(), ObjectUtils.defaultIfNull(sum.get(exportVO.getField()), new BigDecimal(0)).add(new BigDecimal(value)));
                    }
                }

                line++;
            }
        }

        sum.put("line", new BigDecimal(line));
        return sum;
    }


    /**
     * 创建汇总列
     * 
     * @param exportVOs
     * @param map
     * @param sheet
     * @param contextstyle
     */
    public static void createSum(List<ExportVO> exportVOs, Map<String, BigDecimal> map, Sheet sheet, CellStyle contextstyle) {
        int line = map.get("line").intValue();
        Row row = sheet.createRow(line);
        Cell cell = null;

        for (int i = 0; i < exportVOs.size(); i++) {
            ExportVO exportVO = exportVOs.get(i);

            if (map.get(exportVO.getField()) != null) {
                cell = row.createCell(i);
                cell.setCellValue("总和:" + map.get(exportVO.getField()).toString());
                cell.setCellStyle(contextstyle);
            }
        }
    }


    /**
     * 获取列名
     * 
     * @param sheet
     * @return
     */
    public static List<String> getColumns(Row row) {
        List<String> colums = Lists.newArrayList();
        if (row != null) {
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                cell.setCellType(CellType.STRING);
                String name = cell.getStringCellValue().trim();
                if (StringUtils.isBlank(name)) {
                    break;
                }

                colums.add(name);
            }
        }

        return colums;
    }


    /**
     * 添加列
     * 
     * @param row
     * @param size
     * @param value
     */
    public static void createCell(Row row, int size, String value) {
        Cell cell = row.createCell(size);
        cell.setCellValue(value);
    }


    /**
     * 将excel数据转成List<Map<String, String>>
     * 
     * @param suffix
     * @param sheet2
     * 
     * @param row
     * @param size
     * @param value
     * @throws IOException
     */
    public static List<Map<String, String>> getExcelData(InputStream is, List<String> templateColumns, Map<String, String> columnMap, String suffix, String sheetName)
            throws IOException {
        if (StringUtils.equals(suffix, "xlsx")) {
            WB = new XSSFWorkbook(is);
        } else {
            WB = new HSSFWorkbook(is);
        }

        List<Map<String, String>> list = Lists.newArrayList();
        Sheet sheet = WB.getSheet(sheetName);

        if (sheet == null) {
//            throw new ServiceException(SysRespCodeEnum.PARAMS_ERR.code, "sheet为空请查看文件是否正确!");
        }

        List<String> columns = getColumns(sheet.getRow(0));
        StringBuilder sb = new StringBuilder();

        if (columns.size() != templateColumns.size()) {
//            throw new ServiceException(SysRespCodeEnum.PARAMS_ERR.code, "导入文件列和模板不一致!请重新下载模板!");
        }

        for (String column : columns) {
            if (!templateColumns.contains(column)) {
//                throw new ServiceException(SysRespCodeEnum.PARAMS_ERR.code, "导入文件列和模板不一致!请重新下载模板!");
            }
        }

        HSSFDataFormatter df = new HSSFDataFormatter();
        for (int j = 1; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            if (row == null) {
                break;
            }

            Map<String, String> map = Maps.newHashMap();

            for (int n = 0; n < columns.size(); n++) {
                Cell cell = row.getCell(n);

                String value = null;
                String column = columns.get(n);

                if (cell == null || StringUtils.isBlank(df.formatCellValue(cell))) {
                    value = null;
                } else {
                    value = df.formatCellValue(cell);
                }

                map.put(columnMap.get(column), StringUtils.trim(value));
            }

            sb.setLength(0);
            for (Entry<String, String> entry : map.entrySet()) {
                if (StringUtils.isNotBlank(entry.getValue())) {
                    sb.append(entry.getValue());
                }
            }

            if (StringUtils.isBlank(String.valueOf(sb))) {
                break;
            }

            list.add(map);
        }

        WB.close();

        return list;

    }


    /**
     * 将导入文件加上错误信息并写入新文件中
     * 
     * @param is
     * @param suffix
     * @param messages
     * @param file
     * @param sheetName
     * @throws IOException
     */
    public static void appendExcelMessages(InputStream is, String suffix, List<String> messages, File file, String sheetName) throws IOException {
        if (StringUtils.equals(suffix, "xlsx")) {
            WB = new XSSFWorkbook(is);
        } else {
            WB = new HSSFWorkbook(is);
        }

        Sheet sheet = WB.getSheet(sheetName);

        if (sheet == null) {
//            throw new ServiceException(SysRespCodeEnum.PARAMS_ERR.code, "sheet为空请查看文件是否正确!");
        }
        Row row = sheet.getRow(0);
        Integer maxRowLength = row.getPhysicalNumberOfCells();

        createCell(row, maxRowLength, "错误信息");

        for (int i = 1; i <= messages.size(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                break;
            }

            createCell(row, maxRowLength, messages.get(i - 1));
        }
        try (FileOutputStream fos = new FileOutputStream(file);) {

            WB.write(fos);
        }

        WB.close();
    }


    /**
     * 创建导出模板表头字体
     * 
     * @param titles
     * @param row
     * @return 返回下一行的行数 如复杂的导出格式需要重新或者重新修改兼容
     */
    public static Font createDownImportTemplateTitleFont(Workbook wb) {
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);

        return font;
    }


    /**
     * 创建导出模板表头样式
     * 
     * @param titles
     * @param row
     * @return 返回下一行的行数 如复杂的导出格式需要重新或者重新修改兼容
     */
    public static CellStyle createDownImportTemplateTitleStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();

        DataFormat format = wb.createDataFormat();

        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER); // 居中
        style.setDataFormat(format.getFormat("@"));

        return style;
    }


    /**
     * 创建导出模板内容样式
     * 
     * @param titles
     * @param row
     * @return 返回下一行的行数 如复杂的导出格式需要重新或者重新修改兼容
     */
    public static CellStyle createDownImportTemplateContentStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();

        DataFormat format = wb.createDataFormat();

        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER); // 居中
        style.setDataFormat(format.getFormat("@"));

        return style;
    }
}

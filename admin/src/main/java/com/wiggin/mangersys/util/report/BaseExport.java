package com.wiggin.mangersys.util.report;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wiggin.mangersys.config.BusinessException;
import com.wiggin.mangersys.config.ExceptionCodeEnum;
import com.wiggin.mangersys.config.ReportProperties;
import com.wiggin.mangersys.constant.ExportAndImportEnum;
import com.wiggin.mangersys.constant.FileTypeEnum;
import com.wiggin.mangersys.util.BaseFileUtil;
import com.wiggin.mangersys.util.BeanUtil;
import com.wiggin.mangersys.util.Page;
import com.wiggin.mangersys.util.SpringUtil;
import com.wiggin.mangersys.util.report.util.DataConversionUtil;
import com.wiggin.mangersys.util.report.util.GenerateMyFileData;
import com.wiggin.mangersys.util.report.vo.BaseExportVO;
import com.wiggin.mangersys.util.report.vo.ExportVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * 导出
 * 
 * @author weiem
 * 
 */
@Api
public interface BaseExport {

    // 分页查询每页的条数
    int PAGE_SIZE = 2000;


    /**
     * 导出
     * 
     * @author weiem
     * @param t
     * @return
     */
    @ApiOperation("导出")
    @GetMapping("/export")
    default void export(@Valid @ModelAttribute("baseExportVO") BaseExportVO baseExportVO) throws Exception {
        if (StringUtils.isBlank(StringUtils.substring(baseExportVO.getList(), 1, baseExportVO.getList().length() - 1))) {
            throw new BusinessException(ExceptionCodeEnum.EXPORT_DATA_ERROR_CODE.getCode(), "导出列信息不能为空");
        }

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String token = request.getHeader("x-auth-token");

        if (StringUtils.isBlank(baseExportVO.getFileName())) {
            baseExportVO.setFileName("");
        }

        if (baseExportVO.getFileType() == null) {
            baseExportVO.setFileType(FileTypeEnum.XLSX.getCode());
        }

        //Long companyId = null;

        /*if (baseExportVO.getCompanyId() != null) {
            WorkbenchEmployeeService workbenchEmployeeService = SpringUtil.getContext().getBean(WorkbenchEmployeeService.class);
            if (workbenchEmployeeService.getUserCompanyIdIsExist(SessionUtil.getUserId(), baseExportVO.getCompanyId(), token) <= 0) {
                throw new ServiceException(SysRespCodeEnum.PARAMS_ERR.code, "用户不属于该公司");
            }

            companyId = baseExportVO.getCompanyId();
        } else if (StringUtil.isNotBlank(SessionUtil.getCompanyId())) {
            companyId = Long.valueOf(SessionUtil.getCompanyId());
        }*/

        if (baseExportVO.isAsync()) {
            //TODO wiggin
            /*if (baseExportVO.getSystemId() == null) {
                throw new ServiceException(SysRespCodeEnum.PARAMS_ERR.code, "异步导出系统ID不能为空!");
            }*/
            
            GenerateMyFileData generateMyFileData = SpringUtil.getContext().getBean(GenerateMyFileData.class);
            Long fileId = null;
            //Long fileId = generateMyFileData.insertWorkbenchMyFile(baseExportVO, companyId, token);

            if (fileId == null) {
//                throw new ServiceException(SysRespCodeEnum.PARAMS_ERR.code, "我的下载生成数据失败!");
            }

            /*AsyncTask asyncTask = SpringUtil.getContext().getBean(AsyncTask.class);
//            UserInfo userInfo = SessionUtil.currentUser();
            asyncTask.myTaskAsyncPool(fileId, baseExportVO, this.getClass(), userInfo, token);*/

            return;
        }
        HttpServletResponse response = servletRequestAttributes.getResponse();

        File file = getExportFile(baseExportVO, token);

        setResponseHeader(response, file.getName());
//        ResponseUtil.setResponseHeader(response, file.getName());
        try (InputStream is = new FileInputStream(file); OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());) {
            byte[] buffer = new byte[is.available()];
            int len = is.read(buffer);

            outputStream.write(buffer, 0, len);
            outputStream.flush();
        }

        file.deleteOnExit();
    }


    default File getExportFile(BaseExportVO baseExportVO, String token) throws Exception {
        List<ExportVO> exportVOs = null;
        try {
            exportVOs = BeanUtil.parseArray(baseExportVO.getList(), ExportVO.class);
        } catch (Exception e) {
            throw new BusinessException(ExceptionCodeEnum.EXPORT_DATA_ERROR_CODE.getCode(), "参数list错误,必须为:[{field:\"name\",title:\"名字\",columnSum:false}]格式,columnSum非必传!");
        }

        if (CollectionUtils.isEmpty(exportVOs)) {
            throw new BusinessException(ExceptionCodeEnum.EXPORT_DATA_ERROR_CODE.getCode(), "参数list不能为空!");
        }

        return getExportData(baseExportVO, exportVOs, token);
    }


    /**
     * 获取导出数据
     * 
     * @param baseExportVO
     * @return
     * @throws Exception
     */
    default File getExportData(BaseExportVO baseExportVO, List<ExportVO> exportVOs, String token) throws Exception {
        Set<String> fields = Sets.newHashSet();
        Integer fileType = baseExportVO.getFileType();
        ReportProperties rp = SpringUtil.getContext().getBean(ReportProperties.class);
        FileTypeEnum fileTypeEnum = FileTypeEnum.getFileTypeEnum(fileType);

        int numberCyclesPerCycle = rp.getFileMaxSize() / rp.getNumberRowsPerCycle();
        Set<String> filePaths = Sets.newHashSetWithExpectedSize(numberCyclesPerCycle);

        File file = BaseFileUtil.createFile(baseExportVO.getFileName() + IdWorker.getId() + "." + StringUtils.lowerCase(fileTypeEnum.name()));
        filePaths.add(file.getPath());

        for (ExportVO exportVO : exportVOs) {
            fields.add(exportVO.getField());
        }

        fields = filterField(fields);

        Map<String, Object> parameter = StringUtils.isBlank(baseExportVO.getParameter()) ? Maps.newHashMap() : JSON.parseObject(baseExportVO.getParameter());

        Pagination pagination = new Pagination(1, 1);
        parameter.put("pagination", pagination);

        try {
            Page<?> page = getExportList(parameter);

            if (page != null && page.getTotalCount() > 0) {
                List<?> list = page.getList();

                Map<String, Map<String, String>> formatterTypeMap = DataConversionUtil.getDataConversion(list.get(0), ExportAndImportEnum.EXPORT, token);
                List<Map<String, Object>> sqlData = Lists.newArrayListWithCapacity(rp.getNumberRowsPerCycle());

                BigDecimal pageTotal = new BigDecimal(page.getTotalCount());
                BigDecimal pageSize = new BigDecimal(PAGE_SIZE);
                BigDecimal pageNumber1 = pageTotal.divide(pageSize, 6, BigDecimal.ROUND_FLOOR);
                int pageNumber = (int) Math.ceil(pageNumber1.doubleValue());
//                int pageNumber = PageUtil.getPageNumber(page.getTotalCount(), PAGE_SIZE);
                pagination.setSize(PAGE_SIZE);

                int j = 0;

                for (int i = 0; i < pageNumber; i++) {
                    pagination.setCurrent(i + 1);
                    page = getExportList(parameter);

                    sqlData.addAll(getListToMap(page.getList(), fields, baseExportVO.getDateType(), formatterTypeMap));
                    if (sqlData.size() >= rp.getNumberRowsPerCycle() || i + 1 == pageNumber) {
                        try {
                            if (j >= numberCyclesPerCycle) {
                                file = BaseFileUtil.createFile(baseExportVO.getFileName() + IdWorker.getId() + "." + StringUtils.lowerCase(fileTypeEnum.name()));

                                filePaths.add(file.getPath());
                                j = 0;
                            }

                            BaseFileUtil.generateFile(baseExportVO, sqlData, formatterTypeMap.get("fieldType"), exportVOs, file, "Sheet1");
                            sqlData.clear();
                            j++;
                        } catch (Exception e) {
                            file.deleteOnExit();
                            throw new BusinessException(ExceptionCodeEnum.EXPORT_DATA_ERROR_CODE.getCode(), e.getMessage());
                        }
                    }
                }
            }

            page = null;
        } catch (Exception e) {
            for (String path : filePaths) {
                file = new File(path);
                file.deleteOnExit();
            }
            throw new BusinessException(ExceptionCodeEnum.EXPORT_DATA_ERROR_CODE.getCode(), e.getMessage());
        }

        List<String> pathList = new ArrayList<>(filePaths);

        if (filePaths.size() > 1) {
            String zipName = baseExportVO.getFileName() + IdWorker.getId() + ".zip";

            return BaseFileUtil.zipFileName(zipName, pathList);
        }

        return new File(pathList.get(0));
    }


    /**
     * 查询导出数据 (需要自己重写)
     * 
     * @param parameter
     * @return
     */
    Page<?> getExportList(Map<String, Object> parameter);


    /**
     * 将list数据转换成map
     * 
     * @param list
     * @param fields
     * @param dateType
     * @param formatterTypeMap
     * @return
     * @throws Exception
     */
    default List<Map<String, Object>> getListToMap(List<?> list, Set<String> fields, String dateType, Map<String, Map<String, String>> formatterTypeMap) throws Exception {
        List<Map<String, Object>> listMap = Lists.newArrayListWithCapacity(list.size());
        Map<String, String> formatterType = null;
        DateFormat bf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Object object : list) {
            Map<String, Object> map = Maps.newHashMap();
            @SuppressWarnings("unchecked")
            Map<Object, Object> beanMap = org.springframework.cglib.beans.BeanMap.create(object);

            for (Entry<Object, Object> entry : beanMap.entrySet()) {
                Object key = entry.getKey();
                if (!fields.contains(key)) {
                    continue;
                }

                Object value = entry.getValue();
                if (value == null)
                    value = "";

                if (formatterTypeMap.get(key) != null) {
                    formatterType = formatterTypeMap.get(key);

                    if (formatterType.get(String.valueOf(value)) != null) {
                        value = formatterType.get(String.valueOf(value));
                    } else if (value instanceof Boolean && formatterType.get((boolean) value ? "1" : "0") != null) {
                        value = formatterType.get((boolean) value ? "1" : "0");
                    }
                }

                if (value != null && value != "") {
                    String type = value.getClass().getName();

                    switch (type) {
                    case "java.util.Date":
                        if (StringUtils.isNotBlank(dateType)) {
                            bf = new SimpleDateFormat(dateType);
                        }

                        value = bf.format(value);
                        break;

                    case "java.math.BigDecimal":
                        BigDecimal b = new BigDecimal(String.valueOf(value));
                        value = b.setScale(2, BigDecimal.ROUND_HALF_DOWN);

                        break;
                    }
                }

                map.put(String.valueOf(key), value);
            }

            listMap.add(filterData(map));
        }

        list.clear();
        return listMap;
    }


    /**
     * 导出列过滤
     * 
     * @param fields
     * @return
     */
    default Set<String> filterField(Set<String> fields) {
        return fields;
    }


    /**
     * 数据过滤
     * 
     * @param map
     * @return
     */
    default Map<String, Object> filterData(Map<String, Object> map) {
        return map;
    }
    
    
    /**
     * 设置下载response 格式
     * 
     * @param response
     * @param fileName
     * @throws UnsupportedEncodingException
     */
    default public void setResponseHeader(HttpServletResponse response, String fileName) throws UnsupportedEncodingException {
        fileName = new String(fileName.getBytes(), "ISO8859-1");
        
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition,Ajax-Download,Ajax-Download-File");
        response.setContentType("application/octet-stream;charset=ISO8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setHeader("Ajax-Download", "true");
        response.setHeader("Ajax-Download-File", fileName);
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
    }
}
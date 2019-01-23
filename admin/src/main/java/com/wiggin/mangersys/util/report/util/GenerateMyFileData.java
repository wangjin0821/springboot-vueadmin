package com.wiggin.mangersys.util.report.util;

import org.springframework.stereotype.Component;


/**
 * 生成我的下载文件数据
 * 
 * @author weiem
 *
 */
@Component
public class GenerateMyFileData {

   /* @Autowired
    WorkbenchService workbenchService;


    *//**
     * 插入MyFile数据
     * 
     * @param baseExportVO
     * @param token
     * @return
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     *//*
    public Long insertWorkbenchMyFile(BaseExportVO baseExportVO, Long companyId, String token) {
        InsertWorkbenchMyFileVO workbenchMyFile = new InsertWorkbenchMyFileVO();
        workbenchMyFile.setCompanyId(companyId);

        String fileName = baseExportVO.getFileName();
        if (StringUtil.isBlank(fileName)) {
            fileName = IdWorker.getIdStr();
        }

        workbenchMyFile.setFileName(fileName);
        workbenchMyFile.setSystemId(baseExportVO.getSystemId());
        workbenchMyFile.setFileType(baseExportVO.getFileType());
        workbenchMyFile.setStatus(1);

        return workbenchService.insertWorkbenchMyFile(workbenchMyFile, token);
    }


    *//**
     * 更新MyFile数据
     * 
     * @param userId
     * 
     * @param baseExportVO
     * @return
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     *//*
    public int updateWorkbenchMyFile(Long fileId, Integer status, Long exportFileId, String token, Integer fileType) {
        UpdateWorkbenchMyFileVO updateWorkbenchMyFileVO = new UpdateWorkbenchMyFileVO();

        updateWorkbenchMyFileVO.setFileId(fileId);
        updateWorkbenchMyFileVO.setStatus(status);
        updateWorkbenchMyFileVO.setExportFileId(exportFileId);

        if (fileType != null) {
            updateWorkbenchMyFileVO.setFileType(fileType);
        }

        return workbenchService.updateWorkbenchMyFileStatus(updateWorkbenchMyFileVO, token);
    }*/
}

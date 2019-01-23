package com.wiggin.mangersys.constant;

/**
 * 文件类型枚举
 * 
 * @author weiem
 *
 */
public enum ExportAndImportEnum {

    EXPORT(1),
    IMPORT(2);

    public Integer code;


    private ExportAndImportEnum(Integer code) {
        this.code = code;
    }


    public static ExportAndImportEnum getFileTypeEnum(Integer code) {
        for (ExportAndImportEnum fileTypeEnum : ExportAndImportEnum.values()) {
            if (fileTypeEnum.code.equals(code)) {
                return fileTypeEnum;
            }
        }

        return null;
    }
}

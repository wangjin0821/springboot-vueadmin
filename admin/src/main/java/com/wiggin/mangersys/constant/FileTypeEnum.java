package com.wiggin.mangersys.constant;

import lombok.Getter;


/**
 * 文件类型枚举
 * 
 * @author weiem
 *
 */
@Getter
public enum FileTypeEnum {

    XLS(1),
    XLSX(2),
    CSV(3);

    Integer code;


    private FileTypeEnum(Integer code) {
        this.code = code;
    }


    public static FileTypeEnum getFileTypeEnum(Integer code) {
        for (FileTypeEnum fileTypeEnum : FileTypeEnum.values()) {
            if (fileTypeEnum.code.equals(code)) {
                return fileTypeEnum;
            }
        }

        return null;
    }
}
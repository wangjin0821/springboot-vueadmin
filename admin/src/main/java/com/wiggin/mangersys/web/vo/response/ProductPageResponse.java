package com.wiggin.mangersys.web.vo.response;

import com.wiggin.mangersys.domain.entity.Product;
import com.wiggin.mangersys.util.report.annotation.ImportAndExportDataConversion;
import com.wiggin.mangersys.util.report.util.ConverUrlToImageData;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class ProductPageResponse extends Product {

    private static final long serialVersionUID = -6504543445100658721L;

    private String productTitle;

    private String productTtileEn;

    private String saleStatusText;

    private String picturePath;

    private String pictureUrl;

    @ImportAndExportDataConversion(specialDataConvert = true, conversionClass = ConverUrlToImageData.class)
    private String pictureData;
}

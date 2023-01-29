package ru.sir.richard.boss.model.data.report;

import lombok.Data;
import org.apache.poi.hpsf.Date;

@Data
public class TempProductSalesReportBean {
    private int productId;
    private int quantity;
    private int categoryProductId;
    private String sku;
    private String name;

    // oi.quantity, oi.product_id, min(o.category_product_id) category_product_id, min(p.sku) sku, min(p.name) name


}

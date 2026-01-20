package com.rgbconsulting.prestashop.mapper;

/**
 *
 * @author sergi
 */
public class StockMapper {

    private String stock_id;
    private String id_product;
    private String quantity;
    private String out_of_stock;

    public StockMapper() {
        stock_id = "";
        id_product = "";
        quantity = "";
        out_of_stock = "";
    }

    public StockMapper(String stock_id, String id_product, String quantity) {
        this.stock_id = stock_id;
        this.id_product = id_product;
        this.quantity = quantity;
        
        if (quantity.equals("0")) {
            this.out_of_stock = "2";
        } else {
            this.out_of_stock = "1";
        }
    }

    public String xmlStock() {
        return "<prestashop xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n"
                + "  <stock_available>\n"
                + "    <id>" + this.stock_id + "</id>\n"
                + "    <id_product>" + this.id_product + "</id_product>\n"
                + "    <id_product_attribute>" + 0 + "</id_product_attribute>\n"
                + "    <id_shop>" + 1 + "</id_shop>\n"
                + "    <id_shop_group>" + 0 + "</id_shop_group>\n"
                + "    <quantity>" + this.quantity + "</quantity>\n"
                + "    <depends_on_stock>" + 0 + "</depends_on_stock>\n"
                + "    <out_of_stock>" + this.out_of_stock + "</out_of_stock>\n"
                + "    <location><![CDATA[]]></location>\n"
                + "  </stock_available>\n"
                + "</prestashop>";
    }

    public String getStock_id() {
        return this.stock_id;
    }

    public String getId_product() {
        return this.id_product;
    }

    public String getQuantity() {
        return this.quantity;
    }

    public void setStock_id(String stock_id) {
        this.stock_id = stock_id;
    }

    public void setId_product(String id_product) {
        this.id_product = id_product;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}

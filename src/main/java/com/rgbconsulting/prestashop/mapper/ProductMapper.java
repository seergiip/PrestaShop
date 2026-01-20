package com.rgbconsulting.prestashop.mapper;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sergi
 */
public class ProductMapper {

    private String id_manufacturer;
    private String id_supplier;
    private String id_category_default;
    private String product_name;
    private float price;
    private long product_id;
    private List<String> categoryIds;
    private String product_type;
    private String reference;
    private float unit_price;

    public ProductMapper() {
        this.id_manufacturer = "";
        this.id_supplier = "";
        this.id_category_default = "";
        this.product_name = "";
        this.price = 0.0f;
        this.product_id = -1;
        this.categoryIds = new ArrayList<>();
        this.product_type = "standard";
        this.reference = "";
        this.unit_price = 0.0f;
    }

    public ProductMapper(String id_manufacturer,
            String id_supplier,
            String id_category_default,
            String product_name,
            float price,
            long product_id,
            List<String> categoryIds,
            String product_type,
            String reference,
            float unit_price) {
        this.id_manufacturer = id_manufacturer;
        this.id_supplier = id_supplier;
        this.id_category_default = id_category_default;
        this.product_name = product_name;
        this.price = price;
        this.product_id = product_id;
        this.categoryIds = categoryIds;
        this.product_type = product_type;
        this.reference = reference;
        this.unit_price = unit_price;
    }

    public String xmlProductPOST() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<prestashop xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n"
                + "<product>\n"
                + "    <id_manufacturer>" + this.id_manufacturer + "</id_manufacturer>\n"
                + "    <id_supplier>" + this.id_supplier + "</id_supplier>\n"
                + "    <id_category_default>" + this.id_category_default + "</id_category_default>\n"
                + "    <new><![CDATA[1]]></new>\n"
                + "    <id_default_combination><![CDATA[1]]></id_default_combination>\n"
                + "    <id_tax_rules_group><![CDATA[1]]></id_tax_rules_group>\n"
                + "    <type><![CDATA[1]]></type>\n"
                + "    <id_shop_default><![CDATA[1]]></id_shop_default>\n"
                + "    <reference>" + this.reference + "</reference>\n"
                + "    <supplier_reference><![CDATA[ABCDEF]]></supplier_reference>\n"
                + "    <ean13><![CDATA[1231231231231]]></ean13>\n"
                + "    <state><![CDATA[1]]></state>\n"
                + "    <product_type>" + this.product_type + "</product_type>\n"
                + "    <price>" + this.price + "</price>\n"
                + "    <unit_price>" + this.unit_price + "</unit_price>\n"
                + "    <active><![CDATA[1]]></active>\n"
                + "    <meta_description>\n"
                + "        <language id=\"1\"><![CDATA[Description]]></language>\n"
                + "    </meta_description>\n"
                + "    <meta_keywords>\n"
                + "        <language id=\"1\"><![CDATA[Keywords]]></language>\n"
                + "    </meta_keywords>\n"
                + "    <meta_title>\n"
                + "        <language id=\"1\"><![CDATA[My Title for SEO]]></language>\n"
                + "    </meta_title>\n"
                + "    <link_rewrite>\n"
                + "        <language id=\"1\"><![CDATA[awesome-product]]></language>\n"
                + "    </link_rewrite>\n"
                + "    <name>\n"
                + "        <language id=\"1\">" + this.product_name + "</language>\n"
                + "    </name>\n"
                + "    <description>\n"
                + "        <language id=\"1\"><![CDATA[Description]]></language>\n"
                + "    </description>\n"
                + "    <description_short>\n"
                + "        <language id=\"1\"><![CDATA[Short description]]></language>\n"
                + "    </description_short>\n"
                + "    <associations>\n"
                + "        <categories>\n"
                +               assignCategories()
                + "        </categories>\n"
                + "    </associations>\n"
                + "</product>\n"
                + "</prestashop>";
    }

    public String xmlProductPUT() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<prestashop xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n"
                + "<product>\n"
                + "    <id>" + this.product_id + "</id>\n"
                + "    <id_manufacturer>" + this.id_manufacturer + "</id_manufacturer>\n"
                + "    <id_supplier>" + this.id_supplier + "</id_supplier>\n"
                + "    <id_category_default>" + this.id_category_default + "</id_category_default>\n"
                + "    <new><![CDATA[1]]></new>\n"
                + "    <id_default_combination><![CDATA[1]]></id_default_combination>\n"
                + "    <id_tax_rules_group><![CDATA[1]]></id_tax_rules_group>\n"
                + "    <type><![CDATA[1]]></type>\n"
                + "    <id_shop_default><![CDATA[1]]></id_shop_default>\n"
                + "    <reference>" + this.reference + "</reference>\n"
                + "    <supplier_reference><![CDATA[ABCDEF]]></supplier_reference>\n"
                + "    <ean13><![CDATA[1231231231231]]></ean13>\n"
                + "    <state><![CDATA[1]]></state>\n"
                + "    <product_type><![CDATA[standard]]></product_type>\n"
                + "    <price>" + this.price + "</price>\n"
                + "    <unit_price><![CDATA[123.45]]></unit_price>\n"
                + "    <active><![CDATA[1]]></active>\n"
                + "    <meta_description>\n"
                + "        <language id=\"1\"><![CDATA[Description]]></language>\n"
                + "    </meta_description>\n"
                + "    <meta_keywords>\n"
                + "        <language id=\"1\"><![CDATA[Keywords]]></language>\n"
                + "    </meta_keywords>\n"
                + "    <meta_title>\n"
                + "        <language id=\"1\"><![CDATA[My Title for SEO]]></language>\n"
                + "    </meta_title>\n"
                + "    <link_rewrite>\n"
                + "        <language id=\"1\"><![CDATA[awesome-product]]></language>\n"
                + "    </link_rewrite>\n"
                + "    <name>\n"
                + "        <language id=\"1\">" + this.product_name + "</language>\n"
                + "    </name>\n"
                + "    <description>\n"
                + "        <language id=\"1\"><![CDATA[Description]]></language>\n"
                + "    </description>\n"
                + "    <description_short>\n"
                + "        <language id=\"1\"><![CDATA[Short description]]></language>\n"
                + "    </description_short>\n"
                + "    <associations>\n"
                + "        <categories>\n"
                +             assignCategories()
                + "        </categories>\n"
                + "    </associations>\n"
                + "</product>\n"
                + "</prestashop>";
    }

    private void setId_manufacturer(String id_manufacturer) {
        this.id_manufacturer = id_manufacturer;
    }

    private void setId_supplier(String id_supplier) {
        this.id_supplier = id_supplier;
    }

    private void setId_category_default(String id_category_default) {
        this.id_category_default = id_category_default;
    }

    private void setProductName(String product_name) {
        this.product_name = product_name;
    }

    private void setPrice(float price) {
        this.price = price;
    }

    private String assignCategories() {
        String xml = "";
        for (String id : this.categoryIds) {
            xml += "<category><id>" + id + "</id></category>";
        }
        return xml;
    }
    
    
}

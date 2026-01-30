package com.rgbconsulting.prestashop.controller;

import com.rgbconsulting.prestashop.common.odoo.model.ProductCategory;
import com.rgbconsulting.prestashop.common.odoo.model.ProductProduct;
import com.rgbconsulting.prestashop.common.odoo.model.ProductTemplate;
import com.rgbconsulting.prestashop.common.odoo.model.Stock;
import com.rgbconsulting.prestashop.model.CategoryOdooPrestashop;
import com.rgbconsulting.prestashop.rest.RestClientOdoo;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sergi
 */
public class ControllerOdoo {

    RestClientOdoo restClientOdoo = new RestClientOdoo();

    public List<ProductTemplate> getProductsTemplate() {
        List<ProductTemplate> products = null;
        try {
            products = restClientOdoo.getProductsTemplate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Stock> getStocks() {
        List<Stock> stocks = null;
        try {
            stocks = restClientOdoo.getStocks();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stocks;
    }

    public Double getQuantity(Integer product_id, List<Stock> stocks) {
        return restClientOdoo.getQuantity(product_id, stocks);
    }

    public List<ProductProduct> getProductsProduct() {
        List<ProductProduct> products = null;
        try {
            products = restClientOdoo.getProductsProduct();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<ProductCategory> getProductsCategory() {
        List<ProductCategory> products = null;
        try {
            products = restClientOdoo.getProductsCategory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public File downloadImage(String imageUrl) {
        File image = null;
        try {
            image = restClientOdoo.downloadImage(imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public List<CategoryOdooPrestashop> getAllCategoriesOdooPrestashop() {
        List<CategoryOdooPrestashop> c = new ArrayList<>();
        try {
            c = restClientOdoo.getAllCategoriesOdooPrestashop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public void insertCategoryOdooPrestashop(Integer odoo_id,
            Integer prestashop_id,
            String odoo_name,
            String prestashop_name,
            String id_parent,
            String active) {
        try {
            restClientOdoo.insertCategoryOdooPrestashop(odoo_id, prestashop_id, odoo_name, prestashop_name, id_parent, active);
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }
}

package com.rgbconsulting.prestashop.controller;

import com.rgbconsulting.prestashop.common.odoo.model.Product;
import com.rgbconsulting.prestashop.rest.RestClientOdoo;
import java.util.List;

/**
 *
 * @author sergi
 */
public class ControllerOdoo {
    RestClientOdoo restClientOdoo = new RestClientOdoo();
    
    public List<Product> getProducts() {
        List<Product> products = null;
        try {
            products = restClientOdoo.getProducts();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
}

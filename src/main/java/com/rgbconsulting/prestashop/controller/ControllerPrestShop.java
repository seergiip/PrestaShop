package com.rgbconsulting.prestashop.controller;

import com.rgbconsulting.prestashop.common.odoo.model.Product;
import com.rgbconsulting.prestashop.rest.RestClientPrestaShop;
import java.util.List;

/**
 *
 * @author sergi
 */
public class ControllerPrestShop {

    RestClientPrestaShop clientPrestaShop = new RestClientPrestaShop();

    public void getProductByIdResponse(String id) {
        clientPrestaShop.getProductbyId(clientPrestaShop.initClient(), id);
    }

    public List<Product> getAllProducts() {
        return clientPrestaShop.getAllProducts(clientPrestaShop.initClient());
    }

    public void uploadProduct(String xmlProduct) {
        clientPrestaShop.uploadProduct(clientPrestaShop.initClient(), xmlProduct);
    }

    public void updateProduct(String xmlProduct) {
        clientPrestaShop.updateProduct(clientPrestaShop.initClient(), xmlProduct);
    }

    public void deleteProductById(String id) {
        clientPrestaShop.deleteProductById(clientPrestaShop.initClient(), id);
    }
}

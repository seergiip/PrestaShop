package com.rgbconsulting.prestashop.controller;

import com.rgbconsulting.prestashop.rest.RestClientPrestaShop;

/**
 *
 * @author sergi
 */
public class ControllerPrestShop {

    RestClientPrestaShop clientPrestaShop = new RestClientPrestaShop();

    public void getProductByIdResponse(String id) {
        clientPrestaShop.getProductbyId(clientPrestaShop.initClient(), id);
    }

    public void getAllProducts() {
        clientPrestaShop.getAllProduct(clientPrestaShop.initClient());
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

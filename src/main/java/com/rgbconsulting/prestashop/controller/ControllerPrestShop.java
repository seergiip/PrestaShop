package com.rgbconsulting.prestashop.controller;

import com.rgbconsulting.prestashop.common.odoo.model.ProductTemplate;
import com.rgbconsulting.prestashop.mapper.CategoryMapper;
import com.rgbconsulting.prestashop.rest.RestClientPrestaShop;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

/**
 *
 * @author sergi
 */
public class ControllerPrestShop {

    RestClientPrestaShop clientPrestaShop = new RestClientPrestaShop();
    
    // Load Properties
    public ControllerPrestShop() {
        try {
            clientPrestaShop.loadProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public void getProductByIdResponse(String id) {
        clientPrestaShop.getProductbyId(clientPrestaShop.initClient(), id);
    }

    public List<ProductTemplate> getAllProducts() {
        return clientPrestaShop.getAllProducts(clientPrestaShop.initClient());
    }

    public HttpResponse uploadProduct(String xmlProduct) {
        return clientPrestaShop.uploadProduct(clientPrestaShop.initClient(), xmlProduct);
    }

    public HttpResponse updateProduct(String xmlProduct) {
        return clientPrestaShop.updateProduct(clientPrestaShop.initClient(), xmlProduct);
    }

    public void deleteProductById(String id) {
        clientPrestaShop.deleteProductById(clientPrestaShop.initClient(), id);
    }
    
    public HttpResponse uploadCategory(String xmlCategory) {
        return clientPrestaShop.uploadCategory(clientPrestaShop.initClient(), xmlCategory);
    }
    
    public String getCategoryId(HttpResponse<String> response) {
        return clientPrestaShop.getCategoryId(response);
    }
    
    public HttpResponse getAvailableStock(String id_product) {
        return clientPrestaShop.getAvailableStock(clientPrestaShop.initClient(), id_product);
    }
    
    public String getIdOfStock(HttpResponse response) {
        return clientPrestaShop.getIdOfStock(response);
    }
    
    public void uploadStock(String xmlStock) {
        clientPrestaShop.uploadStock(clientPrestaShop.initClient(), xmlStock);
    }
    
    public void updateStock(String xmlStock, String stock_id) {
        clientPrestaShop.updateStock(clientPrestaShop.initClient(), xmlStock, stock_id);
    }
    
    public String getProductIdFromResponse(HttpResponse response) {
        return clientPrestaShop.getProductIdFromResponse(response);
    }
    
    public HttpResponse getProductByReference(String reference) {
        return clientPrestaShop.getProductByReference(clientPrestaShop.initClient(), reference);
    }
    
    public HttpResponse getPrestaShopCategories () {
        return clientPrestaShop.getPrestaShopCategories(clientPrestaShop.initClient());
    }
    
    public List<CategoryMapper> getPrestaShopCategoriesToList(HttpResponse response) {
        return clientPrestaShop.getPrestaShopCategoriesToList(clientPrestaShop.getPrestaShopCategories(clientPrestaShop.initClient()));
    }
    
    public void uploadImage (File imageFile,
            String productReference) {
        try {
            clientPrestaShop.uploadImage(clientPrestaShop.initClient(), imageFile, productReference);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

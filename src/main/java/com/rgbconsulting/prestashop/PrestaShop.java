package com.rgbconsulting.prestashop;

import com.rgbconsulting.prestashop.controller.ControllerPrestShop;


/**
 *
 * @author sergi
 */
public class PrestaShop {

    public static void main(String[] args) {
        ControllerPrestShop cps = new ControllerPrestShop();
        
        // Get the product by id from the prestashop
        //cps.getProductByIdResponse("9");
        
        // Get all the products from the prestashop
        //cps.getAllProducts();
        
        // Upload a product to the prestashop
        //String xmlProduct = "";
        //cps.uploadProduct(xmlProduct);
        
        // Update a product from the prestashop
        cps.updateProduct();
        // Delete a product from the prestashop
        cps.deleteProductById("1");
    }
}

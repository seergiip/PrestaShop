package com.rgbconsulting.prestashop;

import com.rgbconsulting.prestashop.controller.ControllerOdoo;
import com.rgbconsulting.prestashop.controller.ControllerPrestShop;
import com.rgbconsulting.prestashop.mapper.ProductMapper;


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
        ProductMapper mapperPOST = new ProductMapper("1", "1", "4", "PANTACAS", 300, 22, "4", "standard");
        cps.uploadProduct(mapperPOST.xmlProductPOST());
        
        // Update a product from the prestashop
        //ProductMapper mapperPUT = new ProductMapper("1", "1", "1", "TEST", 300, 22);
        //cps.updateProduct(mapperPUT.xmlProductPUT());
        // Delete a product from the prestashop
        //cps.deleteProductById("1");
        
        
        // Agafar producte de odoo
        //ControllerOdoo controllerOdoo = new ControllerOdoo();
        
        //controllerOdoo.getProduct();
    }
}

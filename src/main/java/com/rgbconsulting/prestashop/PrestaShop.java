package com.rgbconsulting.prestashop;

import com.rgbconsulting.prestashop.common.odoo.model.Product;
import com.rgbconsulting.prestashop.controller.ControllerOdoo;
import com.rgbconsulting.prestashop.controller.ControllerPrestShop;
import com.rgbconsulting.prestashop.mapper.ProductMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sergi
 */
public class PrestaShop {

    public static void main(String[] args) {
        ControllerOdoo controllerOdoo = new ControllerOdoo();
        ControllerPrestShop cps = new ControllerPrestShop();
        //Lista de productes de prestashop
        List<Product> prestashopProducts = cps.getAllProducts();
        //Lista de productes d'odoo
        List<Product> productsOdoo = controllerOdoo.getProducts();

        // Creamos un mapa de referencia → producto para búsqueda rápida
        Map<String, Product> prestashopMap = new HashMap<>();
        for (Product p : prestashopProducts) {
            if (p.getReference() != null && !p.getReference().isEmpty()) {
                prestashopMap.put(p.getReference(), p);
            }
        }
        
        // Bucle per a recorre tots els productes que hi han a Odoo
        for (Product odooProduct : productsOdoo) {
            // Agafo la referencia del producte d'odoo
            String ref = odooProduct.getReference();
            // Agafo el producte que hi ha a PrestaShop amb la mateixa referencia
            Product psProduct = prestashopMap.get(ref);
            
            // Si el producte de PrestaShop es null (osigui que no ha trobat un producte amb aquella referencia)
            if (psProduct == null) {
                // No existeix en PrestaShop, llavors el creo
                System.out.println("Creando producto en PrestaShop: " + odooProduct.getName());

                // Mapejo els camps d'Odoo cap a PrestaShop
                ProductMapper mapperPOST = new ProductMapper(
                        "1", // id_manufacturer
                        "1", // id_supplier
                        "1", // id_category_default
                        odooProduct.getName(), // product_name
                        (float) odooProduct.getSales_price().doubleValue(), //sales_price
                        1, // product_id (pero en el mapeig no s'usa
                        "", // id_category
                        "standard", // product_type
                        ref // reference
                );
                // Creacio del producte a PrestaShop
                cps.uploadProduct(mapperPOST.xmlProductPOST());
            } else {
                // Si existeix, comprobo si tots els camps son iguals, per si s'ha d'actualitzar el producte
                boolean needsUpdate = !odooProduct.getSales_price().equals(psProduct.getSales_price())
                        || !odooProduct.getCost().equals(psProduct.getCost())
                        || !odooProduct.getName().equals(psProduct.getName());

                if (needsUpdate) {
                    System.out.println("Actualizando producto en PrestaShop: " + odooProduct.getName());

                    // ID de PrestaShop
                    long prestashopId = psProduct.getPrestashopId() != null ? psProduct.getPrestashopId() : 0L;
                    
                    // Precios convertidos a float
                    float salesPrice = odooProduct.getSales_price() != null ? odooProduct.getSales_price().floatValue() : 0f;

                    // Creo el mapeig per actualitzar el producte
                    ProductMapper mapperPUT = new ProductMapper(
                            "1", // id_manufacturer
                            "1", // id_supplier
                            "1", // id_category_default
                            odooProduct.getName(), // product_name
                            salesPrice, //sales_price
                            prestashopId, // product_id
                            "", // id_category
                            "standard", // product_type
                            ref // reference
                    );
                    
                    // Faig l'actualitzacio
                    cps.updateProduct(mapperPUT.xmlProductPUT());

                } else {
                    System.out.println("Everything up to date.");
                }
            }
        }
    }
}

package com.rgbconsulting.prestashop;

import com.rgbconsulting.prestashop.common.odoo.model.Product;
import com.rgbconsulting.prestashop.controller.ControllerOdoo;
import com.rgbconsulting.prestashop.controller.ControllerPrestShop;
import com.rgbconsulting.prestashop.mapper.CategoryMapper;
import com.rgbconsulting.prestashop.mapper.ProductMapper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
                String prestaShopCategory = getPrestaShopCategory(odooProduct, cps);
                // No existeix en PrestaShop, llavors el creo
                System.out.println("Creando producto en PrestaShop: " + odooProduct.getName());

                // Mapejo els camps d'Odoo cap a PrestaShop
                ProductMapper mapperPOST = new ProductMapper(
                        "1", // id_manufacturer
                        "1", // id_supplier
                        "1", // id_category_default
                        odooProduct.getName(), // product_name
                        (float) odooProduct.getSales_price().doubleValue(), //sales_price
                        1, // product_id (pero en el mapeig no s'usa)
                        prestaShopCategory, // id_category
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
                    String prestaShopCategory = getPrestaShopCategory(odooProduct, cps);
                    System.out.println("Actualizando producto en PrestaShop: " + odooProduct.getName());

                    // ID de PrestaShop
                    long prestashopId;
                    if (psProduct.getPrestashopId() != null) {
                        prestashopId = psProduct.getPrestashopId();
                    } else {
                        prestashopId = 0L;
                    }

                    // Precios convertidos a float
                    float salesPrice;
                    if (odooProduct.getSales_price() != null) {
                        salesPrice = odooProduct.getSales_price().floatValue();
                    } else {
                        salesPrice = 0f;
                    }

                    // Creo el mapeig per actualitzar el producte
                    ProductMapper mapperPUT = new ProductMapper(
                            "1", // id_manufacturer
                            "1", // id_supplier
                            "1", // id_category_default
                            odooProduct.getName(), // product_name
                            salesPrice, //sales_price
                            prestashopId, // product_id
                            prestaShopCategory, // id_category
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

    private static String getPrestaShopCategory(Product odooProduct, ControllerPrestShop cps) {
        String prestaShopCategory = "1";
        String odooCategory = odooProduct.getCategId().trim();

        try (FileReader freader = new FileReader("files/categorys.txt"); BufferedReader breader = new BufferedReader(freader)) {
            String line = breader.readLine(); //me salto la primera linia (capçalera)
            while ((line = breader.readLine()) != null) {
                String categorys[] = line.split("/");
                String category = categorys[1].trim();
                if (category.equals(odooCategory)) {
                    if (!categorys[2].isEmpty()) {
                        // Si encara no existeix el id dins el fitxer aixo vol dir que no existeix
                        // Llavors el creo
                        
                        // Els numeros hardcodejats estan ficats per probar
                        CategoryMapper categoryMapper = new CategoryMapper(categorys[0].toString(), "2", "1");
                        cps.uploadCategory(categoryMapper.xmlCreationCategory());
                    } else {
                        // Si esta el id vol dir que existeix i li puc passar el id de la categoria
                        prestaShopCategory = categorys[2].toString();
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prestaShopCategory;
    }
}

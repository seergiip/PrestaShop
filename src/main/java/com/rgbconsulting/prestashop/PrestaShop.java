package com.rgbconsulting.prestashop;

import com.rgbconsulting.prestashop.common.odoo.model.ProductProduct;
import com.rgbconsulting.prestashop.common.odoo.model.ProductTemplate;
import com.rgbconsulting.prestashop.common.odoo.model.Stock;
import com.rgbconsulting.prestashop.controller.ControllerOdoo;
import com.rgbconsulting.prestashop.controller.ControllerPrestShop;
import com.rgbconsulting.prestashop.mapper.CategoryMapper;
import com.rgbconsulting.prestashop.mapper.ProductMapper;
import com.rgbconsulting.prestashop.mapper.StockMapper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
        List<ProductTemplate> prestashopProducts = cps.getAllProducts();
        //Lista de productes d'odoo
        List<ProductTemplate> productsOdoo = controllerOdoo.getProductsTemplate();
        //Llista de stock de tots els productes
        List<Stock> stocksOdoo = controllerOdoo.getStocks();
        
        List<ProductProduct> productsProductOdoo = controllerOdoo.getProductsProduct();

        // Creamos un mapa de referencia → producto para búsqueda rápida
        Map<String, ProductTemplate> prestashopMap = new HashMap<>();
        for (ProductTemplate p : prestashopProducts) {
            if (p.getReference() != null && !p.getReference().isEmpty()) {
                prestashopMap.put(p.getReference(), p);
            }
        }

        // Bucle per a recorre tots els productes que hi han a Odoo
        for (ProductTemplate odooProduct : productsOdoo) {
            // Agafo la referencia del producte d'odoo
            String ref = odooProduct.getReference();
            // Agafo el producte que hi ha a PrestaShop amb la mateixa referencia
            ProductTemplate psProduct = prestashopMap.get(ref);
            // id_category_default
            String id_category_default;

            // Si el producte de PrestaShop es null (osigui que no ha trobat un producte amb aquella referencia)
            if (psProduct == null) {
                List<String> prestaShopCategories = getPrestaShopCategories(odooProduct, cps);
                id_category_default = getId_category_default(prestaShopCategories);
                // No existeix en PrestaShop, llavors el creo
                System.out.println("Creando producto en PrestaShop: " + odooProduct.getName());

                // Mapejo els camps d'Odoo cap a PrestaShop
                ProductMapper mapperPOST = new ProductMapper(
                        "1", // id_manufacturer
                        "1", // id_supplier
                        id_category_default, // id_category_default
                        odooProduct.getName(), // product_name
                        (float) odooProduct.getSales_price().doubleValue(), //sales_price
                        1, // product_id (pero en el mapeig no s'usa)
                        prestaShopCategories, // id_category
                        "standard", // product_type
                        ref, // reference
                        1 //unit_price
                );
                // Creacio del producte a PrestaShop i me quedo el seu id
                String PrestaShopProduct_id = cps.getProductId(cps.uploadProduct(mapperPOST.xmlProductPOST()));

                // Creacio de el stock
                String stock_id = cps.getIdOfStock(cps.getAvailableStock(PrestaShopProduct_id));
                Integer productTempId = odooProduct.getId();
                Integer productProductTmpID = null;
                for (ProductProduct prod : productsProductOdoo) {
                    if (prod.getProduct_tmpl_id().equals(productTempId)) {
                        productProductTmpID = prod.getId();
                    }
                }
                
                String quantity = String.valueOf(
                        controllerOdoo
                                .getQuantity(productProductTmpID, stocksOdoo)
                                .intValue()
                );

                
                StockMapper stockMapper = new StockMapper(stock_id, PrestaShopProduct_id, quantity);
                // li paso el id del producte
                cps.uploadStock(stockMapper.xmlStock());
            } else {
                // Si existeix, comprobo si tots els camps son iguals, per si s'ha d'actualitzar el producte
                boolean needsUpdate = !odooProduct.getSales_price().equals(psProduct.getSales_price())
                        || !odooProduct.getCost().equals(psProduct.getCost())
                        || !odooProduct.getName().equals(psProduct.getName());

                if (needsUpdate) {
                    List<String> prestaShopCategories = getPrestaShopCategories(odooProduct, cps);
                    id_category_default = getId_category_default(prestaShopCategories);
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
                            id_category_default, // id_category_default
                            odooProduct.getName(), // product_name
                            salesPrice, //sales_price
                            prestashopId, // product_idvoid
                            prestaShopCategories, // id_category
                            "standard", // product_type
                            ref, // reference
                            1 //unit_price
                    );

                    // Faig l'actualitzacio del producte
                    String product_id = cps.getProductId(cps.updateProduct(mapperPUT.xmlProductPUT()));
                    // Actualitzacio de el stock
                    String quantity = controllerOdoo.getQuantity(Integer.parseInt(product_id), stocksOdoo).toString();
                    String stock_id = cps.getIdOfStock(cps.getAvailableStock(product_id));
                    StockMapper stockMapper = new StockMapper(stock_id, product_id, quantity);
                    // li paso el id del producte
                    cps.uploadStock(stockMapper.xmlStock());
                } else {
                    System.out.println("Everything up to date.");
                }
            }
        }
    }

    private static List<String> getPrestaShopCategories(ProductTemplate odooProduct, ControllerPrestShop cps) {
        List<String> prestaShopCategories = new ArrayList<>();
        prestaShopCategories.add("2");

        String odooCategory = odooProduct.getCategId().trim();

        try (FileReader freader = new FileReader("files/categorys.txt"); BufferedReader breader = new BufferedReader(freader)) {
            String line = breader.readLine(); //me salto la primera linia (capçalera)
            while ((line = breader.readLine()) != null) {
                String categorys[] = line.split("/");
                String category = categorys[1].trim();
                if (category.equals(odooCategory)) {
                    if (categorys[2].isEmpty() || categorys[2].isBlank()) {
                        // Si encara no existeix el id dins el fitxer aixo vol dir que no existeix
                        // Llavors el creo

                        // Els numeros hardcodejats estan ficats per probar
                        CategoryMapper categoryMapper = new CategoryMapper(categorys[0].toString(), "2", "1");

                        String categId = cps.getCategoryId(cps.uploadCategory(categoryMapper.xmlCreationCategory()));

                        // El fiquem al fitxer
                        if (categId != null) {
                            updateCategoryIdInFile(
                                    "files/categorys.txt",
                                    categoryMapper.getName(),
                                    categId
                            );
                        }
                        // Li pasem per a que crei el producte amb el id
                        prestaShopCategories.add(categId);

                    } else {
                        // Si esta el id vol dir que existeix i li puc passar el id de la categoria
                        prestaShopCategories.add(categorys[2].toString());
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prestaShopCategories;
    }

    private static void updateCategoryIdInFile(String filePath, String categoryName, String prestashopId) {

        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.startsWith("PrestaShop")) {
                    updatedLines.add(line);
                    continue;
                }

                String[] parts = line.split("/", -1);

                if (parts.length == 3 && parts[0].equals(categoryName)) {

                    if (parts[2].isBlank()) {
                        line = parts[0] + "/" + parts[1] + "/" + prestashopId;
                    }
                }

                updatedLines.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String l : updatedLines) {
                writer.write(l);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getId_category_default(List<String> categories) {
        return categories.get(categories.size() - 1);
    }

}

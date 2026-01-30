package com.rgbconsulting.prestashop.sync;

import com.rgbconsulting.prestashop.common.odoo.model.ProductCategory;
import com.rgbconsulting.prestashop.common.odoo.model.ProductProduct;
import com.rgbconsulting.prestashop.common.odoo.model.ProductTemplate;
import com.rgbconsulting.prestashop.common.odoo.model.Stock;
import com.rgbconsulting.prestashop.controller.ControllerOdoo;
import com.rgbconsulting.prestashop.controller.ControllerPrestShop;
import com.rgbconsulting.prestashop.mapper.CategoryMapper;
import com.rgbconsulting.prestashop.mapper.ProductMapper;
import com.rgbconsulting.prestashop.mapper.StockMapper;
import com.rgbconsulting.prestashop.model.CategoryOdooPrestashop;
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
public class PrestaShopSyncService {

    public void sync() {
        ControllerOdoo controllerOdoo = new ControllerOdoo();
        ControllerPrestShop cps = new ControllerPrestShop();

        List<ProductTemplate> prestashopProducts = cps.getAllProducts();
        List<ProductTemplate> odooProducts = controllerOdoo.getProductsTemplate();
        List<Stock> stocksOdoo = controllerOdoo.getStocks();
        List<ProductProduct> productsProductOdoo = controllerOdoo.getProductsProduct();

        Map<String, ProductTemplate> prestashopMap = buildPrestashopMap(prestashopProducts);

        for (ProductTemplate odooProduct : odooProducts) {
            syncProduct(
                    odooProduct,
                    prestashopMap.get(odooProduct.getReference()),
                    controllerOdoo,
                    cps,
                    stocksOdoo,
                    productsProductOdoo
            );
        }
    }

    private static void uploadImages(ControllerOdoo controllerOdoo, ControllerPrestShop controllerPrestShop, ProductTemplate productTemplate) {
        controllerPrestShop.uploadImage(controllerOdoo.downloadImage(productTemplate.getImageFile()), productTemplate.getReference());
    }

    private static void syncCategories(ControllerOdoo cntOdoo, ControllerPrestShop cntPrstShop) {
        // Categories of Odoo
        List<ProductCategory> odooCategories = cntOdoo.getProductsCategory();
        // Categories of PrestaShop
        List<CategoryMapper> prestaShopCategories = cntPrstShop.getPrestaShopCategoriesToList(cntPrstShop.getPrestaShopCategories());
        // Categories of PrestaShop & Odoo
        List<CategoryOdooPrestashop> odooPrestaCategories = cntOdoo.getAllCategoriesOdooPrestashop();

        if (odooPrestaCategories.isEmpty()) {
            for (ProductCategory p : odooCategories) {
                cntOdoo.insertCategoryOdooPrestashop(p.getId(), null, p.getComplete_Name(), null, p.getParent_Id().toString(), null);
            }
            for (CategoryMapper c : prestaShopCategories) {
                cntOdoo.insertCategoryOdooPrestashop(null, Integer.parseInt(c.getId()), null, c.getName(), c.getId_parent(), c.getActive());
            }
            // Torno a agafar totes les categories que tinc en total
            odooPrestaCategories = cntOdoo.getAllCategoriesOdooPrestashop();
            
            for (CategoryOdooPrestashop c : odooPrestaCategories) {
                if (c.getOdoo_id() == null) {
                    
                } else if (c.getPrestashop_id() == null) {
                    
                }
            }
            
        } else {
            for (ProductCategory p : odooCategories) {
                for (CategoryMapper c : prestaShopCategories) {
                    /*if () {
                        
                    }*/
                }
            }
        }
    }

    private static List<String> getPrestaShopCategoriesFromTxtFile(
            ProductTemplate odooProduct,
            ControllerPrestShop cps) {
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

                        // Creo la categoria a PrestaShop i guardo el id que se li assigna
                        String categId = cps.getCategoryId(cps.uploadCategory(categoryMapper.xmlCreationCategory()));

                        // Fico el id de la categoria al fitxer
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

    private static void updateCategoryIdInFile(
            String filePath,
            String categoryName,
            String prestashopId) {

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

    private static String getId_category_default(
            List<String> categories) {
        return categories.get(categories.size() - 1);
    }

    private static Map<String, ProductTemplate> buildPrestashopMap(
            List<ProductTemplate> prestashopProducts) {
        Map<String, ProductTemplate> map = new HashMap<>();

        for (ProductTemplate p : prestashopProducts) {
            if (p.getReference() != null && !p.getReference().isEmpty()) {
                map.put(p.getReference(), p);
            }
        }
        return map;
    }

    private static void syncProduct(
            ProductTemplate odooProduct,
            ProductTemplate psProduct,
            ControllerOdoo controllerOdoo,
            ControllerPrestShop cps,
            List<Stock> stocksOdoo,
            List<ProductProduct> productsProductOdoo
    ) {

        if (psProduct == null) {
            createProductInPrestashop(odooProduct, controllerOdoo, cps, stocksOdoo, productsProductOdoo);
            return;
        }

        if (needsUpdate(odooProduct, psProduct)) {
            updateProductInPrestashop(odooProduct, psProduct, controllerOdoo, cps, stocksOdoo, productsProductOdoo);
        } else {
            System.out.println("Everything up to date: " + odooProduct.getName());
        }
    }

    private static boolean needsUpdate(ProductTemplate odoo, ProductTemplate ps) {
        return !odoo.getSales_price().equals(ps.getSales_price())
                || !odoo.getCost().equals(ps.getCost())
                || !odoo.getName().equals(ps.getName());
    }

    private static void createProductInPrestashop(
            ProductTemplate odooProduct,
            ControllerOdoo controllerOdoo,
            ControllerPrestShop cps,
            List<Stock> stocksOdoo,
            List<ProductProduct> productsProductOdoo
    ) {
        System.out.println("-------------------------------------------------------------");
        System.out.println("Creando producto en PrestaShop: " + odooProduct.getName());

        List<String> categories = getPrestaShopCategoriesFromTxtFile(odooProduct, cps);
        String defaultCategory = getId_category_default(categories);
        if (odooProduct.getReference() != null) {
            ProductMapper mapper = new ProductMapper(
                    "1",
                    "1",
                    defaultCategory,
                    odooProduct.getName(),
                    odooProduct.getSales_price().floatValue(),
                    1,
                    categories,
                    odooProduct.getReference(),
                    1
            );
            // Creacio del product
            String prestashopProductId = cps.getProductIdFromResponse(cps.uploadProduct(mapper.xmlProductPOST()));
            // Creacio del stock
            updateStock(
                    prestashopProductId,
                    odooProduct,
                    controllerOdoo,
                    cps,
                    stocksOdoo,
                    productsProductOdoo
            );
            // Creacio de la seva imatge
            uploadImages(controllerOdoo, cps, odooProduct);
        } else {
            System.out.println("Error en crear el producte la referencia es null. Siusplau assigna-li una referencia al producte.");
        }
    }

    private static void updateProductInPrestashop(
            ProductTemplate odooProduct,
            ProductTemplate psProduct,
            ControllerOdoo controllerOdoo,
            ControllerPrestShop cps,
            List<Stock> stocksOdoo,
            List<ProductProduct> productsProductOdoo
    ) {
        System.out.println("-------------------------------------------------------------");
        System.out.println("Actualizando producto en PrestaShop: " + odooProduct.getName());

        List<String> categories = getPrestaShopCategoriesFromTxtFile(odooProduct, cps);
        String defaultCategory = getId_category_default(categories);

        ProductMapper mapper = new ProductMapper(
                "1",
                "1",
                defaultCategory,
                odooProduct.getName(),
                odooProduct.getSales_price().floatValue(),
                psProduct.getPrestashopId(),
                categories,
                odooProduct.getReference(),
                1
        );
        // Actualitzacio del producte
        cps.updateProduct(mapper.xmlProductPUT());

        String prestashopProductId
                = cps.getProductIdFromResponse(
                        cps.getProductByReference(odooProduct.getReference())
                );
        // Actualitzacio del seu stock
        updateStock(
                prestashopProductId,
                odooProduct,
                controllerOdoo,
                cps,
                stocksOdoo,
                productsProductOdoo
        );

        // Actualitzacio de la seva imatge
        uploadImages(controllerOdoo, cps, odooProduct);
    }

    private static void updateStock(
            String prestashopProductId,
            ProductTemplate odooProduct,
            ControllerOdoo controllerOdoo,
            ControllerPrestShop cps,
            List<Stock> stocksOdoo,
            List<ProductProduct> productsProductOdoo
    ) {

        String stockId = cps.getIdOfStock(cps.getAvailableStock(prestashopProductId));

        Integer productProductId = findProductProductId(odooProduct.getId(), productsProductOdoo);

        String quantity = String.valueOf(controllerOdoo.getQuantity(productProductId, stocksOdoo).intValue());

        StockMapper stockMapper = new StockMapper(stockId, prestashopProductId, quantity);

        cps.uploadStock(stockMapper.xmlStock());
    }

    private static Integer findProductProductId(
            Integer productTemplateId,
            List<ProductProduct> productsProductOdoo
    ) {

        for (ProductProduct prod : productsProductOdoo) {
            if (prod.getProduct_tmpl_id().equals(productTemplateId)) {
                return prod.getId();
            }
        }
        return null;
    }

    private static String normalize(String s) {
        return s.toLowerCase()
                .trim()
                .replaceAll("\\s+", " ");
    }

}

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

        syncCategories(controllerOdoo, cps);

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
        // Categories of PrestaShop & Odoo
        List<CategoryOdooPrestashop> odooPrestaCategories = cntOdoo.getAllCategoriesOdooPrestashop();

        if (odooPrestaCategories.isEmpty()) {
            System.out.println("-------------------------------------------------------------------");
            System.out.println("ERROR. HAS DE ESCRIURE ALMENYS LA PRIMERA CATEGORIA DINS LA BASE DE DADES.");
            System.out.println("-------------------------------------------------------------------");
        } else {
            for (ProductCategory o : odooCategories) {
                if (cntOdoo.existsCategoryByOdooId(o.getId())) {
                    continue;
                }
                for (CategoryOdooPrestashop oc : odooPrestaCategories) {
                    if (o.getParent_Id() != null && o.getParent_Id().equals(oc.getOdoo_id())) {

                        // Insertar categoria de Odoo a PrestaShop
                        cntOdoo.insertCategoryOdooPrestashop(o.getId(), null, o.getName(), o.getName(), oc.getPrestashop_id().toString(), oc.getActive());

                        // Crear la categoria a prestashop
                        CategoryMapper categoryMapper = new CategoryMapper(o.getName(), oc.getPrestashop_id().toString(), oc.getActive());

                        String categId = cntPrstShop.getCategoryId(cntPrstShop.uploadCategory(categoryMapper.xmlCreationCategory()));

                        // Agafar el id de la categoria a prestashop i insertarla dins la taula en la fila que correspon
                        cntOdoo.updatePrestaShop_id(o.getId(), Integer.parseInt(categId));

                        odooPrestaCategories.add(
                                new CategoryOdooPrestashop(
                                        o.getId(),
                                        Integer.parseInt(categId),
                                        o.getName(),
                                        o.getName(),
                                        oc.getPrestashop_id().toString(),
                                        oc.getActive()
                                )
                        );
                        break;
                    }
                }
            }
        }
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

        List<String> categories = getPrestaShopIdCategory(odooProduct, controllerOdoo);
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

        List<String> categories = getPrestaShopIdCategory(odooProduct, controllerOdoo);
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

    private static List<String> getPrestaShopIdCategory(ProductTemplate odooProduct, ControllerOdoo cOdoo) {
        String odooCategory = odooProduct.getCategId().trim();
        List<CategoryOdooPrestashop> cop = cOdoo.getAllCategoriesOdooPrestashop();
        List<String> categories = new ArrayList<>();
        String parent_id, category;
        

        for (int i = 0; i < cop.size(); i++) {
            if (cop.get(i).getOdoo_name().equals(odooCategory)) {
                category = cop.get(i).getPrestashop_id().toString();
                categories.add(category);

                // Part recursiva per agafar les categories pare del producte
                parent_id = cop.get(i).getId_parent();
                getPrestaShopParentsId(parent_id, cOdoo, categories);
            }
        }
        return categories;
    }

    private static String getId_category_default(
            List<String> categories) {
        // Primera posicio de la llista es on esta la categoria per defecte
        if (categories.size() > 0) {
            return categories.get(0);
        } else {
            return null;
        }
    }

    private static void getPrestaShopParentsId(
            String parentId,
            ControllerOdoo cOdoo,
            List<String> categories) {

        if (parentId == null) {
            return;
        }

        List<CategoryOdooPrestashop> cop = cOdoo.getAllCategoriesOdooPrestashop();
        for (CategoryOdooPrestashop c : cop) {
            if (c.getPrestashop_id().toString().equals(parentId)) {
                if (!categories.contains(parentId)) {
                    categories.add(parentId);
                }
                getPrestaShopParentsId(c.getId_parent(), cOdoo, categories);
                return;
            }
        }
    }
}

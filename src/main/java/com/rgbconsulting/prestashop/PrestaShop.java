package com.rgbconsulting.prestashop;

import com.rgbconsulting.prestashop.sync.PrestaShopSyncService;

/**
 *
 * @author sergi
 */
public class PrestaShop {

    public static void main(String[] args) {
        new PrestaShopSyncService().sync();
    }
}

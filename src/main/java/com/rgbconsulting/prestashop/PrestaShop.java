package com.rgbconsulting.prestashop;

import com.rgbconsulting.prestashop.app.AppScheduledTasks;
import com.rgbconsulting.prestashop.sync.PrestaShopSyncService;

/**
 *
 * @author sergi
 */
public class PrestaShop {

    public static void main(String[] args) {
        new PrestaShopSyncService().sync();
        /*
        AppScheduledTasks appScheduledTasks = new AppScheduledTasks();
        
        appScheduledTasks.automaticTimer();
         */
    }
}

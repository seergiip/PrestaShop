package com.rgbconsulting.prestashop.app;

import com.rgbconsulting.prestashop.sync.PrestaShopSyncService;
import jakarta.ejb.Schedule;
import jakarta.ejb.Schedules;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.time.LocalDateTime;

/**
 *
 * @author luiscarlosgonzalez
 */
@Startup
@Singleton
public class AppScheduledTasks {

    private PrestaShopSyncService service = new PrestaShopSyncService();

    @Schedule(second = "*/15", minute = "*", hour = "*")
    public void automaticTimer() {
        

        try {
            System.out.println("-------------------------------------");
            System.out.println("Cada 15 segundos:" + LocalDateTime.now());
            service.sync();
            System.out.println("Sync Odoo to PrestaShop completed.");
            System.out.println("-------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Schedules({
        @Schedule(hour = "12"),
        @Schedule(hour = "20")
    })
    public void multipleSchedule() {
        System.out.println("Multiple segundos:" + LocalDateTime.now());
    }
}

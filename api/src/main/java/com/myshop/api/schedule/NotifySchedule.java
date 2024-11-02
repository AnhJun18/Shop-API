package com.myshop.api.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.myshop.api.service.notify.INotifyService;

@Component
public class NotifySchedule {
    @Autowired
    INotifyService notifyService;
    private final static String TK_GR_QC = "Y5OKJTawfVbMoOfMfKtFbXO6wq3QJo6qE1ocde1YvKE";

    @Scheduled(cron = "0 0,30,50 8 * * 1-5")
    public void checkInJob() {
        notifyService.sendNotify(TK_GR_QC, "CheckIn đê bà con ơi!");
    }

    @Scheduled(cron = "0 45 16 * * 1-4")
    @Scheduled(cron = "0 0,30 17 * * 1-4")
    @Scheduled(cron = "0 0 18 * * 1-4")
    @Scheduled(cron = "0 10 11 * * 1-6")
    public void checkOutJob() {
        System.out.println("checkOutJob");
        notifyService.sendNotify(TK_GR_QC, "CheckOut đê bà con ơi!");
    }

    @Scheduled(cron = "0 0 17 * * 5")
    public void checkOutT6() {
        System.out.println("checkOutT6");
        notifyService.sendNotify(TK_GR_QC, "T6 rồi! Bung xõa đi, về sớm thoi bà con");
    }
}

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

    @Scheduled(cron = "0 0,30 7,8,10,16,17 * * 1-7")
    public void scheduleTaskWithFixedRate() {
        notifyService.sendNotify(TK_GR_QC, "Chấm công đê bà con");
    }

   
}

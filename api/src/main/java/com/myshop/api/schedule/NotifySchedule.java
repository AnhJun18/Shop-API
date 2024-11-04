package com.myshop.api.schedule;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties.Job;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myshop.api.service.notify.INotifyService;
import com.google.gson.reflect.TypeToken;

@Component
public class NotifySchedule {
    @Autowired
    INotifyService notifyService;
    @Autowired
    DataSource dataSource;

    private final static String TK_GR_QC = "O6H2ZjWqLJmSI891Nqo2DdjcWsWV8R5ydt0NXJIebQu";
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static Map<String, String> mapcronJob = new HashMap();
    public static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls()
            .setDateFormat("HH:mm:ss").create();

    public NotifySchedule(DataSource dataSource) throws SQLException {
        this.dataSource = dataSource;
        init(dataSource);
    }

    private void init(DataSource dataSource) throws SQLException

    {
        String config = "";
        String sql = "SELECT jsonvalue, keyconfig FROM lpa_job.sys_config WHERE keyconfig = ?";

        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, "LPA_CONFIGJOB");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                config = (resultSet.getString("jsonvalue"));
            }
            if(StringUtils.isNotBlank(config)){
                Type listType = new TypeToken<List<LineJobModel>>() {
            }.getType();
            List<LineJobModel> listJob = gson.fromJson(config, listType);
            if (CollectionUtils.isEmpty(listJob)) {
                for (LineJobModel item : listJob)
                    mapcronJob.put(item.getTime(), item.getMessage());
            }
            }

            
        }
    }
    @Scheduled(cron = "0 30-59/10,0-50/10 7-8 * * 1-5")

    public void checkInJob() {
        notifyService.sendNotify(TK_GR_QC, " `CHECKIN` ĐÊ CẢ NHÀ ƠI!");
    }

    @Scheduled(cron = "0 50-59/10,0-50/10 16-17 * * 1-5")
    public void checkOutJob() {
        notifyService.sendNotify(TK_GR_QC, " `CHECKOUT` ZỀ ĐI BÀ CON!");
    }


    @Scheduled(cron = "0 0 9-12,13-17 * * 1-5")
    public void c() {
        notifyService.sendNotify(TK_GR_QC, " `UỐNG NƯỚC ĐÊ! `");
    }

    @Scheduled(cron = "0 10 9,13,15 * * 1-5")
    public void diDai() {
        notifyService.sendNotify(TK_GR_QC, "` CÓ AI ĐI >>> CHUNG HEM! `");
    }
  
}

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

    private final static String TK_GR_QC = "Pj7DAmdMKAJzUdLc5Meqwa1jqytAuDJkcHAicrq3TZj";
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

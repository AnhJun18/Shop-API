package com.myshop.api.schedule;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    List<String> listN = Arrays.asList(
            "`Thùy Na! ` UỐNG NƯỚC ĐI KÌA:>",
            "`Thùy Na! ` ĐẾN H UỐNG NƯỚC RỒI:>",
            "`Thùy Na! ` NƯỚC ĐANG CẦN ĐƯỢC UỐNG:>",
            "`Thùy Na! ` NƯỚC ĐƯỢC UỐNG CHƯA?:>",
            "`Thùy Na! ` H20:>");

    private static final String HEALTH_CHECK_URL = "https://shop-api-k4ef.onrender.com/api/notify/health";

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
            if (StringUtils.isNotBlank(config)) {
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

    @Scheduled(cron = "0 */10 7-9 * * 1-5")
    public void checkInJob() {
        if (isTimeWithinRange(7, 0, 8, 40))
            notifyService.sendNotify(TK_GR_QC, " `CHECKIN` ĐÊ CẢ NHÀ ƠI!");
    }

    @Scheduled(cron = "0 */10 16-18 * * 1-5")
    public void checkOutJob() {
        if (isTimeWithinRange(16, 0, 17, 40))
            notifyService.sendNotify(TK_GR_QC, " `CHECKOUT` ZỀ ĐI BÀ CON!");
    }

    @Scheduled(cron = "0 0 9-12,13-17 * * 1-5")
    public void c() {
        notifyService.sendNotify(TK_GR_QC, " `HEALTHY! UỐNG NƯỚC ĐI MN `");
    }

    @Scheduled(cron = "0 30 8-17 * * 1-5")
    public void drinkWater() {
        int hour = LocalTime.now().getHour();
        if (hour == 12) {
            return;
        }
        Random rand = new Random();
        int randomIndex = rand.nextInt(listN.size());
        notifyService.sendNotify(TK_GR_QC, listN.get(randomIndex));
    }

    @Scheduled(fixedRate = 600000) // 600,000 milliseconds = 10 phút
    public void performHealthCheck() {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(HEALTH_CHECK_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("API is healthy: " + response.body());
            } else {
                System.err.println("API is unhealthy. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Health check failed: " + e.getMessage());
        }
    }

    public static boolean isTimeWithinRange(int fromHour, int fromMinute,
            int toHour, int toMinute) {
        LocalTime now = LocalTime.now();
        LocalTime fromTime = LocalTime.of(fromHour, fromMinute, 0);
        LocalTime toTime = LocalTime.of(toHour, toMinute, 0);

        if (fromTime.isBefore(toTime) || fromTime.equals(toTime)) { // Trường hợp khoảng bình thường
            return !now.isBefore(fromTime) && !now.isAfter(toTime);
        } else { // Trường hợp khoảng qua đêm (e.g., 23:00 -> 02:00)
            return !now.isBefore(fromTime) || !now.isAfter(toTime);
        }
    }
}
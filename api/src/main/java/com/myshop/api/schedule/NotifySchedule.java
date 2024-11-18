package com.myshop.api.schedule;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myshop.api.service.notify.INotifyService;

@Component
public class NotifySchedule {
    @Autowired
    INotifyService notifyService;

    public static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls()
            .setDateFormat("HH:mm:ss").create();

    private static final String HEALTH_CHECK_URL = "https://shop-api-ptit.onrender.com/api/notify/health";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
        

    public NotifySchedule(DataSource dataSource) throws SQLException {
    }



    @Scheduled(fixedRate = 600000) // 600,000 milliseconds = 10 phút
    public void performHealthCheck() {
        try {
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
    
}

package com.myshop.api.service.notify;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Service;

@Service
public class NotifyServiceImpl implements INotifyService {
	private static final String lineHost = "https://notify-api.line.me/api/notify";

    @Override
    public boolean sendNotify(String token, String message) {
        try {
            String encodedMessage = URLEncoder.encode(message, "UTF-8");
            HttpURLConnection connection = (HttpURLConnection) new URL(lineHost).openConnection();
    
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer "+ token);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            connection.setDoOutput(true);


            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write("message=" + encodedMessage);
            }
            return connection.getResponseCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
}

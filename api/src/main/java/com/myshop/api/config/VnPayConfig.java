package com.myshop.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Data
@Configuration
@ConfigurationProperties(prefix = "vnpay")
public class VnPayConfig {
    public String vnp_Version;
    public String vnp_Command;
    public String vnp_Locale;
    public String vnp_OrderType;
    public String vnp_BankCode;
    public String vnp_PayUrl;
    public String vnp_Returnurl_dev;
    public String vnp_Returnurl;
    public String vnp_TmnCode;
    public String vnp_HashSecret;
    public String vnp_ApiUrl;
    public String url_response_ui_dev;
    public String url_response_ui;
    public String vnp_Salt;

    public String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }

    public String getIpAddress(ServerHttpRequest request) {
        String ipAddress = null;
        try {
            HttpHeaders headers = request.getHeaders();
            String xForwardedForHeader = headers.getFirst("X-FORWARDED-FOR");
            if (xForwardedForHeader != null) {
                // The header value may contain multiple IP addresses separated by commas
                String[] ipAddresses = xForwardedForHeader.split(",");
                // Use the first IP address in the list
                ipAddress = ipAddresses[0].trim();
            }
            if (ipAddress == null) {
                InetSocketAddress remoteAddress = request.getRemoteAddress();
                if (remoteAddress != null) {
                    InetAddress remoteInetAddress = remoteAddress.getAddress();
                    if (remoteInetAddress != null) {
                        ipAddress = remoteInetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
        }
        if (ipAddress == null) {
            ipAddress = "127.0.0.1";
        }
        return ipAddress;
    }

    public String hashAllFields(Map fields) throws UnsupportedEncodingException {
        List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                sb.append('=');
                sb.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString())
                );
                if (itr.hasNext()) {
                    sb.append('&');
                }

            }
        }
        return hmacSHA512(vnp_HashSecret, sb.toString());
    }


    public String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
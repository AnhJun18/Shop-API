package com.myshop.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
@Component
@ConfigurationProperties(prefix = "vnp")
public class VnPayConfig {
    public static String vnp_Version;
    public static String vnp_Command;
    public static String vnp_Locale;
    public static String vnp_OrderType;
    public static String vnp_BankCode;
    public static String vnp_PayUrl;
    public static String vnp_Returnurl_dev;
    public static String vnp_Returnurl;
    public static String vnp_TmnCode;
    public static String vnp_HashSecret;
    public static String vnp_ApiUrl;
    public static String url_response_ui_dev;
    public static String url_response_ui;
    public static String vnp_Salt;

    public static String hmacSHA512(final String key, final String data) {
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

    public static String getIpAddress(ServerHttpRequest request) {
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

    public static String hashAllFields(Map fields) throws UnsupportedEncodingException {
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


    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
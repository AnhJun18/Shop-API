package com.myshop.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
@ConfigurationProperties("ghtk")
public class GHTKConfig {
    public static String TOKEN;
    public static String API_ENDPOINT;
    public static String CREATE_ORDER_URL;
    public static String CALCUATE_SHIP_FEE_URL;
    public static String CHECKING_STATUS_URL;
    public static String PRINT_INVOICE;
    public static String CANCEL_ORDER_URL;
    public static String PICK_PROVINCE;
    public static String PICK_DISTRICT;
    public static String GHTK_HASHSECRET;
    public static String SALT;

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
}

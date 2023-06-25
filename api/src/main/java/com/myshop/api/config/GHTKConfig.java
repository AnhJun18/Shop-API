package com.myshop.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Data
@Configuration
@ConfigurationProperties(prefix = "ghtk")
public class GHTKConfig {
    public String TOKEN;
    public String API_ENDPOINT;
    public String CREATE_ORDER_URL;
    public String CALCUATE_SHIP_FEE_URL;
    public String CHECKING_STATUS_URL;
    public String PRINT_INVOICE;
    public String CANCEL_ORDER_URL;
    public String PICK_PROVINCE;
    public String PICK_DISTRICT;
    public String GHTK_HASHSECRET;
    public String SALT;

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
}

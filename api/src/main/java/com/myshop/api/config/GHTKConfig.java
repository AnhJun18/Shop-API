package com.myshop.api.config;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class GHTKConfig {

    public final static String TOKEN = "700a2a511c4730b289ad27ab100b9de838ca07bd";
    public final static String API_ENDPOINT = "https://services-staging.ghtklab.com";
    public final static String CREATE_ORDER_URL = API_ENDPOINT + "/services/shipment/order/?ver=1.5";
    public final static String CALCUATE_SHIP_FEE_URL = API_ENDPOINT + "/services/shipment/fee";
    public final static String CHECKING_STATUS_URL = API_ENDPOINT + "/services/shipment/v2/";
    public final static String PRINT_INVOICE = API_ENDPOINT + "/services/label/";
    public final static String CANCEL_ORDER_URL = API_ENDPOINT + "/services/shipment/cancel/";
    public final static String PICK_PROVINCE ="TP Hồ Chí Minh";
    public final static String PICK_DISTRICT ="Tăng Nhơn Phú A";
    public final static String GHTK_HASHSECRET ="PA18AAIN38HT36HKILS43MGPTITV32BM2KHHS3PA923";
    public final static String SALT ="PA2001Jun";

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

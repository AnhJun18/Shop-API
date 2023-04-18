package com.myshop.api.controller;

import com.myshop.api.config.VnPayConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping(value = "/api/payment/vnpay")
public class PaymentVnPayController {

    @GetMapping("/make")
    public ResponseEntity createPayment(HttpServletRequest request, @RequestParam(name = "vnp_OrderInfo") String vnp_OrderInfo,
                                             @RequestParam(name = "vnp_OrderType") String ordertype, @RequestParam(name = "vnp_Amount") Integer amount,
                                             @RequestParam(name = "vnp_Locale") String language, @RequestParam(name = "vnp_BankCode", defaultValue = "") String bankcode) throws UnsupportedEncodingException {
        String vnp_Version = VnPayConfig.vnp_Version;
        String vnp_Command = VnPayConfig.vnp_Command;
        String vnp_TxnRef = VnPayConfig.getRandomNumber(8);
        String vnp_IpAddr = VnPayConfig.getIpAddress(request);
        String vnp_TmnCode = VnPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        if (bankcode != null && bankcode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankcode);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", ordertype);

        if (language != null && !language.isEmpty()) {
            vnp_Params.put("vnp_Locale", language);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", VnPayConfig.vnp_Returnurl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Build data to hash and querystring
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                // Build query
                try {
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.vnp_HashSecret ,hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VnPayConfig.vnp_PayUrl + "?" + queryUrl;
        vnp_Params.put("redirect_url", paymentUrl);
        return ResponseEntity.status(302).header(HttpHeaders.LOCATION, paymentUrl).build();
    }
    @GetMapping(value = "/result")
    public ResponseEntity completePayment(HttpServletRequest request
    ) {
        System.out.println(request.getParameter("vnp_SecureHash"));
        return ResponseEntity.status(302).header(HttpHeaders.LOCATION, "http://localhost:3000/success/payment").build();
    }
    @GetMapping(value = "/cs")
    public String completePayment() {
        return "http://localhost:3000/success/payment";
    }
//	@GetMapping(value = "/result")
//	public Map<String, String> completePayment(HttpServletRequest request,
//			@RequestParam(name = "vnp_OrderInfo") String vnp_OrderInfo,
//			@RequestParam(name = "vnp_Amount") Integer vnp_Amount,
//			@RequestParam(name = "vnp_BankCode", defaultValue = "") String vnp_BankCode,
//			@RequestParam(name = "vnp_BankTranNo") String vnp_BankTranNo,
//			@RequestParam(name = "vnp_CardType") String vnp_CardType,
//			@RequestParam(name = "vnp_PayDate") String vnp_PayDate,
//			@RequestParam(name = "vnp_ResponseCode") String vnp_ResponseCode,
//			@RequestParam(name = "vnp_TransactionNo") String vnp_TransactionNo,
//			@RequestParam(name = "vnp_TxnRef") String vnp_TxnRef
//			) {
//		Map<String, String> response = new HashMap<>();
//
//		String year = vnp_PayDate.substring(0, 4);
//		String month = vnp_PayDate.substring(4, 6);
//		String date = vnp_PayDate.substring(6, 8);
//		String hour = vnp_PayDate.substring(8, 10);
//		String minutes = vnp_PayDate.substring(10, 12);
//		String second = vnp_PayDate.substring(12, 14);
//
//		String timePay = date + "-" + month + "-" + year + " " + hour + ":" + minutes + ":" + second;
//
//		response.put("vnp_OrderInfo", vnp_OrderInfo);
//		response.put("vnp_Amount", vnp_Amount.toString());
//		response.put("vnp_BankCode", vnp_BankCode);
//		response.put("vnp_BankTranNo", vnp_BankTranNo);
//		response.put("vnp_CardType", vnp_CardType);
//		response.put("vnp_PayDate", timePay);
//		response.put("vnp_ResponseCode", vnp_ResponseCode);
//		response.put("vnp_TransactionNo", vnp_TransactionNo);
//		response.put("vnp_TxnRef", vnp_TxnRef);
//
//		return response;
//	}
}
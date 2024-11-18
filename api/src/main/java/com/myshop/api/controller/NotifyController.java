package com.myshop.api.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.myshop.api.payload.request.datacollect.NotifyRequest;
import com.myshop.api.service.notify.INotifyService;
import com.myshop.common.http.ApiResponse;
import org.springframework.web.bind.annotation.*;
/**
 * @author Anh Jun
 * @author  Anh Jun
 */
@RestController
@RequestMapping("/api/notify")
public class NotifyController {


    @Autowired
    private INotifyService notifyService;


    @PostMapping("/push")
    public ApiResponse<Boolean> login( @RequestBody NotifyRequest notifyRequest) {
        return ApiResponse.of(notifyService.sendNotify(notifyRequest.getToken(), notifyRequest.getMessage()));
    }


    @GetMapping("/health")
    public ApiResponse<String> healthCheck() {
        return ApiResponse.of("Ok");
    }

}

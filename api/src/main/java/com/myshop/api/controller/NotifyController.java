package com.myshop.api.controller;




import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myshop.api.payload.request.datacollect.NotifyRequest;
import com.myshop.api.payload.response.user.LoginResponse;
import com.myshop.api.service.notify.INotifyService;
import com.myshop.common.http.ApiResponse;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

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

}

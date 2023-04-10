package com.myshop.api.controller;


import com.myshop.api.payload.request.datacollect.DataCollectRequest;
import com.myshop.api.service.chatbot.ChatBotService;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.chatbot.entities.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Anh Jun
 * @author Anh Jun
 */

@RestController
@RequestMapping("/api/zchatbot")
public class ChatBotController {
    @Autowired
    ChatBotService chatBotService;


    @GetMapping("/tags/list")
    public Mono<Iterable<Tags>> getListTag() {
        return Mono.just(chatBotService.getListTag());
    }

    @GetMapping("/tags/describer")
    public Mono<ApiResponse<Object>> getDescriberTag(@RequestParam("nameTag") String tag) {
        return Mono.just(chatBotService.getDescriberTag(tag));
    }

    @PostMapping("/collect")
    public Mono<ApiResponse<Object>> collectData(@RequestBody List<DataCollectRequest> data) {
        return Mono.just(chatBotService.collectData(data));
    }


}

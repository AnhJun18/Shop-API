package com.myshop.api.service.chatbot;


import com.myshop.api.payload.request.datacollect.DataCollectRequest;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.chatbot.entities.Tags;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ChatBotService {

    Iterable<Tags> getListTag();

    ApiResponse<Object> getDescriberTag(String tag);

    ApiResponse<Object> collectData(@RequestBody List<DataCollectRequest> data);
}

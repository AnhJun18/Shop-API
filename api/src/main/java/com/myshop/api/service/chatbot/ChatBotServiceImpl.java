package com.myshop.api.service.chatbot;

import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.payload.request.datacollect.DataCollectRequest;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.chatbot.entities.DataCollect;
import com.myshop.repositories.chatbot.entities.Tags;
import com.myshop.repositories.chatbot.repos.DataCollectRepository;
import com.myshop.repositories.chatbot.repos.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Transactional
@Service
public class ChatBotServiceImpl extends CRUDBaseServiceImpl<DataCollect, DataCollect, DataCollect, Long> implements ChatBotService {

    private final DataCollectRepository dataCollectRepository;
    @Autowired
    TagRepository tagRepository;

    public ChatBotServiceImpl(DataCollectRepository dataCollectRepository) {
        super(DataCollect.class, DataCollect.class, DataCollect.class, dataCollectRepository);
        this.dataCollectRepository = dataCollectRepository;
    }


    @Override
    public Iterable<Tags> getListTag() {
        return tagRepository.findAll();
    }


    @Override
    public ApiResponse<Object> getDescriberTag(String tag) {
        Optional<Tags> myTag=tagRepository.findByName(tag);
        if(myTag.isPresent())
            return ApiResponse.builder().data(myTag.get()).build();
        else
            return ApiResponse.builder().data(null).build();
    }

    @Transactional
    @Override
    public ApiResponse<Object> collectData(List<DataCollectRequest> data) {
        for (DataCollectRequest item:data) {
            DataCollect newLine= DataCollect.builder()
                    .request(item.getRequest())
                    .response(item.getResponse())
                    .build();
            dataCollectRepository.save(newLine);
        }
        return ApiResponse.builder().status(200).message("Dữ liệu đã được lưu").build();
    }
}

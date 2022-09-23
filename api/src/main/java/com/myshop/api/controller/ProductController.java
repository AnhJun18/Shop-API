package com.myshop.api.controller;


import com.myshop.api.config.FirebaseConfig;
import com.myshop.api.service.firebase.IImageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * @author Anh Jun
 * @author Anh Jun
 */

@Slf4j
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    IImageService imageService;
    @Autowired
    FirebaseConfig properties;


    @Transactional
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> upload(@RequestPart("image") FilePart filePart) throws IOException {
       String fileName =  imageService.save(filePart);
        return Mono.just(properties.prefixImageUrl.concat(fileName).concat(properties.suffixImageUrl));
    }

}

package com.myshop.api.payload.request.product;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.codec.multipart.FilePart;

@Data
@Builder
public class ProductRequest {
    private String name;
    private FilePart image;
    private String category;
    private String describe;
    private Integer price;
    private String tag;
}

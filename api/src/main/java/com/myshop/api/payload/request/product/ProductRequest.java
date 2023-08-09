package com.myshop.api.payload.request.product;

import lombok.Data;

import java.util.List;

@Data
public class ProductRequest {
    private String name;
    private String pictures;
    private String description;
    private List<String> categories;
}

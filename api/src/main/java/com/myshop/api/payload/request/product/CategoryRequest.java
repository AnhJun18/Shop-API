package com.myshop.api.payload.request.product;

import lombok.Data;

import java.util.List;

@Data
public class CategoryRequest {
    private String categoryCode;
    private String categoryName;
    private List<String> pictures;
}

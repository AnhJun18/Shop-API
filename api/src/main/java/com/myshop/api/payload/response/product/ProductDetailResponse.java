package com.myshop.api.payload.response.product;

import com.myshop.repositories.stocks.entities.StocksDetail;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductDetailResponse {
    private boolean status;
    private String message;
    private StocksDetail stocksDetail;
}

package com.myshop.api.payload.request.stockin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PreviewRequest {
    private String name;
    private String dataExcel;
}
package com.myshop.api.payload.request.stockin;

import com.myshop.api.payload.dtos.RequestSupplierDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RequestSupplierRequest {
    private String supplierId;
    private List<RequestSupplierDto> dataRequestSupplier;
}

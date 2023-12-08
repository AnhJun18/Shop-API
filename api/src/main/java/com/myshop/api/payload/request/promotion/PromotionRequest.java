package com.myshop.api.payload.request.promotion;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
public class PromotionRequest {
    private String promotionName;

    private String description;

    private Instant startDate;

    private Instant endDate;

    private List<ValuePromotionInPr> listApply;
}

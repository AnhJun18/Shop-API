package com.myshop.api.payload.request.promotion;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.Instant;

@Data
@NoArgsConstructor
public class PromotionRequest {
    private String name;

    private String description;

    @Min(1)
    @Max(100)
    private Integer value;

    private Instant startDate;

    private Instant endDate;
}

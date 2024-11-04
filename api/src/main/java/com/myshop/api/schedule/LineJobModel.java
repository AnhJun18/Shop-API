package com.myshop.api.schedule;

import com.google.auto.value.AutoValue.Builder;

import lombok.Data;

@Data
@Builder
public class LineJobModel {
    private String time;
    private String message;
    
}

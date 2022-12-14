package com.myshop.api.payload.request.datacollect;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataCollectRequest {
   private String request;
   private String response;
}

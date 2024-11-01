package com.myshop.api.payload.request.datacollect;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotifyRequest {
   private String token;
   private String message;
}

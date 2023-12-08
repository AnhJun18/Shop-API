package com.myshop.api.payload.request.fpgrowth;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class FPRequest {
  Integer minSup;
  Integer minConf;
  List<String> itemSelected;
  @Builder.Default
  Boolean isTest = false;
}

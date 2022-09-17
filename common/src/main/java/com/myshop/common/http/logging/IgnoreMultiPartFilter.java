package com.myshop.common.http.logging;

import org.springframework.http.MediaType;

public class IgnoreMultiPartFilter implements MediaTypeFilter {

  @Override
  public boolean logged(MediaType mediaType) {
    if (mediaType == null || !mediaType.includes(MediaType.MULTIPART_FORM_DATA)) {
      return true;
    }
    return false;
  }
}

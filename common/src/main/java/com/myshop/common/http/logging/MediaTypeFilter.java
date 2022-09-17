package com.myshop.common.http.logging;

import org.springframework.http.MediaType;

public interface MediaTypeFilter {

    default boolean logged(MediaType mediaType) {
        return true;
    }

}

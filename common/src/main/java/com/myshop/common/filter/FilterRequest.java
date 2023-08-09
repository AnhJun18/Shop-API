package com.myshop.common.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nullable;

@Data
@Builder
@AllArgsConstructor
public class FilterRequest {
    private String search ;
    private int page ;
    private int itemsPerPage;

    public FilterRequest() {
        this.search = "";
        this.page = 1;
        this.itemsPerPage = 10;
    }
}

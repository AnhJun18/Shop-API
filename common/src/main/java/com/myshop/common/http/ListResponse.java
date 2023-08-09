package com.myshop.common.http;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ListResponse<T> {
  private T items;
  private int totalItems;
  private int page;
  private int totalPages;
  private int itemsPerPage;

  public ListResponse(T items, int totalItems, int page, int itemsPerPage) {
    this.items = items;
    this.totalItems = totalItems;
    this.page = page;
    this.totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
    this.itemsPerPage = itemsPerPage;
  }
}

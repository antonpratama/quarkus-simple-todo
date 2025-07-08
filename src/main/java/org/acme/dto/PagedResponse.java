package org.acme.dto;

import java.util.List;

public class PagedResponse<T> {
  public long total;
  public int page;
  public int totalPage;
  public int size;
  public List<T> data;

  public PagedResponse(long total, int page, int size, List<T> data) {
    this.total = total;
    this.page = page;
    this.size = size;
    this.data = data;
  }

  public PagedResponse(long total, int page, int size, int totalPage, List<T> data) {
    this.total = total;
    this.page = page;
    this.size = size;
    this.totalPage = totalPage;
    this.data = data;
  }
}

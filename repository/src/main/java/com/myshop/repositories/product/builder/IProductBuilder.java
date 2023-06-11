package com.myshop.repositories.product.builder;

import com.myshop.repositories.product.entities.Category;
import com.myshop.repositories.product.entities.Product;

public interface IProductBuilder {

    ProductBuilder setName(String name);

    ProductBuilder setCategory(Category category);

    ProductBuilder setLinkImg(String url);

    ProductBuilder setDescribe(String describe);

    ProductBuilder setPrice(Integer price);

    ProductBuilder setSold(Long sold);

    ProductBuilder setDeleteFlag(Boolean isDeleted);

    Product build();
}

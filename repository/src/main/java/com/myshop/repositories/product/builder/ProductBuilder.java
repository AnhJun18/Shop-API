package com.myshop.repositories.product.builder;

import com.myshop.repositories.product.entities.Category;
import com.myshop.repositories.product.entities.Product;

public class ProductBuilder implements IProductBuilder {

    private String name;
    private Category category;
    private String linkImg;
    private String describe;
    private Integer price;
    private Long sold;
    private Boolean deleteFlag;

    @Override
    public ProductBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public ProductBuilder setCategory(Category category) {
        this.category = category;
        return this;
    }

    @Override
    public ProductBuilder setLinkImg(String url) {
        this.linkImg = url;
        return this;
    }

    @Override
    public ProductBuilder setDescribe(String describe) {
        this.describe = describe;
        return this;
    }

    @Override
    public ProductBuilder setPrice(Integer price) {
        this.price = price;
        return this;
    }

    @Override
    public ProductBuilder setSold(Long sold) {
        this.sold = sold;
        return this;
    }

    @Override
    public ProductBuilder setDeleteFlag(Boolean isDeleted) {
        this.deleteFlag = isDeleted;
        return this;
    }

    @Override
    public Product build() {
        return new Product(name, linkImg, category, describe, price, sold, deleteFlag);
    }
}

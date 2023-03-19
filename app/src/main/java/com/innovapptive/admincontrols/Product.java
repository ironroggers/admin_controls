package com.innovapptive.admincontrols;

public class Product {
    String productTitle;
    String category;
    String mrp;
    String sp;
    String id;
    String url;
    public Product(){

    }
    public Product(String productTitle, String category, String mrp, String sp, String id, String url) {
        this.productTitle = productTitle;
        this.category = category;
        this.mrp = mrp;
        this.sp = sp;
        this.id=id;
        this.url=url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getSp() {
        return sp;
    }

    public void setSp(String sp) {
        this.sp = sp;
    }
}

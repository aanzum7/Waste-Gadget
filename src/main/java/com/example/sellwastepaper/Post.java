package com.example.sellwastepaper;

import java.io.Serializable;
import java.util.Date;

public class Post implements Serializable {
    public String quantity,desc,address,phone,imgUrl,uploadBy;
    Date date;

    public Post() {
    }

    public Post(String quantity, String desc, String address, String phone, String imgUrl, String uploadBy, Date date) {
        this.quantity = quantity;
        this.desc = desc;
        this.address = address;
        this.phone = phone;
        this.imgUrl = imgUrl;
        this.uploadBy = uploadBy;
        this.date = date;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDesc() {
        return desc;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getUploadBy() {
        return uploadBy;
    }

    public Date getDate() {
        return date;
    }
}

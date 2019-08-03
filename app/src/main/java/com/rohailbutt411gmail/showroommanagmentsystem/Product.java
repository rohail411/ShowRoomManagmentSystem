package com.rohailbutt411gmail.showroommanagmentsystem;

public class Product {
    public String image;
    public String chasis_no;
    public String brand;
    public String model;
    public String engine_power;
    public String color;
    public String reg_no;
    public String document_status;


    public Product(){

    }
    public Product(String image,String chasis_no,String brand,String model,String engine_power,String color,String reg_no,String document_status){
        this.image = image;
        this.chasis_no = chasis_no;
        this.brand = brand;
        this.model = model;
        this.engine_power = engine_power;
        this.color = color;
        this.document_status = document_status;
    }

    public String getDocument_status() {
        return document_status;
    }

    public void setDocument_status(String document_status) {
        this.document_status = document_status;
    }

    public String getReg_no() {
        return reg_no;
    }

    public void setReg_no(String reg_no) {
        this.reg_no = reg_no;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getChasis_no() {
        return chasis_no;
    }

    public void setChasis_no(String chasis_no) {
        this.chasis_no = chasis_no;
    }

    public String getEngine_power() {
        return engine_power;
    }

    public void setEngine_power(String engine_power) {
        this.engine_power = engine_power;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}

package com.rohailbutt411gmail.showroommanagmentsystem;

import java.util.Date;

public class Sold {
    public String chasis_no;
    public int profit;
    public String image;

    public String buyer_name;
    public String reg_no;
    public int sell_amount;
    public String brand;
    public String model;
    public String color;
    public int buy_amount;
    public int remaining_amount;
    public String user;
    public Date date;


    public Sold(){}

    public Sold(String chasis_no,int profit,String image,String buyer_name,String reg_no,int sell_amount,String brand,String model,String color,Date date
    ,String user,int buy_amount,int remaining_amount){
        this.chasis_no = chasis_no;
        this.profit = profit;
        this.image = image;
        this.buyer_name = buyer_name;
        this.reg_no = reg_no;
        this.sell_amount = sell_amount;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.date = date;
        this.user = user;
        this.remaining_amount = remaining_amount;
        this.buy_amount = buy_amount;
    }

    public int getBuy_amount() {
        return buy_amount;
    }

    public String getUser() {
        return user;
    }

    public int getRemaining_amount() {
        return remaining_amount;
    }

    public Date getDate() {
        return date;
    }

    public int getSell_amount() {
        return sell_amount;
    }

    public String getReg_no() {
        return reg_no;
    }

    public String getColor() {
        return color;
    }

    public String getModel() {
        return model;
    }

    public String getBrand() {
        return brand;
    }

    public String getBuyer_name() {
        return buyer_name;
    }


    public String getChasis_no() {
        return chasis_no;
    }

    public String getImage() {
        return image;
    }

    public int getProfit() {
        return profit;
    }

    public void setChasis_no(String chasis_no) {
        this.chasis_no = chasis_no;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setReg_no(String reg_no) {
        this.reg_no = reg_no;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setBuyer_name(String buyer_name) {
        this.buyer_name = buyer_name;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setSell_amount(int sell_amount) {
        this.sell_amount = sell_amount;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setBuy_amount(int buy_amount) {
        this.buy_amount = buy_amount;
    }

    public void setRemaining_amount(int remaining_amount) {
        this.remaining_amount = remaining_amount;
    }
}

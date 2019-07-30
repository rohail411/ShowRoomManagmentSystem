package com.rohailbutt411gmail.showroommanagmentsystem;

import java.util.Date;

public class Expense {
    public int amount;
    public String user;
    public String detail;
    public Date date;
    public String expense_type;


    public Expense(){}

    public Expense(int amount,Date date,String user,String detail,String expense_type){
        this.amount = amount;
        this.date = date;
        this.user = user;
        this.detail = detail;
        this.expense_type = expense_type;
    }

    public String getUser() {
        return user;
    }

    public String getDetail() {
        return detail;
    }

    public String getExpense_type() {
        return expense_type;
    }

    public Date getDate() {
        return date;
    }

    public int getAmount() {
        return amount;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setExpense_type(String expense_type) {
        this.expense_type = expense_type;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

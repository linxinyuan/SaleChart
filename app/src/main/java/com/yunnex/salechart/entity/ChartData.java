package com.yunnex.salechart.entity;

/**
 * Created by Venco on 2016/9/8.
 */

public class ChartData {
    private int sale;
    private String date;

    public ChartData(String date, int sale) {
        this.date = date;
        this.sale = sale;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSale() {
        return sale;
    }

    public void setSale(int sale) {
        this.sale = sale;
    }
}

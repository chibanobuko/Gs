//1つのセルにあるデータを保存するためのデータクラスです。
package com.example.chiba.gs;

import java.util.ArrayList;

public class MessageRecord {
    //保存するデータ全てを変数で定義します。
    private String imageUrl;
    private String comment;
    private String startDate;
    private String endDate;
    private String ingredients;
    private String price;

    /*//データを１つ作成する関数です。項目が増えたら増やしましょう。
    public MessageRecord(String imageUrl, String comment) {
        this.imageUrl = imageUrl;
        this.comment = comment;
    }
    //それぞれの項目を返す関数です。項目が増えたら増やしましょう。
    public String getComment() {
        return comment;
    }
    public String getImageUrl() {
        return imageUrl;
    }*/

    public String getImageUrl() { return imageUrl; }
    public String getComment() { return comment; }/*
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getIngredients() { return ingredients; }
    public String getPrice() { return price; }*/

   /* public MessageRecord(String imageUrl, String comment, String startDate, String endDate, String ingredients, String price ) {
        this.imageUrl = imageUrl;
        this.comment = comment;
        this.startDate = startDate;
        this.endDate = endDate;
        this.ingredients = ingredients;
        this.price = price;
    }*/

    public MessageRecord(String imageUrl, String comment ) {
        this.imageUrl = imageUrl;
        this.comment = comment;
    }
}

package com.cactime.api;

/**
 * Created by andy on 2018/3/1.
 */

public class PastData {

    private String itemName;
    private int itemYear;
    private int itemMonth;
    private int itemDay;
    private boolean itemIsTop;
    private boolean itemIsPush;
    private Long itemAllDay;

    public String getItemName(){
        return  itemName;
    }

    public void setItemName(String itemName){
        this.itemName = itemName;
    }

    public int getItemYear(){
        return  itemYear;
    }

    public void setItemYear(int itemYear){
        this.itemYear = itemYear;
    }

    public int getItemMonth(){
        return  itemMonth;
    }

    public void setItemMonth(int itemMonth){
        this.itemMonth = itemMonth;
    }

    public int getItemDay(){
        return  itemDay;
    }

    public void setItemDay(int itemDay){
        this.itemDay = itemDay;
    }

    public boolean getItemIsTop(){
        return  itemIsTop;
    }

    public void setItemIsTop(boolean itemIsTop){
        this.itemIsTop = itemIsTop;
    }

    public boolean getItemIsPush(){
        return  itemIsPush;
    }

    public void setItemIsPush(boolean itemIsPush){
        this.itemIsPush = itemIsPush;
    }

    public Long getItemAllDay(){
        return  itemAllDay;
    }

    public void setItemAllDay(Long itemAllDay){
        this.itemAllDay = itemAllDay;
    }
}

package com.cactime.itemclass;

import java.util.ArrayList;
import java.util.List;

public class UserData {

    private String uid;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String userName;
    private String Sex;
    private boolean isYear;
    private String Desire;
    private ArrayList<DesireData> DesireList;

    public String getuid() {
        return uid;
    }

    public void setuid(String uid) {
        this.uid = uid;
    }

    public int getmYear() {
        return mYear;
    }

    public void setmYear(int mYear) {
        this.mYear = mYear;
    }

    public int getmMonth() {
        return mMonth;
    }

    public void setmMonth(int mMonth) {
        this.mMonth = mMonth;
    }

    public int getmDay() {
        return mDay;
    }

    public void setmDay(int mDay) {
        this.mDay = mDay;
    }

    public String getuserName() {
        return userName;
    }

    public void setuserName(String userName) {
        this.userName = userName;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String Sex) {
        this.Sex = Sex;
    }

    public boolean getisYear() {
        return isYear;
    }

    public void setisYear(boolean isYear) {
        this.isYear = isYear;
    }

    public String getDesire() {
        return Desire;
    }

    public void setDesire(String Desire) {
        this.Desire = Desire;
    }

    public ArrayList<DesireData> getDesireList() {
        return DesireList;
    }

    public void setDesireList(ArrayList<DesireData> DesireList) {
        this.DesireList = DesireList;
    }

}

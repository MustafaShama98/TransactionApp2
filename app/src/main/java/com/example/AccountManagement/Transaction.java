package com.example.AccountManagement;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"amount","description","type","deleted","day","month","year","hour","minute"})
public class Transaction {

    @NonNull
    @ColumnInfo(name = "amount")
    public
    int amount;

    @NonNull
    @ColumnInfo(name = "description")
    String description;

    @NonNull
    @ColumnInfo(name = "type")
    String type;

    @NonNull
    @ColumnInfo(name = "deleted")
    Boolean deleted;

    @ColumnInfo(name = "year")
    int  year;

    @ColumnInfo(name = "month")
    int  month;

    @ColumnInfo(name = "day")
    int  day;

    @ColumnInfo(name = "hour")
    int hour;

    @ColumnInfo(name = "minute")
    int minute;


    public Transaction(int amount, String description, String type,  Boolean deleted,int day, int month, int year, int hour, int minute) {
        this.amount = amount;
        this.description = description;
        this.deleted = deleted;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.type = type;
    }

    public Boolean getDeleted() {
        return deleted;
    }
    public void setDeleted(Boolean deleted){
        this.deleted = deleted;
    }

    public int getTitle() {
        return amount;
    }

    public void setTitle(int title) {
        this.amount = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getType() {
        return type;
    }

    public void String(String type) {
        this.type = type;
    }



    @Override
    public String toString() {
        return "Transaction{" +
                "amount='" + amount + '\'' +
                ", description='" + description + '\'' +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minute=" + minute +
                '}';
    }
}

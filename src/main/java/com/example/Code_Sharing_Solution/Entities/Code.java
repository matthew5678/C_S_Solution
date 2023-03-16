package com.example.Code_Sharing_Solution.Entities;


import jakarta.persistence.*;

import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity(name = "Code")
public class Code {


    @Id
    //@GeneratedValue(strategy= GenerationType.AUTO)
    @Column
    private String id;

    //@NotNull
    private String code;

    //@NotNull
    //@UpdateTimestamp causes error
    private LocalDateTime date;

    private long time;

    private long views;

    private boolean isTimeAvailable;

    private boolean isViewsAvailable;

    public Code(){

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        var val = date.format(formatter);

//        return LocalDateTime.parse(val, formatter);
        return val;
    }

    public void setDate(LocalDateTime date) {
        final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateTime = date.format(FORMATTER).toString();
        System.out.println("dateTime string : " + dateTime);

        this.date = LocalDateTime.parse(dateTime, FORMATTER);;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public boolean isTimeAvailable() {
        return isTimeAvailable;
    }

    public void setTimeAvailable(boolean timeAvailable) {
        isTimeAvailable = timeAvailable;
    }

    public boolean isViewsAvailable() {
        return isViewsAvailable;
    }

    public void setViewsAvailable(boolean viewsAvailable) {
        isViewsAvailable = viewsAvailable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Code code1)) return false;
        return id == code1.id && code.equals(code1.code) && date.equals(code1.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, date);
    }


    @Override
    public String toString() {
        final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        var formattedDate = date.format(formatter);

        return "{\n" +
                "code: " + code + "\n" +
                "date: " + date + "\n" +
                '}';
    }

//    public String getDateString() {
//        final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
//        return date.format(formatter);
//    }
}

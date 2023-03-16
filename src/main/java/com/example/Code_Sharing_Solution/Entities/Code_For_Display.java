package com.example.Code_Sharing_Solution.Entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Code_For_Display {


    //@NotNull
    private String code;

    //@NotNull
    @UpdateTimestamp
    private LocalDateTime date;

    private long time;

    private long views;


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

}

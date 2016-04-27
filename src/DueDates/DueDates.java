/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DueDates;

/**
 *
 * @author hallm8
 */
public class DueDates {
    private String contentName;
    private String monthDayYear;
    private String time;

    public DueDates(String contentName, String monthDayYear, String time) {
        this.contentName = contentName;
        this.monthDayYear = monthDayYear;
        this.time = time;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public String getMonthDayYear() {
        return monthDayYear;
    }

    public void setMonthDayYear(String monthDayYear) {
        this.monthDayYear = monthDayYear;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    
    
    
    
}

package com.example.eventapp.calendar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.eventapp.entities.Event;

public class Month {
    public int year;
    public int month;
    private int maxOfMonth;
    private int startOfMonth;
    private List<Date> days;

    public Month(int year, int month) {
        this.days = new ArrayList<Date>();
        this.year = year;
        this.month = month;
        this.maxOfMonth = getMaxDay(year, month);
    }

    public int getMaxDay(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 01);
        return cal.getActualMaximum(Calendar.DATE);
    }

    // 日にちを設定
    public void setDate() {
        // 月始まりの曜日を確認
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate endDay = LocalDate.of(year, month, getMaxDay(year, month));
        int firstWeekOfDay = firstDay.getDayOfWeek().getValue();

        // 前月表示用
        if (firstWeekOfDay != 7) {
            for (int d = firstWeekOfDay; d > 0; d--) {
                days.add(new Date(firstDay.minusDays(d)));
            }
        }
        // for (int d = 7 - firstWeekOfDay; d > 0; d--) {
        // days.add(new Date(firstDay.minusDays(d)));
        // }

        startOfMonth = days.size();

        // リストを作成
        // int maxDay = getMaxDay(year, month);
        for (int d = 0; d < maxOfMonth; d++) {
            days.add(new Date(firstDay.plusDays(d), true));
        }
        // 次月表示用
        for (int d = 1; d < 41 - days.size(); d++) {
            days.add(new Date(endDay.plusDays(d)));
        }
    }

    public void setEvent(List<Event> events) {
        for (Event event : events) {
            int day = event.getEventDate().getDayOfMonth() + startOfMonth - 1;
            Date eventDay = days.get(day);
            eventDay.setEvent(event);
            days.set(day, eventDay);
        }
    }

    // リスト取得
    public List<Date> getDays() {
        return days;
    }

    public List<Date> getWeek(int weekNum) {
        int startOf = weekNum * 7;
        return days.subList(startOf, startOf + 7);
    }

    public LocalDate getFirstLocalDate() {
        return days.get(0).getLocalDate();
    }

    public LocalDate getLastLocalDate() {
        return days.get(days.size() - 1).getLocalDate();
    }

    // 次の月
    public int getNextMonthValue() {
        if (month == 12) {
            return 1;
        }
        return month + 1;
    }

    // 前の月
    public int getPreviousMonthValue() {
        if (month == 1) {
            return 12;
        }
        return month - 1;
    }

    // 次の月の年
    public int getNextMonthYearValue() {
        if (month == 12) {
            return year + 1;
        }
        return year;
    }

    // 前の月の年
    public int getPreviousMonthYearValue() {
        if (month == 1) {
            return year - 1;
        }
        return year;
    }

}

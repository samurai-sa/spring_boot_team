package com.example.eventapp.calendar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.eventapp.entities.Event;

public class Date {
    private LocalDate date;
    private int weekOfDay;
    public List<Event> events;
    public boolean isThisMonth = false;
    public boolean hasEvents = false;

    // public Date(int year, int month, int dayOfMonth) {
    // this.date = LocalDate.of(year, month, dayOfMonth);
    // }

    public Date(LocalDate date) {
        this.date = date;
        this.events = new ArrayList<Event>();
        this.weekOfDay = date.getDayOfWeek().getValue();
    }

    public Date(LocalDate date, boolean isThisMonth) {
        this.date = date;
        this.events = new ArrayList<Event>();
        this.weekOfDay = date.getDayOfWeek().getValue();
        this.isThisMonth = true;
    }

    public int getDayOfMonth() {
        return date.getDayOfMonth();
    }

    public void setEvent(Event event) {
        events.add(event);
        hasEvents = true;
    }

    public int getWeekOfDay() {
        return weekOfDay;
    }

    public LocalDate getLocalDate() {
        return date;
    }

}

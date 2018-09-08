package com.suckow.lifelog.model;

import java.util.List;
import java.util.UUID;

public class logbook {
    String bookId;
    String bookTitle;
    String lastUpdate;
    List<LogItem> logItems;

    public logbook() {}

    public logbook(String title, String lastUpdate) {
        this.bookId = UUID.randomUUID().toString();
        this.bookTitle = title;
        this.lastUpdate = lastUpdate;
    }

    public logbook(String title, String lastUpdate, List<LogItem> logItems) {
        this.bookId = UUID.randomUUID().toString();
        this.bookTitle = title;
        this.lastUpdate = lastUpdate;
        this.logItems = logItems;
    }

    public void addLogItem(LogItem item) {
        logItems.add(item);
    }

    public void removeLogItemIndex(int i) {
        logItems.remove(i);
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public List<LogItem> getLogItems() {
        return logItems;
    }

    public void setLogItems(List<LogItem> logItems) {
        this.logItems = logItems;
    }
}

package com.ermolov.rss;

public class TickerItem {
    private final String title;
    private final String link;
    private final String description;
    private final String guid;

    public TickerItem(String title, String link, String description, String guid) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.guid = guid;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getGuid() {
        return guid;
    }

    @Override
    public String toString() {
        return "TickerItem{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", guid='" + guid + '\'' +
                '}';
    }
}
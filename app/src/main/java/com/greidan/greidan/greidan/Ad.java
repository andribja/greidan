package com.greidan.greidan.greidan;

import java.util.Date;

/**
 * Created by Daníel on 01/03/2016.
 */
public class Ad {

    String title;
    String content;
    String category;
    User author;
    Date timePosted;

    public Ad(String title, String content, String category, User author) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }

    public User getAuthor() {
        return author;
    }

    public Date getTimePosted() {
        return timePosted;
    }
}

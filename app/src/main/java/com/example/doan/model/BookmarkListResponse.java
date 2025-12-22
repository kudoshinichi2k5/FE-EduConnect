package com.example.doan.model;

import java.util.List;

public class BookmarkResponse {
    private int count;
    private List<Opportunity> bookmarks;

    public List<Opportunity> getBookmarks() {
        return bookmarks;
    }
}

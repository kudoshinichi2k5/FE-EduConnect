package com.example.doan.model;

import java.util.List;

public class BookmarkListResponse {

    private int count;
    private List<BookmarkItem> bookmarks;

    public int getCount() {
        return count;
    }

    public List<BookmarkItem> getBookmarks() {
        return bookmarks;
    }
}

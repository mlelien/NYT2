package com.example.emilyl.nyt;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by emilyl on 6/23/16.
 */

@Parcel
public class SearchFilters {
    private String date = "";
    private String sort = "";
    private ArrayList<String> newsCategory = new ArrayList<>();

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSort() {
        return sort;
    }

    public ArrayList<String> getNewsCategory() {
        return newsCategory;
    }
    public void addNewsCategory(String category) {
        newsCategory.add(category);
    }

    public void removeNewsCategory(String category) {
        newsCategory.remove(category);
    }
}

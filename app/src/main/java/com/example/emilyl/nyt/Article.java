package com.example.emilyl.nyt;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by emilyl on 6/21/16.
 */
@Parcel
public class Article {
    String webURL;
    String headline;
    String thumbNail;

    int year;
    int month;
    int day;

    public Article() {}

    public Article(JSONObject jsonObject) {
        try {
            this.webURL = jsonObject.optString("web_url");
            if (this.webURL.equals(""))
                this.webURL = jsonObject.getString("url");

            JSONObject headlineObject = jsonObject.optJSONObject("headline");
            if (headlineObject != null)
                this.headline = headlineObject.optString("main");
            else
                this.headline = jsonObject.getString("title");

            JSONArray multimediaArray = jsonObject.getJSONArray("multimedia");

            if (multimediaArray.length() > 0) {
                String url = multimediaArray.getJSONObject(0).getString("url");
                if (!url.contains("http"))
                    this.thumbNail = "http://nytimes.com/" + url;
                else
                    this.thumbNail = url;

            } else
                this.thumbNail = "";

            String dateArticle =  jsonObject.optString("pub_date");
            if (dateArticle.equals(""))
                dateArticle = jsonObject.getString("published_date");
            this.year = Integer.parseInt(dateArticle.substring(0, 4));
            this.month = Integer.parseInt(dateArticle.substring(5, 7));
            this.day = Integer.parseInt(dateArticle.substring(8, 10)) - 1;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJSONArray (JSONArray jsonArray) {
        ArrayList<Article> articles = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                 articles.add(new Article(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return articles;
    }

    public String getWebURL() {
        return webURL;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbNail() {
        return thumbNail;
    }
}

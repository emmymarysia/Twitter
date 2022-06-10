package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {

    public String body;
    public String createdAt;
    public User user;
    public String mediaUrl;
    public boolean hasMedia;
    public String relTime;
    public String tweetId;
    public boolean isFavorited;
    public boolean isRetweeted;
    public int favoriteCount;
    public int retweetCount;
    public int replyCount;

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public Tweet() {
    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("retweeted_status")) {
            return null;
        }

        Tweet tweet = new Tweet();
        if (jsonObject.has("full_text")) {
            tweet.body = jsonObject.getString("full_text");
        } else {
            tweet.body = jsonObject.getString("text");
        }
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        JSONArray media = jsonObject.getJSONObject("entities").optJSONArray("media");
        if (media != null) {
            tweet.mediaUrl = media.getJSONObject(0).getString("media_url_https");
            tweet.hasMedia = true;
        } else {
            tweet.mediaUrl = "";
            tweet.hasMedia = false;
        }
        tweet.relTime = tweet.getRelativeTimeAgo(tweet.createdAt);
        tweet.tweetId = jsonObject.getString("id_str");
        tweet.isFavorited = jsonObject.getBoolean("favorited");
        tweet.isRetweeted = jsonObject.getBoolean("retweeted");
        tweet.favoriteCount = jsonObject.getInt("favorite_count");
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        tweet.replyCount = jsonObject.getInt("retweet_count");
        return tweet;
    }

    public String getRelativeTimeAgo(String jsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(jsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "- just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "- a minute ago";
            } else if (diff < 60 * MINUTE_MILLIS) {
                return "- " + diff / MINUTE_MILLIS + "m";
            } else if (diff < 24 * HOUR_MILLIS) {
                return "- " + diff / HOUR_MILLIS + "h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "- yesterday";
            } else {
                return "- " + diff / DAY_MILLIS + "d";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            Tweet newTweet = fromJson(jsonArray.getJSONObject(i));
            // to skip retweets and only add original tweets to timeline
            if (newTweet != null) {
                tweets.add(newTweet);
            }
        }
        return tweets;
    }
}

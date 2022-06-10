package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        ImageView ivMedia;
        TextView tvRelTime;
        ImageButton ibFavorite;
        TextView tvFavoriteCount;
        ImageButton ibRetweet;
        TextView tvRetweetCount;
        ImageButton ibReply;
        TextView tvReplyCount;
        TextView tvUsername;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            tvRelTime = itemView.findViewById(R.id.tvRelTime);
            ibFavorite = itemView.findViewById(R.id.ibFavorite);
            tvFavoriteCount = itemView.findViewById(R.id.tvFavoriteCount);
            ibRetweet = itemView.findViewById(R.id.ibRetweet);
            tvRetweetCount = itemView.findViewById(R.id.tvRetweetCount);
            ibReply = itemView.findViewById(R.id.ibReply);
            tvReplyCount = itemView.findViewById(R.id.tvReplyCount);
            tvUsername = itemView.findViewById(R.id.tvUsername);

            itemView.setOnClickListener(this);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.name);
            tvRelTime.setText(tweet.relTime);
            tvFavoriteCount.setText(Integer.toString(tweet.favoriteCount));
            tvRetweetCount.setText(Integer.toString(tweet.retweetCount));
            tvReplyCount.setText(Integer.toString(tweet.replyCount));
            tvUsername.setText("@" + tweet.user.screenName);

            Drawable replyImage = context.getDrawable(android.R.drawable.stat_notify_chat);
            ibReply.setImageDrawable(replyImage);

            if (tweet.isFavorited) {
                Drawable favoriteImage = context.getDrawable(R.drawable.ic_vector_heart);
                ibFavorite.setImageDrawable(favoriteImage);
            } else {
                Drawable unfavoriteImage = context.getDrawable(R.drawable.ic_vector_heart_stroke);
                ibFavorite.setImageDrawable(unfavoriteImage);
            }

            if (tweet.isRetweeted) {
                Drawable retweetImage = context.getDrawable(R.drawable.ic_vector_retweet);
                ibRetweet.setImageDrawable(retweetImage);
            } else {
                Drawable retweetImage = context.getDrawable(R.drawable.ic_vector_retweet_stroke);
                ibRetweet.setImageDrawable(retweetImage);
            }

            ibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ComposeActivity.class);
                    i.putExtra("isReplyToTweet", true);
                    i.putExtra("idOfTweetToReplyTo", tweet.tweetId);
                    i.putExtra("usernameToReplyTo", tweet.user.screenName);
                    ((Activity) context).startActivityForResult(i, TimelineActivity.REQUEST_CODE);
                    tweet.replyCount += 1;
                    tvReplyCount.setText(Integer.toString(tweet.replyCount));
                }
            });

            Glide.with(context).load(tweet.user.profileImageUrl).centerInside().transform(new RoundedCorners(90)).into(ivProfileImage);
            int radius = 30;
            if (tweet.hasMedia) {
                Glide.with(context).load(tweet.mediaUrl).centerInside().transform(new RoundedCorners(radius)).into(ivMedia);
                ivMedia.setVisibility(View.VISIBLE);
            } else {
                ivMedia.setVisibility(View.GONE);
            }

            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!tweet.isFavorited) {
                        // tell Twitter I want to favorite this
                        TwitterApp.getRestClient(context).favoriteTweet(tweet.tweetId, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i("adapter", "Tweet favorited");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e("adapter", "Tweet favorite failed");
                            }
                        });
                        // change the drawable to clicked
                        tweet.isFavorited = true;
                        Drawable favoriteImage = context.getDrawable(R.drawable.ic_vector_heart);
                        ibFavorite.setImageDrawable(favoriteImage);
                        tvFavoriteCount.setText(Integer.toString(tweet.favoriteCount + 1));
                        tweet.favoriteCount += 1;
                    } else {
                        // tell Twitter I want to unfavorite this
                        TwitterApp.getRestClient(context).unfavoriteTweet(tweet.tweetId, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i("adapter", "Tweet unfavorited");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e("adapter", "Tweet favorite failed");
                            }
                        });
                        // change the drawable back to unclicked
                        tweet.isFavorited = false;
                        Drawable unfavoriteImage = context.getDrawable(R.drawable.ic_vector_heart_stroke);
                        ibFavorite.setImageDrawable(unfavoriteImage);
                        tvFavoriteCount.setText(Integer.toString(tweet.favoriteCount - 1));
                        tweet.favoriteCount -= 1;
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            //get item position
            int position = getAdapterPosition();
            //make sure position is valid/exists in the view
            if(position != RecyclerView.NO_POSITION) {
                //get the Movie at that position in the list
                Tweet tweet = tweets.get(position);
                //create an intent for the new activity
                Intent intent = new Intent(context, TweetDetailActivity.class);
                //serialize the movie using parceler, use its short name as a key
                intent.putExtra("tweet", Parcels.wrap(tweet));
                //show the activity
                context.startActivity(intent);
            }
        }
    }
}

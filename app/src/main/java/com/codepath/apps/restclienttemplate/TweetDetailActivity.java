package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;
import org.w3c.dom.Text;

public class TweetDetailActivity extends AppCompatActivity {

    Tweet tweet;

    ImageView ivDetailProfileImage;
    TextView tvDetailName;
    TextView tvDetailScreenname;
    TextView tvDetailRelTime;
    TextView tvDetailBody;
    ImageView ivDetailMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        ivDetailProfileImage = findViewById(R.id.ivDetailProfileImage);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailScreenname = findViewById(R.id.tvDetailScreenname);
        tvDetailRelTime = findViewById(R.id.tvDetailRelTime);
        tvDetailBody = findViewById(R.id.tvDetailBody);
        ivDetailMedia = findViewById(R.id.ivDetailMedia);

        tvDetailName.setText(tweet.user.name);
        tvDetailScreenname.setText(tweet.user.screenName);
        tvDetailRelTime.setText(tweet.relTime);
        tvDetailBody.setText(tweet.body);
        Glide.with(this).load(tweet.user.profileImageUrl).centerInside().transform(new RoundedCorners(90)).into(ivDetailProfileImage);
        int radius = 30;
        if (tweet.hasMedia) {
            Glide.with(this).load(tweet.mediaUrl).centerInside().transform(new RoundedCorners(radius)).into(ivDetailMedia);
            ivDetailMedia.setVisibility(View.VISIBLE);
        } else {
            ivDetailMedia.setVisibility(View.GONE);
        }

    }
}
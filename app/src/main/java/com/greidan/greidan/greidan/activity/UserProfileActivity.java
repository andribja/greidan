package com.greidan.greidan.greidan.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.greidan.greidan.greidan.R;
import com.greidan.greidan.greidan.model.Review;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class UserProfileActivity extends ProgressActivity {

    private static final String TAG = "UserProfileActivity";

    HashMap<String, Review> reviews;

    TextView mUsernameView;
    TextView mEmailView;
    TextView mMemberSinceView;
    TextView mRatingView;
    ListView mReviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mUsernameView = (TextView) findViewById(R.id.label_username);
        mEmailView = (TextView) findViewById(R.id.label_email);
        mMemberSinceView = (TextView) findViewById(R.id.label_member_since);
        mRatingView = (TextView) findViewById(R.id.label_user_rating);

        // TODIO: get user information somehow
        mUsernameView.setText("foobar");
        mEmailView.setText("foo@bar.com");
        mMemberSinceView.setText(String.format(getString(R.string.member_since), "16. apr 2016"));
        mRatingView.setText(String.format(getString(R.string.user_rating), 3.5));

        mReviewList = (ListView) findViewById(R.id.review_list);
        mReviewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "You clicked somthing");
            }
        });

        // TODO: get reviews from server
        ArrayList<Review> foo = new ArrayList<>();
        foo.add(new Review("asdf", "foo", "bar", 4.5, new Date()));
        foo.add(new Review("fdsa", "oof", "foo", 1, new Date()));
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("reviews", foo);
        doUponCompletion(bundle);

    }

    @Override
    public void doUponCompletion(Bundle data) {
        List<Review> reviewList = data.getParcelableArrayList("reviews");

        reviews = new HashMap<>();
        for(Review review: reviewList) {
            reviews.put(review.getId(), review);
        }

        mReviewList.setAdapter(new ReviewAdapter(this, reviewList));
    }

    private class ReviewAdapter extends ArrayAdapter<Review> {

        public ReviewAdapter(Context context, List<Review> reviews) {
            super(context, 0, reviews);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Review review = getItem(position);

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_review, parent, false);
            }

            TextView title = (TextView) convertView.findViewById(R.id.review_item_title);
            RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.review_item_rating);

            title.setText(review.getTitle());
            ratingBar.setRating((float) review.getRating());

            convertView.setTag(review.getId());

            return convertView;
        }

    }
}

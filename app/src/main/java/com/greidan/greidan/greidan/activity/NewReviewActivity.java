package com.greidan.greidan.greidan.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.greidan.greidan.greidan.R;
import com.greidan.greidan.greidan.manager.ReviewManager;
import com.greidan.greidan.greidan.manager.UserManager;
import com.greidan.greidan.greidan.model.Review;

import java.util.Date;

public class NewReviewActivity extends ProgressActivity {

    private static final String TAG = "NewReviewActivity";

    ReviewManager reviewManager;
    UserManager userManager;

    TextView mRevieweeLabel;
    EditText mContent;
    RatingBar mRatingBar;
    Button mButtonPost;
    Button mButtonCancel;

    String revieweeName;
    Review newReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_review);

        reviewManager = new ReviewManager(this);
        userManager = new UserManager(this);

        mContainerView = findViewById(R.id.new_review_container);
        mProgressView = findViewById(R.id.new_review_progress);

        revieweeName = getIntent().getStringExtra("revieweeName");

        mRevieweeLabel = (TextView) findViewById(R.id.new_reviewee_label);
        mRevieweeLabel.setText(getString(R.string.new_review_label, revieweeName));
        mContent = (EditText) findViewById(R.id.new_review_content);
        mRatingBar = (RatingBar) findViewById(R.id.new_review_rating);
        mButtonPost = (Button) findViewById(R.id.button_post_review);
        mButtonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mContent.getText().toString();
                double rating = mRatingBar.getRating();

                attemptPostReview(revieweeName, content, rating);
            }
        });

        mButtonCancel = (Button) findViewById(R.id.button_review_cancel);
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void attemptPostReview(String revieweeName, String content, double rating) {
        // TODO: validate input?
        newReview = new Review(null, rating, content, revieweeName, userManager.getLoggedInUsername(), new Date());

        showProgress(true);
        reviewManager.postReviewToServer(newReview);
    }

    @Override
    public void doUponCompletion(Bundle response) {
        Log.i(TAG, "doUponCompletion");
        Log.i(TAG, "Response: " + response);

        String message = response.getString("message");
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        if(response.getBoolean("success")) {
            finish();
        }
    }
}

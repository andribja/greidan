package com.greidan.greidan.greidan.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.greidan.greidan.greidan.R;
import com.greidan.greidan.greidan.manager.UserManager;
import com.greidan.greidan.greidan.model.Review;
import com.greidan.greidan.greidan.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class UserProfileActivity extends ProgressActivity {

    private static final String TAG = "UserProfileActivity";

    UserManager userManager;

    User user;
    HashMap<String, Review> reviews;

    ImageView mImageView;
    TextView mUsernameView;
    TextView mEmailView;
    TextView mMemberSinceView;
    TextView mRatingView;
    ListView mReviewList;
    Button mSendMessageButton;
    Button mAddReviewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        String username = getIntent().getStringExtra("username");

        userManager = new UserManager(this);
        //userManager.fetchUserProfileByUsername(username);

        mImageView = (ImageView) findViewById(R.id.image_profile);
        mUsernameView = (TextView) findViewById(R.id.label_username);
        mEmailView = (TextView) findViewById(R.id.label_email);
        mMemberSinceView = (TextView) findViewById(R.id.label_member_since);
        mRatingView = (TextView) findViewById(R.id.label_user_rating);

        mSendMessageButton = (Button) findViewById((R.id.button_message));
        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, NewMessageActivity.class);
                startActivity(intent);
            }
        });
        mAddReviewButton = (Button) findViewById((R.id.button_review));

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
        bundle.putParcelableArrayList("reviewlist", foo);
        doUponCompletion(bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String profileImagePath = userManager.getProfileImagePath();

        if(profileImagePath != null) {
            mImageView.setImageBitmap(BitmapFactory.decodeFile(profileImagePath));
        } else {
            mImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher, null));
        }
    }

    @Override
    public void doUponCompletion(Bundle response) {
        Log.i(TAG, "doUponCompletion");

        if(response.containsKey("reviewlist")) {
            List<Review> reviewList = response.getParcelableArrayList("reviewlist");

            reviews = new HashMap<>();
            for(Review review: reviewList) {
                reviews.put(review.getId(), review);
            }

            mReviewList.setAdapter(new ReviewAdapter(this, reviewList));
        }

        if(response.containsKey("users") || true) {
            // Temporary until users can be retrieved from server
//            List<User> userList = response.getParcelableArrayList("userlist");
            List<User> userList = new ArrayList<>();
            userList.add(new User("123", "foobar", "foo@bar.com", new Date(), 3.5));
            if(userList.size() ==1) {
                user = userList.get(0);

                mUsernameView.setText(user.getUsername());
                mEmailView.setText(user.getEmail());
                mMemberSinceView.setText(String.format(getString(R.string.member_since), user.getTimeJoined()));
                mRatingView.setText(String.format(getString(R.string.user_rating), user.getRating()));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_edit_profile) {

            Bundle bundle = new Bundle();
            bundle.putParcelable("user", user);
            Intent intent = new Intent(UserProfileActivity.this, EditUserProfileActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
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

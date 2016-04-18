package com.greidan.greidan.greidan.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.greidan.greidan.greidan.R;
import com.greidan.greidan.greidan.util.FileUtil;
import com.greidan.greidan.greidan.util.RealPathUtil;
import com.greidan.greidan.greidan.manager.UserManager;
import com.greidan.greidan.greidan.model.User;

import java.io.File;
import java.io.IOException;

public class EditUserProfileActivity extends ProgressActivity {

    private static final String TAG = "EditProfileActivity";
    private static final int SELECT_PICTURE = 1;

    UserManager userManager;

    User user;

    private String pendingImagePath;
    private String imagePath;
    private String extFilename;

    ImageView mImagePicker;
    EditText mUsername;
    EditText mEmail;
    EditText mOldPass;
    EditText mNewPass;
    EditText mConfirmPass;
    Button mUpdateButton;
    Button mCancelButton;
    
    String currentUsername;
    String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        mContainerView = findViewById(R.id.edit_profile_container);
        mProgressView = findViewById(R.id.edit_profile_progress);

        userManager = new UserManager(this);

        user = getIntent().getParcelableExtra("user");

        mImagePicker = (ImageView) findViewById(R.id.edit_user_image);
        mUsername = (EditText) findViewById(R.id.edit_username);
        mEmail = (EditText) findViewById(R.id.edit_email);
        mOldPass = (EditText) findViewById(R.id.edit_old_pass);
        mNewPass = (EditText) findViewById(R.id.edit_new_pass);
        mConfirmPass = (EditText) findViewById(R.id.edit_confirm);
        mUpdateButton = (Button) findViewById(R.id.edit_profile_button_update);
        mCancelButton = (Button) findViewById(R.id.edit_profile_button_cancel);

        String profileImagePath = userManager.getProfileImagePath();
        if(profileImagePath != null) {
            mImagePicker.setImageBitmap(BitmapFactory.decodeFile(profileImagePath));
        } else {
            mImagePicker.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher, null));
        }

        currentUsername = user.getUsername();
        currentEmail = user.getEmail();
        
        mUsername.setText(currentUsername);
        mEmail.setText(currentEmail);

        mImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select picture"), SELECT_PICTURE);
            }
        });

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                if(pendingImagePath != null) {
                    userManager.uploadImage(new File(pendingImagePath));
                } else {
                    postUpdates();
                }
            }
        });
    }
    
    private void postUpdates() {
        String username = mUsername.getText().toString();
        String email = mEmail.getText().toString();

        // TODO: passwords

        userManager.postUserProfileUpdate(username, email, extFilename);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            pendingImagePath = RealPathUtil.getRealPathFromURI_API19(this, selectedImageUri);

            Log.i(TAG, "Image path: " + pendingImagePath);

            mImagePicker.setImageBitmap(BitmapFactory.decodeFile(pendingImagePath));
        }
    }

    @Override
    public void doUponCompletion(Bundle response) {
        Log.i(TAG, "doUpoonCompletion");
        Log.i(TAG, response.toString());

        boolean success = response.getBoolean("success");
        String message = response.getString("message");

        if(pendingImagePath != null) {
            // image upload not completed
            if(message.equals("Upload successful")) {
                // Upload successful
                extFilename = response.getString("extFilename");
                imagePath = pendingImagePath;
                pendingImagePath = null;

                // TODO: seperate folder for seperate user ids?
                String localPath = getFilesDir() + "/img/profile/" + extFilename;

                try {
                    FileUtil.copyFile(imagePath, localPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                userManager.setProfileImagePath(localPath);

                postUpdates();
            } else {
                // Upload unsuccessful
                // TODO: handle this
            }
        } else {
            // Image upload completed
            Log.i(TAG, "Shit done");
            Toast.makeText(this, "Update successful", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}

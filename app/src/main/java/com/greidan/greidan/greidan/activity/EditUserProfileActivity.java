package com.greidan.greidan.greidan.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.greidan.greidan.greidan.R;
import com.greidan.greidan.greidan.RealPathUtil;
import com.greidan.greidan.greidan.manager.UserManager;
import com.greidan.greidan.greidan.model.User;

import java.io.File;

public class EditUserProfileActivity extends ProgressActivity {

    private static final String TAG = "EditProfileActivity";
    private static final int SELECT_PICTURE = 1;

    UserManager userManager;

    User user;

    private String selectedImagePath;
    File imageFile;

    ImageView mImagePicker;
    EditText mUsername;
    EditText mEmail;
    EditText mOldPass;
    EditText mNewPass;
    EditText mConfirmPass;
    Button mUpdateButton;
    Button mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

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

        mUsername.setText(user.getUsername());
        mEmail.setText(user.getEmail());

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
                if(imageFile != null) {
                    userManager.uploadImage(imageFile);
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            selectedImagePath = RealPathUtil.getRealPathFromURI_API19(this, selectedImageUri);

            Log.i(TAG, "Image path: " + selectedImagePath);
            imageFile = new File(selectedImagePath);

            mImagePicker.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
        }
    }

    @Override
    public void doUponCompletion(Bundle response) {
        Log.i(TAG, "doUpoonCompletion");
        Log.i(TAG, response.toString());
    }
}

package com.greidan.greidan.greidan.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.greidan.greidan.greidan.R;
import com.greidan.greidan.greidan.manager.MessageManager;
import com.greidan.greidan.greidan.manager.UserManager;
import com.greidan.greidan.greidan.model.Message;
import com.greidan.greidan.greidan.model.User;

import java.util.Date;

public class NewMessageActivity extends ProgressActivity {

    MessageManager mMessageManager;
    UserManager mUserManager;

    EditText mSubject;
    EditText mContent;
    Button mButtonPost;

    Message newMessage;
    String recipientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        mMessageManager = new MessageManager(this);
        mUserManager = new UserManager(this);

        recipientName = getIntent().getStringExtra("recipientName");

        mSubject = (EditText) findViewById(R.id.new_message_subject);
        mContent = (EditText) findViewById(R.id.new_message_content);

        mButtonPost = (Button) findViewById(R.id.new_message_button_post);
        mButtonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = mSubject.getText().toString();
                String content = mContent.getText().toString();

                attemptPostMessage(subject, content);
            }
        });

        mContainerView = findViewById(R.id.new_message_container);
        mProgressView = findViewById(R.id.new_message_progress);

    }

    private void attemptPostMessage(String subject, String content) {
        showProgress(true);
        // TODO: Feed the message correct information about users
        newMessage = new Message(null, subject, content, recipientName, mUserManager.getLoggedInUsername(), new Date());
        mMessageManager.postMessageToServer(newMessage);
    }

    @Override
    public void doUponCompletion(Bundle data) {
        boolean success = data.getBoolean("success");
        String message = data.getString("message");
        String id = data.getString("id");

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        if(data.getBoolean("error")) {
            showProgress(false);
        }

        if(success) {
            newMessage.setId(id);
            finish();
        }
    }
}

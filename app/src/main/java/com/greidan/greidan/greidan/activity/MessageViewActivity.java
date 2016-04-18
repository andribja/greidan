package com.greidan.greidan.greidan.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.greidan.greidan.greidan.R;
import com.greidan.greidan.greidan.manager.MessageManager;
import com.greidan.greidan.greidan.model.Message;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MessageViewActivity extends ActionBarActivity {

    MessageManager messageManager;

    TextView mSubject;
    TextView mAuthor;
    TextView mTimePosted;
    TextView mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_view);

        messageManager = new MessageManager(this);

        Bundle bundle = getIntent().getExtras();
        final Message message = (Message) bundle.getParcelable("message");

        mSubject = (TextView) findViewById(R.id.message_view_subject);
        mAuthor = (TextView) findViewById(R.id.message_view_author);
        mAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageViewActivity.this, UserProfileActivity.class);
                intent.putExtra("username", message.getAuthorName());
                startActivity(intent);
            }
        });
        mTimePosted = (TextView) findViewById(R.id.message_view_time_posted);
        mContent = (TextView) findViewById(R.id.message_view_content);

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        mSubject.setText(message.getSubject());
        mAuthor.setText(String.format("Sendandi: %1$s", message.getAuthorName()));
        mTimePosted.setText(String.format("Skilaboð sent þann %1$s", df.format(message.getTimePosted())));
        mContent.setText(message.getContent());

    }
}

package com.greidan.greidan.greidan.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.greidan.greidan.greidan.R;
import com.greidan.greidan.greidan.manager.MessageManager;
import com.greidan.greidan.greidan.model.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MessageListActivity extends ProgressActivity {

    MessageManager mMessageManager;
    ListView mListView;

    HashMap<String, Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        mMessageManager = new MessageManager(this);

        mListView = (ListView) findViewById(R.id.message_list);

        // TODO: Ætti að taka inn logged in user id til að ná í skilaboð notandans
        //mMessageManager.fetchMessages("belja");

        ArrayList<Message> foo = new ArrayList<>();
        foo.add(new Message("1337", "Yo sup", "Hestur", "13", "31", "jobbi", new Date()));
        foo.add(new Message("137", "Subject", "Content", "13", "31", "dylan", new Date()));
        foo.add(new Message("1337", "what's dylan grillin?", "Hestur", "13", "31", "grillin", new Date()));
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("messages", foo);
        doUponCompletion(bundle);

    }

    public void doUponCompletion(Bundle data) {
        List<Message> messages = data.getParcelableArrayList("messages");
        populateMessageList(messages);


        if(data.getBoolean("error")) {
            Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
        }

        showProgress(false);
    }

    private void populateMessageList(List<Message> messages) {
        this.messages = new HashMap<>();
        for(Message message: messages) {
            this.messages.put(message.getId(), message);
        }

        mListView.setAdapter(new MessageListAdapter(this, messages));
    }

    private class MessageListAdapter extends ArrayAdapter<Message> {

        public MessageListAdapter(Context context, List<Message> messages) {
            super(context, 0, messages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Message message = getItem(position);

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_message, parent, false);
            }

            TextView subject = (TextView) convertView.findViewById(R.id.message_item_subject);
            subject.setText(message.getSubject());
            TextView author = (TextView) convertView.findViewById(R.id.message_item_author);
            author.setText(message.getAuthorName());
            convertView.setTag(message.getId());

            return convertView;
        }
    }
}

package com.thegads.uliza.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.thegads.uliza.R;
import com.thegads.uliza.model.NewMessage;

public class MessageActivity extends AppCompatActivity {

    private static final String TAG = MessageActivity.class.getSimpleName();
    public static final String MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView body_text = (TextView) findViewById(R.id.message_body);
        TextView time_text = (TextView) findViewById(R.id.message_time);

        NewMessage message = (NewMessage) getIntent().getExtras().getSerializable(MESSAGE);

        if (message != null) {
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(formatTopic(message.getChatId()));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            body_text.setText(message.getBody());
            time_text.setText(message.getTime());

           /* if (message.getId() != 0) {
                NewMessage.executeQuery("UPDATE NEW_MESSAGE SET read = ? WHERE ID = ?", "0", String.valueOf(message.getId()));
            }*/
        }

    }

    private static String formatTopic(String chatId) {
        String[] parts = chatId.split("/");
        return parts[2].replaceAll("_", " ");
    }
}

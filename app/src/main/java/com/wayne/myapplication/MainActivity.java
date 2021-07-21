package com.wayne.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author sunbowen
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView message = findViewById(R.id.messageTextView);
        Button downloadButton = findViewById(R.id.downloadButton);
        message.setSelected(true);
        downloadButton.setOnClickListener(v -> {
            Intent downloadActivityIntent = new Intent(this, DownloadActivity.class);
            startActivity(downloadActivityIntent);
        });
    }
}
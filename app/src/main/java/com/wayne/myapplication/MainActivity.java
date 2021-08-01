package com.wayne.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        message.setSelected(true);
        Button downloadButton = findViewById(R.id.downloadButton);
        EditText etDownloadUrl = findViewById(R.id.et_downloadUrl);
        EditText etThreadNum = findViewById(R.id.et_threadNum);
        downloadButton.setOnClickListener(v -> {
            String url = "https://downloads.gradle-dn.com/distributions/gradle-7.0.2-bin.zip";
            DataManager.setThreadNum(Integer.parseInt(etThreadNum.getText().toString()));
            DataManager.setURL(url);
            DataManager.setSavePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
            DataManager.setFileName(url.substring(url.lastIndexOf("/")));
            Intent downloadActivityIntent = new Intent(this, DownloadActivity.class);
            startActivity(downloadActivityIntent);
        });
    }
}
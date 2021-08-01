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
            String url2 = "https://vd4.bdstatic.com/mda-kht238a6vyvvt5zf/sc/mda-kht238a6vyvvt5zf.mp4?v_from_s=hkapp-haokan-hnb&auth_key=1627825887-0-0-0b004db0845eebaaaef05cefb8187d0d&bcevod_channel=searchbox_feed&pd=1&pt=3&abtest=3000156_1";
            DataManager.setThreadNum(Integer.parseInt(etThreadNum.getText().toString()));
            DataManager.setURL(url);
            DataManager.setSavePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
            DataManager.setFileName(url.substring(url.lastIndexOf("/")));
            Intent downloadActivityIntent = new Intent(this, DownloadActivity.class);
            startActivity(downloadActivityIntent);
        });
    }
}
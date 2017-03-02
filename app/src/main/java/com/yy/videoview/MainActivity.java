package com.yy.videoview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button bt_local;
    private Button bt_internet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    private void initView() {
        bt_local = (Button) findViewById(R.id.id_bt_local);
        bt_internet = (Button) findViewById(R.id.id_bt_internet);
    }
    private void initEvent() {
        bt_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放本地视频
                Intent intent = new Intent(MainActivity.this,VideoActivity.class);
                intent.putExtra(Utils.video_type,"local");
                startActivity(intent);
            }
        });
        bt_internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放网络视频
                Intent intent = new Intent(MainActivity.this,VideoActivity.class);
                intent.putExtra(Utils.video_type,"interent");
                startActivity(intent);
            }
        });
    }
}

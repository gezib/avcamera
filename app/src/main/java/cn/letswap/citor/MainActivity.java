package cn.letswap.citor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import cn.letswap.citor.extra.CitorButton;

public class MainActivity extends AppCompatActivity {

    private CitorButton citorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        citorButton = findViewById(R.id.button);
        citorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (citorButton.isStateOn()) {
                        citorButton.setText("已开启");
                    } else {
                        citorButton.setText("已关闭");
                    }
                    Settings.switchAppStatus(citorButton.isStateOn());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0  && grantResults[0]  == PackageManager.PERMISSION_GRANTED) {
                    Log.e("lets start", "request permission succeed");
                    // 初始化要写在这里 不然执行顺序有问题 待调查
                    if (!Settings.exists()) {
                        try {
                            Settings.init();
                            citorButton.setStateOn(Settings.isAppOn());
                            citorButton.callOnClick();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }else {
                    Log.e("lets start", "request permission failed");
                }
                break;
        }
    }
}
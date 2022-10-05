package cn.letswap.citor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

        if (!Settings.exists()) {
            try {
                Settings.init();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

//      MODE_WORLD_READABLE no longer supported
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        citorButton = findViewById(R.id.button);
        //        IFR: shortcut for Is First Run
        boolean isFirstRun = sp.getBoolean("IFR",true);
        if (isFirstRun) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("IFR",false);
            editor.putBoolean("ON",true);
            editor.commit();
        }

        citorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                if (citorButton.isStateOn()) {
                    citorButton.setText("已开启");
                    editor.putBoolean("ON",true);
                }else {
                    citorButton.setText("已关闭");
                    editor.putBoolean("ON",false);
                }
                editor.commit();
            }
        });



        boolean appOn = sp.getBoolean("ON",false);
        citorButton.setStateOn(appOn);
        citorButton.callOnClick();
    }

}
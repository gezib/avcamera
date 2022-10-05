package cn.letswap.citor;

import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Settings {

    private static final String settingsFilePath = "/data/data/cn.letswap.citor/settings/settings.json";
    private static final Data initSettings = new Data();

    private static File settingsFile = null;

    private static File getSettingsFile() {
        if (settingsFile == null) {
            settingsFile = new File(settingsFilePath);
        }
        return settingsFile;
    }

    public static boolean exists() {
        return getSettingsFile().exists();
    }

    public static void init() throws IOException {
        if (!getSettingsFile().exists()) {
            boolean created = getSettingsFile().createNewFile();
            if (!created) {
                Log.e("lets start", "file create failed");
                return;
            }
            FileOutputStream fos = new FileOutputStream(getSettingsFile());
            fos.write(new Gson().toJson(initSettings).getBytes(StandardCharsets.UTF_8));
            fos.close();
        }
    }

    public static boolean isAppOn() throws IOException {
        FileInputStream fis = new FileInputStream(getSettingsFile());
        IOUtils.toString(fis,StandardCharsets.UTF_8);
        Gson g = new Gson();
        Data d = g.fromJson(IOUtils.toString(fis,StandardCharsets.UTF_8),Data.class);
        fis.close();
        return d.isAppOn();
    }

    public static void switchAppStatus(boolean appOn) throws IOException {
        if (isAppOn() == appOn) {
            return;
        }
        Data d = new Data();
        d.setAppOn(appOn);
        FileOutputStream fos = new FileOutputStream(getSettingsFile());
        fos.write(new Gson().toJson(d).getBytes(StandardCharsets.UTF_8));
        fos.close();
    }
}

class Data {
    private boolean appOn = true;

    Data(){}

    public boolean isAppOn() {
        return appOn;
    }

    public void setAppOn(boolean appOn) {
        this.appOn = appOn;
    }

}
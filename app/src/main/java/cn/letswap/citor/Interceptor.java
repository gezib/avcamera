package cn.letswap.citor;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaMuxer;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.EOFException;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.prefs.Preferences;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Interceptor implements IXposedHookLoadPackage {

    public static Camera mCamera = null;
    public static SurfaceTexture mSurfaceTexture = null;
    public static SurfaceTexture tmpSurfaceTexture = null;

    public static Surface mSurface = null;
    public static MediaPlayer mMediaPlayer = null;

//    Camera 2
    public static CameraDevice.StateCallback stateCallback = null;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//        Settings.reload();
        // 好像真读不到这个文件 只能判断是否存在
//        Log.e("lets start", Settings.exists()+"" +Settings.isAppOn());
//        if (Settings.exists() && Settings.isAppOn()) {
//            Log.e("lets start",lpparam.packageName);
//        } else  {
//            Log.e("lets start",lpparam.packageName + " skipped");
//            return;
//        }

        Log.e("lets start", lpparam.packageName);

        /*
        * Camera 1 Live TikTok
        * 1. setPreviewTexture
        * 2. startPreview
        * 3. stopPreview
        * 4. release
        * */
        XposedHelpers.findAndHookMethod("android.hardware.Camera", lpparam.classLoader, "setPreviewTexture", SurfaceTexture.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Log.e("lets start","before setPreviewTexture");

                if (param.args[0]  == null) {
                    Log.e("lets start","param.args[0]  == null");
                    return;
                }

//                不知道这个tmp 什么用 这样写就没问题。
                if (mCamera != null && mCamera.equals(param.thisObject)) {
                    param.args[0] = tmpSurfaceTexture;
                    return;
                }

                mCamera = (Camera)param.thisObject;
                mSurfaceTexture = (SurfaceTexture) param.args[0];

                if (tmpSurfaceTexture != null) {
                    tmpSurfaceTexture.release();
                }

                tmpSurfaceTexture = new SurfaceTexture(10);
                param.args[0] = tmpSurfaceTexture;
            }
        });

        XposedHelpers.findAndHookMethod("android.hardware.Camera", lpparam.classLoader, "setDisplayOrientation", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Log.e("lets start","before setDisplayOrientation");
                if (param.args[0]  == null) {
                    Log.e("lets start","setDisplayOrientation: param.args[0]  == null");
                    return;
                }
//                只是摄像头的转向好像
                Log.e("lets start","setDisplayOrientation: param is: " + param.args[0]);
//                param.args[0] = 90;
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
//                param.args[0] = 90;
            }
        });

        XposedHelpers.findAndHookMethod("android.hardware.Camera", lpparam.classLoader, "setPreviewCallbackWithBuffer", Camera.PreviewCallback.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Log.e("lets start","before setPreviewCallbackWithBuffer");
            }
        });

        XposedHelpers.findAndHookMethod("android.hardware.Camera", lpparam.classLoader, "addCallbackBuffer", byte[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Log.e("lets start","before addCallbackBuffer");
            }
        });

        XposedHelpers.findAndHookMethod("android.hardware.Camera", lpparam.classLoader, "setPreviewCallback", Camera.PreviewCallback.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Log.e("lets start","before setPreviewCallback");
            }
        });

        XposedHelpers.findAndHookMethod("android.hardware.Camera", lpparam.classLoader, "setOneShotPreviewCallback", Camera.PreviewCallback.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Log.e("lets start","before setOneShotPreviewCallback");
            }
        });

        XposedHelpers.findAndHookMethod("android.hardware.Camera", lpparam.classLoader, "takePicture", Camera.ShutterCallback.class, Camera.PictureCallback.class, Camera.PictureCallback.class, Camera.PictureCallback.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                Log.e("lets start","after takePicture");
            }
        });

        XposedHelpers.findAndHookMethod("android.media.MediaRecorder", lpparam.classLoader, "setCamera", Camera.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Log.e("lets start","before MediaRecorder.setCamera");
            }
        });

        XposedHelpers.findAndHookMethod("android.hardware.Camera", lpparam.classLoader, "startPreview", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.e("lets start","before startPreview");

                if (mSurfaceTexture == null) {
                    Log.e("lets start", "mSurfaceTexture == null");
                    return;
                }

                if (mSurface != null) {
                    Log.e("lets start", "mSurface != null");
                    mSurface.release();
                }

                if (mMediaPlayer !=null) {
                    Log.e("lets start", "mMediaPlayer != null");
                    mMediaPlayer.release();
                }

                mSurface = new Surface(mSurfaceTexture);
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setSurface(mSurface);
//                mMediaPlayer.setVolume(0,0);

                mMediaPlayer.setLooping(true);
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mMediaPlayer.start();
                    }
                });

                try {
//                    mMediaPlayer.setDataSource("http://192.168.100.51:8080/hls/virtual.m3u8");
                    mMediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera1/virtual.mp4");

                    mMediaPlayer.prepare();
                }catch (Exception e) {
                    Log.e("lets start", e.toString());
                    throw e;
                }
            }
        });

        XposedHelpers.findAndHookMethod("android.hardware.Camera", lpparam.classLoader, "stopPreview", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.e("lets start","before stopPreview");
            }
        });

        XposedHelpers.findAndHookMethod("android.hardware.Camera", lpparam.classLoader, "release", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.e("lets start","before release");
                tmpSurfaceTexture.release();
                mSurfaceTexture.release();
                mSurface.release();
                mMediaPlayer.release();
            }
        });

        XposedHelpers.findAndHookMethod("android.hardware.Camera", lpparam.classLoader, "setPreviewDisplay", SurfaceHolder.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.e("lets start","before setPreviewDisplay");
            }
        });


        XposedHelpers.findAndHookMethod("android.media.MediaMuxer", lpparam.classLoader, "start", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.e("lets start","before MediaMuxer start");
            }
        });



        /*
        * Camera 2
        * 1. openCamera2
        * 2. openCamera
        * 3. addTarget
        * 4. build
        * */
        XposedHelpers.findAndHookMethod("android.hardware.camera2.CameraManager", lpparam.classLoader, "openCamera", String.class, CameraDevice.StateCallback.class, Handler.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.e("lets start2","openCamera");
                if (param.args[1] == null) {
                    Log.e("lets start2","openCamera: state callback is null");
                    return;
                }
                if (param.args[1].equals(stateCallback)) {
                    Log.e("lets start2","openCamera: state callback already initialized");
                    return;
                }
                stateCallback = (CameraDevice.StateCallback) param.args[1];


            }
        });

//        TODO
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            XposedHelpers.findAndHookMethod("android.hardware.camera2.CameraManager", lpparam.classLoader, "openCamera", String.class, CameraDevice.StateCallback.class, Handler.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Log.e("lets start2","openCamera2");
                }
            });
        }

        XposedHelpers.findAndHookMethod("android.media.MediaRecorder", lpparam.classLoader, "setCamera", Camera.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Log.e("lets start2","setCamera");
            }
        });

        XposedHelpers.findAndHookMethod("android.hardware.camera2.CaptureRequest.Builder", lpparam.classLoader, "addTarget", Surface.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Log.e("lets start2","addTarget");
            }
        });

        XposedHelpers.findAndHookMethod("android.hardware.camera2.CaptureRequest.Builder", lpparam.classLoader, "removeTarget", Surface.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Log.e("lets start2","removeTarget");
            }
        });

        XposedHelpers.findAndHookMethod("android.hardware.camera2.CaptureRequest.Builder", lpparam.classLoader, "build", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.e("lets start2","build");
            }
        });

        XposedHelpers.findAndHookMethod("android.media.ImageReader", lpparam.classLoader, "newInstance", int.class, int.class, int.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Log.e("lets start2","newInstance");
            }
        });

        XposedHelpers.findAndHookMethod("android.hardware.camera2.CameraCaptureSession.CaptureCallback", lpparam.classLoader, "onCaptureFailed", CameraCaptureSession.class, CaptureRequest.class, CaptureFailure.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Log.e("lets start2","CaptureCallback");
            }
        });


//        XposedHelpers.findAndHookMethod("android.app.Instrumentation", lpparam.classLoader, "callApplicationOnCreate", Application.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                super.beforeHookedMethod(param);
//                if (param.args[0] instanceof Application) {
//                    mContext = (Application)param.args[0].getApplicationContext();
//                }
//            }
//        });

    }
}

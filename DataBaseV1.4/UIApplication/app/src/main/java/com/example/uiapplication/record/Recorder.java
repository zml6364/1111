package com.example.uiapplication.record;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.view.Display;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.uiapplication.util.CommonUtils;
import com.example.uiapplication.voiceQueryActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class Recorder extends Context {
    /**
     * 每次buffer语音对应的时长为100ms
     */
    public static final int TIMER_INTERVAL = 100;
    private static final String TAG = "Recorder";
    private IdealRecorder.RecordConfig recordConfig;
    private AudioRecord mAudioRecorder = null;
    private RecorderCallback mCallback;
    private int bufferSize;
    private boolean isRecord = false;
    private Thread mThread = null;
    private short[] wave;

    private int PERMISSION_RECORD_AUDIO;
    private int REQUEST_CODE = 101;

    private Runnable RecordRun = new Runnable() {

        public void run() {
            if ((mAudioRecorder != null) && (mAudioRecorder.getState() == 1)) {

                try {
                    mAudioRecorder.stop();
                    mAudioRecorder.startRecording();
                } catch (Exception e) {
                    e.printStackTrace();
                    recordFailed(IdealConst.RecorderErrorCode.RECORDER_EXCEPTION_OCCUR);
                    mAudioRecorder = null;
                }
            }
            if ((mAudioRecorder != null) &&
                    (mAudioRecorder.getState() == 1) && (mAudioRecorder.getRecordingState() == 1)) {
                Log.e(TAG, "no recorder permission or recorder is not available right now");
                recordFailed(IdealConst.RecorderErrorCode.RECORDER_PERMISSION_ERROR);
                mAudioRecorder = null;
            }
            for (int i = 0; i < 2; i++) {
                if (mAudioRecorder == null) {
                    isRecord = false;
                    break;
                }
                mAudioRecorder.read(wave, 0, wave.length);
            }
            while (isRecord) {
                int nLen = 0;
                try {
                    nLen = mAudioRecorder.read(wave, 0, wave.length);
                } catch (Exception e) {
                    isRecord = false;
                    recordFailed(IdealConst.RecorderErrorCode.RECORDER_EXCEPTION_OCCUR);
                }
                if (nLen == wave.length) {
                    mCallback.onRecorded(wave);
                } else {
                    recordFailed(IdealConst.RecorderErrorCode.RECORDER_READ_ERROR);
                    isRecord = false;
                }
            }
            Log.i(TAG, "out of the reading while loop,i'm going to stop");
            unInitializeRecord();
            doRecordStop();
        }
    };


    public Recorder(IdealRecorder.RecordConfig config, RecorderCallback callback) {
        this.mCallback = callback;
        this.recordConfig = config;
    }

    public void setRecordConfig(IdealRecorder.RecordConfig config) {
        this.recordConfig = config;
    }


    public boolean start() {
        isRecord = true;
        synchronized (this) {
            if (doRecordReady()) {
                Log.d(TAG, "doRecordReady");
                if (initializeRecord()) {
                    Log.d(TAG, "initializeRecord");
                    if (doRecordStart()) {
                        Log.d(TAG, "doRecordStart");

                        mThread = new Thread(RecordRun);
                        mThread.start();
                        return true;
                    }
                }
            }
        }
        isRecord = false;
        return false;
    }


    public void stop() {
        synchronized (this) {
            mThread = null;
            isRecord = false;
        }
    }

    public void immediateStop() {
        isRecord = false;
        if (mThread != null) {
            try {
                mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mThread = null;
    }

    public boolean isStarted() {
        return isRecord;
    }

    private boolean initializeRecord() {
        synchronized (this) {
            try {
                if (mCallback == null) {
                    Log.e(TAG, "Error VoiceRecorderCallback is  null");
                    return false;
                }
                if (recordConfig == null) {
                    Log.e(TAG, "Error recordConfig is null");
                    return false;
                }
                short nChannels;
                int sampleRate;
                short bSamples;
                int audioSource;
                int audioFormat;
                int channelConfig;
                if (recordConfig.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT) {
                    bSamples = 16;
                } else {
                    bSamples = 8;
                }

                if ((channelConfig = recordConfig.getChannelConfig()) == AudioFormat.CHANNEL_IN_MONO) {
                    nChannels = 1;
                } else {
                    nChannels = 2;
                }
                audioSource = recordConfig.getAudioSource();
                sampleRate = recordConfig.getSampleRate();
                audioFormat = recordConfig.getAudioFormat();
                int framePeriod = sampleRate * TIMER_INTERVAL / 1000;
                bufferSize = framePeriod * 2 * bSamples * nChannels / 8;

                wave = new short[framePeriod * bSamples / 8 * nChannels / 2];
                Log.d(TAG, "buffersize = " + bufferSize);
                int nMinSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                if (bufferSize < nMinSize) {
                    bufferSize = nMinSize;

                    Log.d(TAG, "Increasing buffer size to " + Integer.toString(bufferSize));
                }
                if (mAudioRecorder != null) {
                    unInitializeRecord();
                }


                //checkStoragePermission();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                }

                mAudioRecorder = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, bufferSize);
                if (mAudioRecorder.getState() != 1) {
                    mAudioRecorder = null;
                    recordFailed(IdealConst.RecorderErrorCode.RECORDER_PERMISSION_ERROR);
                    Log.e(TAG, "AudioRecord initialization failed,because of no RECORD permission or unavailable AudioRecord ");
                    throw new Exception("AudioRecord initialization failed");
                }
                Log.i(TAG, "initialize  Record");
                return true;
            } catch (Throwable e) {
                if (e.getMessage() != null) {
                    Log.e(TAG, getClass().getName() + e.getMessage());
                } else {
                    Log.e(TAG, getClass().getName() + "Unknown error occured while initializing recording");
                }
                return false;
            }
        }
    }

    private void unInitializeRecord() {
        Log.i(TAG, "unInitializeRecord");
        synchronized (this) {
            if (mAudioRecorder != null) {
                try {
                    mAudioRecorder.stop();
                    mAudioRecorder.release();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "mAudioRecorder release error!");
                }
                mAudioRecorder = null;
            }
        }
    }

    private boolean doRecordStart() {
        if (mCallback != null) {
            return mCallback.onRecorderStart();
        }
        return true;
    }

    private boolean doRecordReady() {
/*        if (mCallback != null) {
            return mCallback.onRecorderReady();
        }*/
        return true;
    }

    private void doRecordStop() {
        if (mCallback != null) {
            mCallback.onRecorderStop();
        }
    }

    private void recordFailed(int errorCode) {
        if (mCallback != null) {
            mCallback.onRecordedFail(errorCode);
        }
    }

    @Override
    public AssetManager getAssets() {
        return null;
    }

    @Override
    public Resources getResources() {
        return null;
    }

    @Override
    public PackageManager getPackageManager() {
        return null;
    }

    @Override
    public ContentResolver getContentResolver() {
        return null;
    }

    @Override
    public Looper getMainLooper() {
        return null;
    }

    @Override
    public Context getApplicationContext() {
        return null;
    }

    @Override
    public void setTheme(int resid) {

    }

    @Override
    public Resources.Theme getTheme() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public String getPackageName() {
        return null;
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return null;
    }

    @Override
    public String getPackageResourcePath() {
        return null;
    }

    @Override
    public String getPackageCodePath() {
        return null;
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return null;
    }

    @Override
    public boolean moveSharedPreferencesFrom(Context sourceContext, String name) {
        return false;
    }

    @Override
    public boolean deleteSharedPreferences(String name) {
        return false;
    }

    @Override
    public FileInputStream openFileInput(String name) throws FileNotFoundException {
        return null;
    }

    @Override
    public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
        return null;
    }

    @Override
    public boolean deleteFile(String name) {
        return false;
    }

    @Override
    public File getFileStreamPath(String name) {
        return null;
    }

    @Override
    public File getDataDir() {
        return null;
    }

    @Override
    public File getFilesDir() {
        return null;
    }

    @Override
    public File getNoBackupFilesDir() {
        return null;
    }

    @Nullable
    @Override
    public File getExternalFilesDir(@Nullable String type) {
        return null;
    }

    @Override
    public File[] getExternalFilesDirs(String type) {
        return new File[0];
    }

    @Override
    public File getObbDir() {
        return null;
    }

    @Override
    public File[] getObbDirs() {
        return new File[0];
    }

    @Override
    public File getCacheDir() {
        return null;
    }

    @Override
    public File getCodeCacheDir() {
        return null;
    }

    @Nullable
    @Override
    public File getExternalCacheDir() {
        return null;
    }

    @Override
    public File[] getExternalCacheDirs() {
        return new File[0];
    }

    @Override
    public File[] getExternalMediaDirs() {
        return new File[0];
    }

    @Override
    public String[] fileList() {
        return new String[0];
    }

    @Override
    public File getDir(String name, int mode) {
        return null;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return null;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, @Nullable DatabaseErrorHandler errorHandler) {
        return null;
    }

    @Override
    public boolean moveDatabaseFrom(Context sourceContext, String name) {
        return false;
    }

    @Override
    public boolean deleteDatabase(String name) {
        return false;
    }

    @Override
    public File getDatabasePath(String name) {
        return null;
    }

    @Override
    public String[] databaseList() {
        return new String[0];
    }

    @Override
    public Drawable getWallpaper() {
        return null;
    }

    @Override
    public Drawable peekWallpaper() {
        return null;
    }

    @Override
    public int getWallpaperDesiredMinimumWidth() {
        return 0;
    }

    @Override
    public int getWallpaperDesiredMinimumHeight() {
        return 0;
    }

    @Override
    public void setWallpaper(Bitmap bitmap) throws IOException {

    }

    @Override
    public void setWallpaper(InputStream data) throws IOException {

    }

    @Override
    public void clearWallpaper() throws IOException {

    }

    @Override
    public void startActivity(Intent intent) {

    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {

    }

    @Override
    public void startActivities(Intent[] intents) {

    }

    @Override
    public void startActivities(Intent[] intents, Bundle options) {

    }

    @Override
    public void startIntentSender(IntentSender intent, @Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {

    }

    @Override
    public void startIntentSender(IntentSender intent, @Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, @Nullable Bundle options) throws IntentSender.SendIntentException {

    }

    @Override
    public void sendBroadcast(Intent intent) {

    }

    @Override
    public void sendBroadcast(Intent intent, @Nullable String receiverPermission) {

    }

    @Override
    public void sendOrderedBroadcast(Intent intent, @Nullable String receiverPermission) {

    }

    @Override
    public void sendOrderedBroadcast(@NonNull Intent intent, @Nullable String receiverPermission, @Nullable BroadcastReceiver resultReceiver, @Nullable Handler scheduler, int initialCode, @Nullable String initialData, @Nullable Bundle initialExtras) {

    }

    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user) {

    }

    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user, @Nullable String receiverPermission) {

    }

    @Override
    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user, @Nullable String receiverPermission, BroadcastReceiver resultReceiver, @Nullable Handler scheduler, int initialCode, @Nullable String initialData, @Nullable Bundle initialExtras) {

    }

    @Override
    public void sendStickyBroadcast(Intent intent) {

    }

    @Override
    public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, @Nullable Handler scheduler, int initialCode, @Nullable String initialData, @Nullable Bundle initialExtras) {

    }

    @Override
    public void removeStickyBroadcast(Intent intent) {

    }

    @Override
    public void sendStickyBroadcastAsUser(Intent intent, UserHandle user) {

    }

    @Override
    public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle user, BroadcastReceiver resultReceiver, @Nullable Handler scheduler, int initialCode, @Nullable String initialData, @Nullable Bundle initialExtras) {

    }

    @Override
    public void removeStickyBroadcastAsUser(Intent intent, UserHandle user) {

    }

    @Nullable
    @Override
    public Intent registerReceiver(@Nullable BroadcastReceiver receiver, IntentFilter filter) {
        return null;
    }

    @Nullable
    @Override
    public Intent registerReceiver(@Nullable BroadcastReceiver receiver, IntentFilter filter, int flags) {
        return null;
    }

    @Nullable
    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, @Nullable String broadcastPermission, @Nullable Handler scheduler) {
        return null;
    }

    @Nullable
    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, @Nullable String broadcastPermission, @Nullable Handler scheduler, int flags) {
        return null;
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {

    }

    @Nullable
    @Override
    public ComponentName startService(Intent service) {
        return null;
    }

    @Nullable
    @Override
    public ComponentName startForegroundService(Intent service) {
        return null;
    }

    @Override
    public boolean stopService(Intent service) {
        return false;
    }

    @Override
    public boolean bindService(Intent service, @NonNull ServiceConnection conn, int flags) {
        return false;
    }

    @Override
    public void unbindService(@NonNull ServiceConnection conn) {

    }

    @Override
    public boolean startInstrumentation(@NonNull ComponentName className, @Nullable String profileFile, @Nullable Bundle arguments) {
        return false;
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        return null;
    }

    @Nullable
    @Override
    public String getSystemServiceName(@NonNull Class<?> serviceClass) {
        return null;
    }

    @Override
    public int checkPermission(@NonNull String permission, int pid, int uid) {
        return PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public int checkCallingPermission(@NonNull String permission) {
        return PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public int checkCallingOrSelfPermission(@NonNull String permission) {
        return PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public int checkSelfPermission(@NonNull String permission) {
        return PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void enforcePermission(@NonNull String permission, int pid, int uid, @Nullable String message) {

    }

    @Override
    public void enforceCallingPermission(@NonNull String permission, @Nullable String message) {

    }

    @Override
    public void enforceCallingOrSelfPermission(@NonNull String permission, @Nullable String message) {

    }

    @Override
    public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {

    }

    @Override
    public void revokeUriPermission(Uri uri, int modeFlags) {

    }

    @Override
    public void revokeUriPermission(String toPackage, Uri uri, int modeFlags) {

    }

    @Override
    public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
        return PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public int checkCallingUriPermission(Uri uri, int modeFlags) {
        return PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
        return PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public int checkUriPermission(@Nullable Uri uri, @Nullable String readPermission, @Nullable String writePermission, int pid, int uid, int modeFlags) {
        return PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message) {

    }

    @Override
    public void enforceCallingUriPermission(Uri uri, int modeFlags, String message) {

    }

    @Override
    public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message) {

    }

    @Override
    public void enforceUriPermission(@Nullable Uri uri, @Nullable String readPermission, @Nullable String writePermission, int pid, int uid, int modeFlags, @Nullable String message) {

    }

    @Override
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        return null;
    }

    @Override
    public Context createContextForSplit(String splitName) throws PackageManager.NameNotFoundException {
        return null;
    }

    @Override
    public Context createConfigurationContext(@NonNull Configuration overrideConfiguration) {
        return null;
    }

    @Override
    public Context createDisplayContext(@NonNull Display display) {
        return null;
    }

    @Override
    public Context createDeviceProtectedStorageContext() {
        return null;
    }

    @Override
    public boolean isDeviceProtectedStorage() {
        return false;
    }

    public void checkStoragePermission(Context context) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 如果权限未被授予，向用户请求权限
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            // 权限已经被授予
            // 在这里执行您的逻辑
        }
    }

}

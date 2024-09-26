package com.smartcity.cgs;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.smartcity.SOS119;

import org.linphone.core.BuildConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/** 這邊是 119 緊急呼叫 的 背景服務 */
public class MyService extends Service {

    private final String TAG = "MyService";

    /** 這邊是 gpio 接口，用來外接 119 的按鈕 */
    private final String GPIO_119_PATH = "/sys/class/gpio/gpio149/value";

    private MediaPlayer mediaPlayer;    // mp3 player

    /**
     * 全局同步鎖 :
     * 當 gpio 119 按鈕被按下的時候、會啟動 SOS119 Activity，並且 myServiceLock = true
     * (如果不使用鎖、會啟動多個 acitvity)
     * 然後當 Activity onDestroy 的時候，會將這個鎖釋放 myServiceLock = false
     */
    public static volatile boolean myServiceLock = false;

    private final Handler handler = new Handler();

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.postDelayed(showTime, 1000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(showTime);

        super.onDestroy();

        // mediaPlayer.stop();     // stop playing
        // mediaPlayer.release();  // release mp3 player

    }

    private final Runnable showTime = new Runnable() {

        public void run() {

            /*
             * 當外接 gpio 數值為 0 的時候 --> 代表 : 緊急按鈕目前 [有] 被按下，我們要啟動 緊急撥話 的 Activity
             * 當外接 gpio 數值為 1 的時候 --> 代表 : 緊急按鈕目前 [沒有] 被按下，不做事
             */

            try {
                if (0 == Integer.parseInt(getValue(GPIO_119_PATH))) {

                    // gpio in - 0

                    if (myServiceLock) {
                        return;


                    } else {  // gpio in - 1

                        myServiceLock = true;

                        /************************* 啟動 119 的 Activity *******************************/
                        Log.d("qaz","119 啟動啟動啟動啟動啟動啟動啟動啟動啟動啟動啟動啟動啟動啟動啟動啟動啟動啟動啟動");
                        Intent intent = new Intent(MyService.this , SOS119.class);

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(intent);  // 顯示 119 報警通話

                        // 下面撥警報音
                        // mediaPlayer = MediaPlayer.create(MyService.this, R.raw.alarm119);  // create a mp3 player

                        // 播放 MP3 - 119
                        // mediaPlayer.start();

                    }
                } else if (1 == Integer.parseInt(getValue(GPIO_119_PATH))) {

                        // 未偵測到按下，不做事
                    Log.d("qaz","119 沒啟動");
                }
            } catch (Throwable throwable) {
                // do nothing
                Log.d("qaz", "exception : "  + throwable.getMessage().toString()) ;
            }

            /*
             * gpio 偵測線程，每 0.1 秒偵測一次 gpio 按鈕有沒有被按下
             * it is in order to avoid too many data blocks and cause
             */

            handler.postDelayed(showTime,100);
        }
    };

    private String getValue(String path) {

        String result = "";

        File file = new File(path);
        FileReader mFileReader = null;
        BufferedReader bufferedReader = null;

        try {

            mFileReader = new FileReader(file);
            bufferedReader = new BufferedReader(mFileReader);
            String resultString;

            while (null != (resultString = bufferedReader.readLine())) result = resultString;

        } catch (Throwable throwable) {

            if (BuildConfig.DEBUG) Log.e( TAG, "getValue Throwable " + throwable);

        } finally {

            try {
                if (null != bufferedReader) bufferedReader.close();
            } catch (Throwable throwable) {

                if (BuildConfig.DEBUG) Log.e( "qaz", "bufferedReader Throwable " + throwable);

            }

            try {

                if (null != mFileReader) mFileReader.close();

            } catch (Throwable throwable) {

                if (BuildConfig.DEBUG)
                    Log.e( "qaz", "mFileReader Throwable " + throwable);
            }
        }

        return result;
    }
}
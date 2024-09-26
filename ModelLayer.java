package com.smartcity.cgs;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import org.linphone.core.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ModelLayer {

    private static ModelLayer modelLayer;
    private ModelLayer() {}

    private final String TAG = "ModelLayer";


    ///////////////////////// 圖片的目錄 url  //////////////////////////////////////
    public final String CGS_IMAGE_RUL = "http://192.168.0.135/cgc/api/cgsImg/";
    public final String CGS_TRAVEL_URL = "http://192.168.0.135/cgc/api/cgsTravel/";
    public final String CGS_TRAVEL_IMAGE_URL = "http://192.168.0.135/cgc/api/travelImg/";

    private String token;

    public static ModelLayer getModelLayer() {

        if (null == modelLayer) modelLayer = new ModelLayer();
        return modelLayer;
    }

    /** 設定圖片的方法 */
    @SuppressLint("HandlerLeak")

    private final Handler modelLayerSetImageViewFromURLHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message message) {
            super.handleMessage(message);

            /** 抓到 圖片的 byte array */
            Bundle bundle = message.getData();
            byte[] byteArray = bundle.getByteArray("imageByteArray");

            /** 將 byte array 轉成 bitmap */
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            /** 將目前的 ImageView 設定 圖片的 bitmap */
            ImageView imageView = (ImageView) message.obj;
            imageView.setImageBitmap(bitmap);

        }
    };

    public void setToken(String resToken) { token = resToken; }
    public String getToken() { return token; }
    public void setImageViewFromURL(final ImageView imageViewImage, String urlString) {

        Log.d("qaz","進入setImageViewFromURL");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL(urlString);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);  // 5 secs connection timeout
                    connection.setReadTimeout(5000);     // 5 secs read connection timeout

                    connection.setRequestProperty("Authorization","Bearer " + getToken());   //
                    connection.setRequestMethod("GET");   // get the user data
                    connection.setRequestProperty("Connection", "keep-Alive");
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.setRequestProperty("Accept", "application/json ; ; charset=UTF-8");

                    connection.setDoOutput(false);
                    connection.setDoInput(true);
                    connection.setUseCaches(false);
                    connection.connect();

                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        InputStream inputStream = connection.getInputStream();

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int length;

                        while ((length = inputStream.read(buffer)) != -1) byteArrayOutputStream.write(buffer, 0, length);
                        inputStream.close();

                        byte[] byteArray = byteArrayOutputStream.toByteArray();

                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putByteArray("imageByteArray", byteArray); // 將圖片放到 bundle
                        message.obj = imageViewImage;  // 將欲顯示的圖片指定給 message的 object
                        message.setData(bundle);       // 將圖片設給 message

                        modelLayerSetImageViewFromURLHandler.handleMessage(message);

                        Log.d("qaz","modelLayerSetImageViewFromURLHandler");

                    }
                } catch (Throwable throwable) {

                    if (BuildConfig.DEBUG) Log.e( TAG, "setImageViewFromURL Throwable " + throwable);
                }
            }
        }).start();
    }
}

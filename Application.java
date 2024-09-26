package com.smartcity;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import android.content.SharedPreferences;
import android.util.Log;


import com.smartcity.SerialPortFinder ;

import android_serialport_api.SerialPort;

public class Application extends android.app.Application {

    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;

    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {

        if (mSerialPort == null) {

            /* Read serial port parameters */

            Log.d("qaz", "getSerialPort() 這是城市守衛者的 application ");

            // SharedPreferences sp = getSharedPreferences("android_serialport_api.sample_preferences", MODE_PRIVATE);
            SharedPreferences sp = getSharedPreferences("com.smartcity.cgs_preferences",MODE_PRIVATE);
            String path = sp.getString("DEVICE", "");
            int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

            /* Check parameters */

            if ( (path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

           /*
            * 開啟  serial port :
            * device name : ttyS4 (path)
            * baud rate : 115200  (buadrate)
            */

            mSerialPort = new SerialPort(new File(path), baudrate, 0);

        }
        return mSerialPort;
    }

    public void closeSerialPort() {

        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }


}

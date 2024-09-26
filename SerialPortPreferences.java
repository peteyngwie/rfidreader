package com.smartcity;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.smartcity.cgs.MainActivity;
import com.smartcity.cgs.R;

public class SerialPortPreferences  extends PreferenceActivity {


    private com.smartcity.Application mApplication;
    private SerialPortFinder mSerialPortFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setContentView(R.layout.activity_serial_port_preferences); 這不需要 contentview !
        // getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        Preference buttonPreference = findPreference("button_preference");

        mApplication = (com.smartcity.Application) getApplication();

        mSerialPortFinder = mApplication.mSerialPortFinder;

        addPreferencesFromResource(R.xml.serial_port_preferences); // 串列埠 傳輸率 設定

        // Devices  - 列表以供選擇

        final ListPreference devices = (ListPreference) findPreference("DEVICE");  // 取出全部的裝置
        String[] entries = mSerialPortFinder.getAllDevices();  // find all available devicdes (ttyS0-S4)
        String[] entryValues = mSerialPortFinder.getAllDevicesPath();

        // 列出所有的 devices ////////////////////// tty0 -4
        for (int i = 0; i < entries.length; i++)
            Log.d("qaz", "裝置" + "[" + i + "]" + ":" + entries[i]);

        devices.setEntries(entries);
        devices.setEntryValues(entryValues);

        // devices.setSummary(devices.getValue());
        Log.d("qaz", " >>>>>>>>> 裝置設定: " + devices.getSummary());  // not yet !

        devices.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            public boolean onPreferenceChange(Preference preference, Object newValue) {

                preference.setSummary((String) newValue);

                Log.d("qaz", "裝置完成設定值:" + preference.getSummary() );

                return true;
            }
        });

        // Baud rates
        final ListPreference baudrates = (ListPreference) findPreference("BAUDRATE");
        baudrates.setSummary(baudrates.getValue());
        baudrates.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                preference.setSummary((String) newValue);

                return true;
            }
        });

    }  // end of onCreate

    @Override
    protected void onDestroy() {
        // 我们自己的方法。
        super.onDestroy();
        Log.d("qaz", "離開 SerialPortPreference ");
        finish();
        // Intent intent = new Intent(SerialPortPreferences.this, MainActivity.class) ;
        // startActivity(intent);

    }
}
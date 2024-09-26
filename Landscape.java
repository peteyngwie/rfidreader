package com.smartcity.cgs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class Landscape extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landscape);

        // getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);  // 這行是要將 drawer設為右邊
    }
}
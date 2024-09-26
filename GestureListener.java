package com.smartcity;

import android.view.GestureDetector;
import android.view.MotionEvent;

// 自訂的手勢偵測器
public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    // 定義手勢偵測的閾值 (敏感度)
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();

        // 偵測打勾手勢 (例如右上至左下的一條斜線)
        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(diffY) > SWIPE_THRESHOLD) {
            if (diffX > 0 && diffY > 0) {
                // 打勾手勢偵測成功
                // showAlertDialog();

                return true;
            }
        }
        return false;
    }
}

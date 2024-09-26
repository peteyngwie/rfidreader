package com.smartcity.cgs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SideSpinner extends LinearLayout {
    private ImageView SideLeftBack ;   // Left back ( back to previous page )
    private ImageView SideHome     ;   // Home

    private ImageView SideCamera   ;   // Camera

    private TextView countdownTitle ;   // count down title

    private RelativeLayout.LayoutParams laParams;  // 設置全域變數 laParams

    private TextView WaitTxt ;         // wait for a while

    private Button cancel_dummy , confirm_dummy ;

    public int [] ImageList = {R.drawable.chiayiculturalpark03 ,
                               R.drawable.chiayiculturalpark06 ,
                               R.drawable.beixianghupark01    } ;


    // private CharSequence[] mSpinnerValues = null;
    // private int mSelectedIndex = -1;

    public SideSpinner(Context context) {
        super(context);
        initializeViews(context);
    }
    public SideSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeViews(context);

    }
    public SideSpinner(Context context,
                       AttributeSet attrs,
                       int defStyle) {

        super(context, attrs, defStyle);

        initializeViews(context);

    }
    /**
     * Inflates the views in the layout.
     *
     * @param context
     * the current context for the view.
     */
    private void initializeViews(Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.sidespinner_view, this);

    }
    @Override
    protected void onFinishInflate() {

        super.onFinishInflate();  // inflate

        SideLeftBack = (ImageView) this.findViewById(R.id.sidebar_leftbackview);       // left back
        SideHome     = (ImageView) this.findViewById(R.id.sidebar_homeview)    ;       // home
        // SideCamera   = (ImageView) this.findViewById(R.id.sidebar_cameraview)  ;       // camera

        SideLeftBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(view.getContext(), "sidebar Left back", Toast.LENGTH_SHORT).show();
                ((Activity)view.getContext()).finish();     //  finish current activity

            }
        });   //  lelf back - previous page (回前一頁)

        SideHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(view.getContext(), "sidebar Home", Toast.LENGTH_SHORT).show();
                ((Activity)view.getContext()).finish();     //  finish current activity
                Intent intent = new Intent(view.getContext() , MainActivity.class) ;
                view.getContext().startActivity(intent);    // back to home

            }
        });   //  Home (回首頁)


        // 照相機
        /*
        SideCamera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "sidebar Camera", Toast.LENGTH_SHORT).show();
                // 相機對話框
                CameraDialog(view.getContext());  // camera dialog


            }
        });   // 相機

         */

    }

    private void CameraDialog(Context context)
    {
        // Camera's prompt dialog
        // 照相對話框


        View dialogView = LayoutInflater.
                from( getContext()).
                inflate(R.layout.cameradialog, null);   // inflate camera dialog

        ImageView img1, img2, img3 ;

        ImageView cameraView ;

        Button cancel , confirm ;

          cancel   = dialogView.findViewById(R.id.cancelbtn) ;    //  取消按鈕
          confirm  = dialogView.findViewById(R.id.confirmbtn) ;   // 確定按鈕
          countdownTitle = dialogView.findViewById(R.id.countdowntxt);  // count down title
          WaitTxt = dialogView.findViewById(R.id.waittxt);              // wait for a while
        // both dummy buttons are their names modification
          cancel_dummy = cancel  ;
          confirm_dummy = confirm ;

          img1 = dialogView.findViewById(R.id.img1) ;
          img2 = dialogView.findViewById(R.id.img2) ;
          img3 = dialogView.findViewById(R.id.img3) ;

          cameraView = dialogView.findViewById(R.id.cameraview) ;   // camera view

          laParams = (RelativeLayout.LayoutParams)cameraView.getLayoutParams();   // 取得  imageview (releativelayout)

           img1.setImageDrawable(getResources().getDrawable(ImageList[0] ));    // 設定圖像 -1
           img2.setImageDrawable(getResources().getDrawable(ImageList[1] ));    // 設定圖像 -2
           img3.setImageDrawable(getResources().getDrawable(ImageList[2] ));    // 設定圖像 -3

           // cancel.getBackground().setAlpha(0);   //

            // Drawable drawable = getResources().getDrawable(R.drawable.my) ;  //  item selector
            // img1.setImageDrawable(drawable);
            // img2.setImageDrawable(drawable);
            // img3.setImageDrawable(drawable);

            laParams.height = 365;    // camera's height
            laParams.width  = 700;    // camera's width
            cameraView.setLayoutParams(laParams); // 將上面要控制imageview所變成的大小設定進去的方法

            img1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(view.getContext(), "第一張圖", Toast.LENGTH_SHORT).show();
                cameraView.setImageDrawable(getResources().getDrawable( ImageList[0] ));

            }
        });   // 點擊換圖並確認該圖

        img2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(view.getContext(), "第二張圖", Toast.LENGTH_SHORT).show();
                cameraView.setImageDrawable(getResources().getDrawable( ImageList[1] ));


            }
        });   // 點擊換圖並確認該圖

        img3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(view.getContext(), "第三張圖", Toast.LENGTH_SHORT).show();
                cameraView.setImageDrawable(getResources().getDrawable( ImageList[2] ));

            }
        });   // 點擊換圖並確認該圖


        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        alert.setCancelable(false) ;    // 點擊外部不可關閉對話框

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(dialogView);   // 設定dialog的 view

        final AlertDialog dialog = alert.create();

        Button homebackbtn ;

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // dialog.dismiss();    // close the dialog

                timer.cancel();   // stop the countdown  timer

            }
        });   // 取消


        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // 拍攝 , 必須有一個倒數計時的顯示
                // 倒數計時 5 秒 就照相
                // basically, it needs a countdown timer which can countdown to zero .
                // timer.start() ;  // 倒數計時啟動
                if ( confirm.getText().equals("確定"))
                    Toast.makeText(context, "目前是確定", Toast.LENGTH_SHORT).show();
                else if ( confirm.getText().equals("拍攝"))
                    timer.start() ;  // 倒數計時啟動


            }
        });

    }  // end of showMultiLangDialog

    public CountDownTimer timer = new CountDownTimer(10000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            // 倒數中 ...
            long sec = millisUntilFinished / 1000 ;

            if ( sec != 0 ) {
                countdownTitle.setText("                               倒數" + (millisUntilFinished / 1000) + "秒");
                WaitTxt.setText("耐心等待");

            }
            else {
                countdownTitle.setText("                                 拍攝完成");
                WaitTxt.setText("");
                cancel_dummy.setText("重拍");
                confirm_dummy.setText("確定");

            }

        }

        @Override
        public void onFinish() {
            // 倒數完成
            countdownTitle.setEnabled(true);
            // countdownTitle.setText("");
        }
    };

    public void oncancel(View v) {
        timer.cancel();
    }

    /**
     * 开始倒计时
     * @param v
     */
    public void restart(View v) {
        timer.start();
    }


}


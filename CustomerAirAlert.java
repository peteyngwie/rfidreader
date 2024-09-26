package com.smartcity.cgs;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartcity.ShelterDetail;

import java.util.List;


class CustomAdapterAirAlert extends RecyclerView.Adapter<CustomAdapterAirAlert.MyViewHolder> {
    //create a list to pass our Model class
    List<com.smartcity.cgs.ModelAirAlert> modelList;
    Context context;

    Button RightUpButton ;

    RelativeLayout r ;

    TextView Address ;
    View cardview;

    public String
            Titlebundle ,     //  title of 避難所
            Addressbundle,    //  address of 避難所
            Timebundle ,      //  time of 避難所
            Distancebundle  ; //  distance of 避難所

    public int WhichOne  ;  // item's number

    private Activity activity ;


    public CustomAdapterAirAlert(List<com.smartcity.cgs.ModelAirAlert> modelList, Context context) {
        this.modelList = modelList;
        this.context = context;


    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate our custom view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.airalertcardview,parent,false);


        return new MyViewHolder(view);

    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //bind all custom views by its position
        //to get the positions we call our Model class

        final com.smartcity.cgs.ModelAirAlert model = modelList.get(position);
        // 寬高 : 70/30
        // 這裡是要動態調整 recyclerview 的  item's 長寬

        Toast.makeText(context, "改變", Toast.LENGTH_SHORT).show();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(1800,30) ;
        params.addRule(RelativeLayout.ALIGN_LEFT);   // 向左對齊
        params.setMargins(100,20,0,0);  // 設定切齊

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(100,35);

        // params.addRule(RelativeLayout.ALIGN_LEFT);   // 向左對齊
        // params.setMargins(50,0,0,0);  // 設定切齊

        ViewGroup.LayoutParams parms = holder.itemView.getLayoutParams();
        parms.height = 200;     // 每個 item 的高度
        parms.width  = 1000 ;   // 每個 item 的寬度

        //  避難所名稱 , 地址  , 時間及距離 , 人數 設定
        holder.Titlename.setText(model.getTitlename());   // 避難所名稱
        holder.Location.setText(model.getAddress());      // 避難所位址
        holder.TimeAndDistance.setText(model.getTimeAndDistance()); //  時間與距離
        holder.Amount.setText(model.getAmount());         // 人數

        // image setting

        holder.ShelterimageView.setImageDrawable(context.getResources().getDrawable(model.getArrowimage()));
        holder.LocationImageView.setImageDrawable(context.getResources().getDrawable(model.getLocationimage()));
        holder.TimeAndDistanceImageView.setImageDrawable(context.getResources().getDrawable(model.getPeopleWalkingimage()));
        holder.AmountImageView.setImageDrawable(context.getResources().getDrawable(model.getAmountimage()));

        if (position == 0 || position == 1 ) {
            RightUpButton.setText("公有建築");


            if ( position == 0 && position != 1 ) {    // 第一項

                Log.d("ccc", modelList.get(position).getAddress());
               //  holder.relativeLayout.setBackgroundColor(Color.parseColor("#567845"));

            }
            else if ( position ==1 && position!= 0 ) {  // 第二項


                Log.d("ccc", modelList.get(position).getAddress());
              //  holder.relativeLayout.setBackgroundColor(Color.parseColor("#567845"));

            }
        }
        else if ( position == 2 ) {   // 第三項

            Log.d("ccc", modelList.get(position).getAddress());

            // RightUpButton.setLayoutParams(params);  // 改變寬度: 70 dp 及 切齊的地方
            RightUpButton.setText("飯店");

            LayoutInflater factory = LayoutInflater.from(context.getApplicationContext());

            View layout = factory.inflate(R.layout.activity_air_defense_alert, null);

            Address = layout.findViewById(R.id.shelteraddresstxt) ; // 位址
            Address.setText("cccccc");

            Log.d("ccc", (String) Address.getText()) ;

            Address.setTextColor(Color.rgb(100, 255, 255));

        }
        //click listener
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "點了第 " + position + " 項", Toast.LENGTH_SHORT).show();

                // 點擊後跳轉到次頁 - 顯示避難所的細節

                // 取得目前的context, 這context 是指的是 carview 的 view , 而非 activity 的  view
                //
                context = v.getContext() ;
                ((Activity)context).overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // fade in/out  的特效
                ((Activity)context).finish(); // this line must be used ! But if you launch shelter detail , this activity has been killed
                Intent intent = new Intent();   // new a intent

                // Toast.makeText(context, "ggggggggggggggggggggggggggggggggggggggggggg", Toast.LENGTH_SHORT).show();

                intent.setClass(v.getContext() , ShelterDetail.class);  // 防空警報細部資料
                Bundle bundle = new Bundle();            // create a bundle
                bundle.putInt("whichone",position);      // 點擊了哪一個
                intent.putExtras(bundle);                // put bundle into the intent and jump to next activity
                v.getContext().startActivity(intent);

                // Intent intent = new Intent(context,DetailsActivity.class);
                // intent.putExtra("image",model.getImage());
                // intent.putExtra("name",model.getName());
                // intent.putExtra("tag",model.getTag());
                // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return modelList.size();
    }
    //all the custom view will be hold here or initialize here inside MyViewHolder
    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ShelterimageView , LocationImageView , TimeAndDistanceImageView , AmountImageView ;
        TextView Titlename, Location , TimeAndDistance, Amount;
        RelativeLayout relativeLayout;

        TextView Address ;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // imageview 的設定
            ShelterimageView = itemView.findViewById(R.id.arrowimage);      // 避難所
            LocationImageView = itemView.findViewById(R.id.locationimage);  // 位址
            TimeAndDistanceImageView = itemView.findViewById(R.id.timedistanceimage);  // 時間與距離
            AmountImageView = itemView.findViewById(R.id.amountimage);          // 人數
            Address = itemView.findViewById(R.id.locationtxt);                  // 地址

            // textview 的設定
            Titlename = itemView.findViewById(R.id.sheltername) ;  // 避難所名稱
            Location = itemView.findViewById(R.id.locationtxt) ;   // 位址
            TimeAndDistance = itemView.findViewById(R.id.timedistancetxt) ;   // 時間與距離
            Amount = itemView.findViewById(R.id.amounttxt) ;

            // 右上的按鈕

            RightUpButton = itemView.findViewById(R.id.descriptionButton) ;   // 按鈕

            relativeLayout = itemView.findViewById(R.id.item);
        }
    }

}

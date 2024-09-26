package com.smartcity.cgs;
import static android.app.Activity.OVERRIDE_TRANSITION_CLOSE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Context;
        import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.EventLogTags;
import android.util.Log;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;
        import java.util.List;
  class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {


              //create a list to pass our Model class
    List<Model> modelList;

    private List<Bitmap> bitmapList ;   // put bitmaps
    Context context;

     public String
            Titlebundle ,   // title of landscape
            Addressbundle,  // address of landscape
            Timebundle ,    //  time of landscape
            Distancebundle , // distance of landscape

            Descriptionbundle ;    // description of landscape

     public int WhichOne  ;  // item's number



    public CustomAdapter(List<Model> modelList, Context context) {

        this.modelList = modelList;
        this.context = context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //inflate our custom view

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.landscapecardviewlayout,parent,false);  // landscape item layout (景點導覽的項目)

        return new MyViewHolder(view);

    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //bind all custom views by its position
        //to get the positions we call our Model class

        final Model model = modelList.get(position);  // 建立 model 取出第幾個  item

        Toast.makeText(context, "第" + position +" 項", Toast.LENGTH_SHORT).show();

        // image icon 設定 - 主圖 , 小人 , 位置 ///////////////////

        // Bitmap bitmap = bitmapList.get(position);  // 取出第幾個 bitmap
        holder.MajorimageView.setImageBitmap(model.getMajorimage());  // 取出 bitmap 然後設進去 20240828  by peter

        // holder.MajorimageView.setImageDrawable(context.getResources().getDrawable(model.getMajorimage()));              // 主圖
        // holder.PeopleWalkingView.setImageDrawable(context.getResources().getDrawable(model.getPeopleWalkingimage()));   // 小人
        // holder.LocationView.setImageDrawable(context.getResources().getDrawable(model.getLocationimage()));             // 位置

        // 項目字串設定 - 每一項目中的各項

        holder.Titlename.setText(model.getTitlename());   //  setting 景點名稱
        holder.Titlename.setTextSize(45);                 // setting textsize
        holder.Address.setText(model.getAddress());       //  setting 位置

        holder.Time.setText(model.getTime());             //  setting 時間
        holder.Disatnce.setText(model.getDistance());     //  setting 距離


        //click listener
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context,LandscapeDetails.class);  // show 景點 location details
                assert intent != null ; //

                // copy those items from model
                // 取出景點關資料

                Titlebundle = modelList.get(position).getTitlename() ;
                Addressbundle = modelList.get(position).getAddress()  ;
                Timebundle =  modelList.get(position).getTime() ;
                Distancebundle = modelList.get(position).getDistance() ;
                Descriptionbundle = modelList.get(position).getDescription();

                WhichOne = position ;     // 按了哪一個 ( 第一 : 北門火車站 , 最後 : 森林之歌 )

                Log.d(TAG,"name ::" + Titlebundle) ;

                Bundle bundle = new Bundle() ;     // create a bundle for passing data to detail activity

                assert bundle != null ;            // assert bundle is available

                Toast.makeText(context, "XXXXXXXXXXXX" + position, Toast.LENGTH_SHORT).show();

                // 傳出 景點抬頭 , 時間 , 位址 , 距離
                bundle.putString("title",Titlebundle);          // the title of landscape
                bundle.putString("time",Timebundle);            // the time of landscape
                bundle.putString("address",Addressbundle);      // the title of landscape
                bundle.putString("distance", Distancebundle);   // the distance of landscape
                bundle.putString("description", Descriptionbundle)  ;  // the description of landscape
                bundle.putInt("position" , WhichOne);                  // 第幾個被按了

                // ((Activity)v.getContext()).overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);
                if (Build.VERSION.SDK_INT >= 34) {
                    // a nwer api , but it still is useless
                    ((Activity)v.getContext()).overrideActivityTransition(
                            Activity.OVERRIDE_TRANSITION_OPEN , R.anim.pop_in, R.anim.pop_out);
                }


                intent.putExtras(bundle)  ;    // put a bundle to intent and send it to next activity

                v.getContext().startActivity(intent);   // to landscape details activity

            }
        });
    }
    @Override

    public int getItemCount() {

        return modelList.size();   // 取出項目數


    }
    //all the custom view will be hold here or initialize here inside MyViewHolder
    class MyViewHolder extends RecyclerView.ViewHolder{
        // cardview 中每項的 item 宣告

        ImageView MajorimageView;       // 主左圖
        ImageView PeopleWalkingView ;   // 小人
        ImageView LocationView ;        // 位置

        TextView Titlename,   // 景點名稱
                 Address  ,   // 位置
                 Time ,       // 交通時間
                 Disatnce  ,      // 距離
                 Description ;    // 描述

        RelativeLayout relativeLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // 初始化 cardview 中的item

            MajorimageView = itemView.findViewById(R.id.majorimage);       // 主圖
            PeopleWalkingView = itemView.findViewById(R.id.peopleimage) ;  // 小人
            LocationView = itemView.findViewById(R.id.locationimage);      // 位置

            // 各項的資料  - 景點抬頭 , 地址 , 時間 , 距離

            Titlename = itemView.findViewById(R.id.descriptiontxt );
            Address = itemView.findViewById(R.id.addresstxt);
            Time = itemView.findViewById(R.id.timetxt);
            Disatnce = itemView.findViewById(R.id.distancetxt);
            relativeLayout = itemView.findViewById(R.id.item);

            /*

            Titlebundle = Titlename.getText().toString()  ;   // To get the title of landscape
            Log.d(TAG,"名稱 >>>  " + Titlename.getText().toString()) ;
            assert Titlebundle != null;
            Addressbundle = Address.getText().toString() ;    // 位址
            assert Addressbundle != null ;
            Timebundle = Time.getText().toString();           // 時間
            assert Timebundle !=null ;
            Distancebundle = Disatnce.getText().toString();   // 距離

             */

        }  // constructor
    }      // end of class MyViewHolder

      // 方法來動態添加圖片
      public void addImage(Bitmap bitmap) {
          bitmapList.add(bitmap);
          notifyItemInserted(bitmapList.size() - 1);
      }

}

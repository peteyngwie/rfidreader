package com.smartcity.cgs;
import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import java.util.ArrayList;

public class LandscapeGVAdapter extends ArrayAdapter<LandscapeModel> {

    private int itemWidth;   // grid item's width
    private int itemHeight;  // grid item's height 

    public LandscapeGVAdapter (@NonNull Context context, ArrayList<LandscapeModel> courseModelArrayList) {
        super(context, 0, courseModelArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;


        if (listitemView == null) {

            // Layout Inflater inflates each item to be displayed in GridView.

            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.card_item, parent, false);

        }

        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(view.getContext(), "第幾個" + position , Toast.LENGTH_SHORT).show();
            }
        });   // 按下哪個 item

        LandscapeModel courseModel = getItem(position);
        ImageView courseIV = listitemView.findViewById(R.id.idIVcourse);
        courseIV.setImageResource(courseModel.getImgid());

        courseIV.setScaleType(ImageView.ScaleType.FIT_XY);  // imageview fill out itself

        return listitemView;

    }  // end of  getview

}


package com.example.subtitulo;


import android.app.ActionBar;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    public SliderAdapter(Context context){
        this.context = context;
    }

    //Array
    public int[] slide_images = {

        R.drawable.step1,
        R.drawable.step2,
        R.drawable.step3
    };
    public int[] margins = {

            114,116,60
    };
    public String[] slide_heading = {
            "CONFUSED?", "HERE'S A PLAN", "START SUBTITULO"
    };
    public String[] slide_descs = {
            "Got amazing pictures? And can't think of a caption?",
            "Try the new-age deep-learning tech to assist you for the same!",
            "Think no more! Get started with Subtitulo today!"
    };
    @Override
    public int getCount() {
        return slide_heading.length;

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.slide_layout, container, false);
            ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
            TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
            TextView slideDesc = (TextView) view.findViewById(R.id.slide_desc);
        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_heading[position]);
        slideDesc.setText(slide_descs[position]);
//        RelativeLayout.LayoutParams imageLayoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
//                ActionBar.LayoutParams.WRAP_CONTENT);
//        imageLayoutParams.setMargins(margins[position],110,0,0);
//        slideImageView.setLayoutParams(imageLayoutParams);
        container.addView(view);
        return  view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}


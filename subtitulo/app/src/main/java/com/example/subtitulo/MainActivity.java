package com.example.subtitulo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import static android.graphics.Color.WHITE;
import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    private TextView[] mDots;
    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private  SliderAdapter sliderAdapter;
    private Button next;
    private Button prev;
    private  int mCurrentPage;
    String name;
    String responser;
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);
        sliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);
        mSlideViewPager.addOnPageChangeListener(viewListener);

        Intent intent = getIntent();
        String name = intent.getStringExtra("Name");


        responser = "HELLO THERE " + name + ". WELCOME TO SUB-TEA-TWO-LOW";

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.ENGLISH);
                }
            }
        });
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.ENGLISH);
                }
            }
        });



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentPage == 2){
                    t1.speak(responser, TextToSpeech.QUEUE_FLUSH, null,null);
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(MainActivity.this,Image.class);
                    startActivity(intent);
                }
                else {
                    mSlideViewPager.setCurrentItem(mCurrentPage + 1);
                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlideViewPager.setCurrentItem(mCurrentPage - 1);
            }
        });
    }

    public void addDotsIndicator(int position) {
        mDots = new TextView[3];
        mDotLayout.removeAllViews();

        try {
            for (int i = 0; i < mDots.length; i++) {

                mDots[i] = new TextView(this);
                mDots[i].setText(Html.fromHtml("&#8226;"));
                mDots[i].setTextSize(35);
                mDots[i].setTextColor(Color.WHITE);

                mDotLayout.addView(mDots[i]);

            }

            if (mDots.length > 0) {
                mDots[position].setTextColor(Color.BLACK);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG);
        }
    }
        ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                addDotsIndicator(position);

                mCurrentPage = position;

                if(position == 0){
                    next.setEnabled(true);
                    prev.setEnabled(false);
                    prev.setVisibility(View.INVISIBLE);
                    next.setText("Next");
                    prev.setText("");

                }
                else if(position == mDots.length - 1){
                    next.setEnabled(true);
                    prev.setEnabled(true);
                    prev.setVisibility(View.VISIBLE);
                    next.setText("Finish");
                    prev.setText("Back");

                }



                else{
                    next.setEnabled(true);
                    prev.setEnabled(true);
                    prev.setVisibility(View.VISIBLE);
                    next.setText("Next");
                    prev.setText("Back");

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }
}


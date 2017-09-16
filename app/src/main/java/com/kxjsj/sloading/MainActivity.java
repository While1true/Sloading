package com.kxjsj.sloading;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private SLoading sLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sLoading = (SLoading) findViewById(R.id.sloading);
    }

    public void c1(View v) {
        sLoading.startAnimator();
    }

    public void c2(View v) {
        sLoading.stopAnimator();
    }
    public void c3(View v) {
        sLoading.stopAnimator();
        sLoading.setType(sLoading.getType()==0?1:0);
        sLoading.startAnimator();
    }
    public void c4(View v) {
        sLoading.stopAnimator();
        sLoading.setNum((3+new Random().nextInt(6)))
                .setRadius(24+new Random().nextInt(48));
        sLoading.startAnimator();
    }
}

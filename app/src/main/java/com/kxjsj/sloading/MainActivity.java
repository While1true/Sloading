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

}

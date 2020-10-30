package com.example.textrecognition;

import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;

public class splashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread mythread = new Thread(){

            @Override
            public void run() {
                try {
                    sleep(2000);
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        mythread.start();

    }
}

package com.example.whatsapp.view.startup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.whatsapp.R;
import com.example.whatsapp.view.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class splashScreenActivity extends AppCompatActivity {
    TextView akshay, by;
    ImageView logo;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        akshay=findViewById(R.id.akshay);
        by=findViewById(R.id.by);
        logo=findViewById(R.id.logo);
        YoYo.with(Techniques.Hinge).duration(2800).playOn(logo);
        YoYo.with(Techniques.SlideInRight).duration(2000).playOn(akshay);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(splashScreenActivity.this, MainActivity.class));
                    finish();
                }
            }, 3000);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(splashScreenActivity.this, WelcomeScreenActivity.class));
                    finish();
                }
            }, 3000);
        }
    }
}
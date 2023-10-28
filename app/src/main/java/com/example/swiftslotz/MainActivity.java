package com.example.swiftslotz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.swiftslotz.activities.LoginActivity;
import com.example.swiftslotz.utilities.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BaseActivity {

    Animation from_t,from_b;
    ImageView splashImg,logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // get the full screen view
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // assign the animations
        from_t= AnimationUtils.loadAnimation(this,R.anim.from_top);
        from_b= AnimationUtils.loadAnimation(this,R.anim.from_bottom);

        // asign images
        splashImg=findViewById(R.id.splashImg);
        logo=findViewById(R.id.splashLogo);

        // set the animations
        splashImg.setAnimation(from_t);
        logo.setAnimation(from_b);


        // forwarding the page
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user != null) {
                    startActivity(new Intent(MainActivity.this, BaseActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }

                finish();
            }
        }, 3000);


    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//        startActivity(intent);
//    }
}

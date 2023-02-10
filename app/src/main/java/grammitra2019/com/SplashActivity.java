package grammitra2019.com;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private TextView     mSplashWelcomeMsg, mSplashAppnameMsg;
    private ImageView    mSplashAppLogo;
    private Intent       mIntent;
    private FirebaseAuth mFirebaseAuth;
    public  long         mBackPressedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);

        loginIDconfigue();
        fade();

        mFirebaseAuth = Utility.FIREBASE_AUTH;

        // if admin is logged in ....
        if (mFirebaseAuth.getCurrentUser() != null) {
            if (Utility.FIREBASE_AUTH.getCurrentUser().getUid().equals("hnPwOtM56gTC1rqmznVBAC30Brm1")) {
                startActivity(new Intent(SplashActivity.this, AdminActivity.class));
                finish();
            } else {

                // if user is logged in ...
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    // if admin/user not logged in ....
                    mIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashActivity.this, mSplashAppLogo, "applogo");
                    startActivity(mIntent, activityOptionsCompat.toBundle());
                    finish();

                }
            }, 3000);
        }
    }

    private void loginIDconfigue() {

        mSplashAppLogo = findViewById(R.id.splash_imgview_logo);
        mSplashAppnameMsg = findViewById(R.id.splash_logo_name);
        mSplashWelcomeMsg = findViewById(R.id.splsh_wlcm_msg);

    }

    private void fade() {

        //animation start for only this activity
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        mSplashAppLogo.startAnimation(animation1);
        mSplashAppnameMsg.startAnimation(animation1);
        mSplashWelcomeMsg.startAnimation(animation1);
    }

    @Override
    public void onBackPressed() {

        // when button is pressed
        mBackPressedTime = System.currentTimeMillis();

        if (mBackPressedTime + 2000 > System.currentTimeMillis()) {
            mIntent = new Intent(Intent.ACTION_MAIN);
            mIntent.addCategory(Intent.CATEGORY_HOME);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mIntent);
            finish();
            super.onBackPressed();

        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
    }
}
package project.kyawmyoag.doctormanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.Wave;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import project.kyawmyoag.doctormanager.Registration.MainActivity;


public class splash extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    private boolean InternetCheck = true;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        spinner = (ProgressBar)findViewById(R.id.spinkit);
        Sprite wave = new Wave();
        spinner.setIndeterminate(wave.isVisible());
        PostDelayedMethod();
    }

    public void PostDelayedMethod(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean InternetResult=checkConnection();
                if (InternetResult){
                    Intent intent = new Intent(splash.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    spinner.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.GONE);
                    DialogAppear();
                }
            }
        },SPLASH_TIME_OUT);
    }
    public void DialogAppear(){
        new FancyAlertDialog.Builder(splash.this)
                .setTitle("အင်တာနက်မရှိပါ။")
                .setBackgroundColor(Color.parseColor("#5002a4"))  //Don't pass R.color.colorvalue
                .setMessage("အင်တာနက် ချိတ်ဆက်ရန်လိုအပ်သည်။")
                .setNegativeBtnText("ထွက်မည်")
                .setPositiveBtnBackground(Color.parseColor("#FF4081"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("ချိတ်ဆက်ပြီး")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setIcon(R.drawable.error64, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        spinner.setVisibility(View.VISIBLE);
                        PostDelayedMethod();
                        //startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        finish();

                    }
                })
                .build();
    }


    protected boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo !=null && netInfo.isConnectedOrConnecting()){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkConnection (){
        if (isOnline()){
            InternetCheck = true;
            return InternetCheck;
        } else {
            InternetCheck = false;
            return InternetCheck;
        }

    }
}
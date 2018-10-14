package com.tubug.wall.ballrotate.wallpaper;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.tubug.wall.ballrotate.DensityDpToPx;
import com.tubug.wall.ballrotate.MainActivity;
import com.tubug.wall.ballrotate.R;


/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class WallpaperPreferenceActivity extends Activity {
    SharedPreferences preferences;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private int mClickId;
    private boolean isAdLoaded = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        Switch dragRotate = findViewById(R.id.switch_drag_rotate);
        dragRotate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.edit().putBoolean(Const.TAG_DRAG, b).apply();
            }
        });
        dragRotate.setChecked(preferences.getBoolean(Const.TAG_DRAG, true));


        Switch autoRotate = findViewById(R.id.switch_auto_rotate);
        autoRotate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.edit().putBoolean(Const.TAG_AUTO_ROTATE, b).apply();
            }
        });
        autoRotate.setChecked(preferences.getBoolean(Const.TAG_AUTO_ROTATE, true));

        RadioGroup radioGroup = findViewById(R.id.bg_group);
        radioGroup.check(preferences.getInt(Const.TAG_BG_TEXURE, R.id.radio_default));
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                preferences.edit().putInt(Const.TAG_BG_TEXURE, i).apply();
            }
        });

        RadioGroup ballGroup = findViewById(R.id.ball_group);
        ballGroup.check(preferences.getInt(Const.TAG_BALL_TYPE, R.id.radio_baskball));
        ballGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                preferences.edit().putInt(Const.TAG_BALL_TYPE, i).apply();
            }
        });

        SeekBar seekBar = findViewById(R.id.sk_scale);
        seekBar.setProgress(preferences.getInt(Const.TAG_SCALE_RATE, 30));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                preferences.edit().putInt(Const.TAG_SCALE_RATE, seekBar.getProgress()).apply();
            }
        });
        findViewById(R.id.btn_preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickId = R.id.btn_preview;
                if(!showAd()){
                    toPreview();
                }
            }
        });

        findViewById(R.id.btn_choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickId = R.id.btn_choose;
                if(!showAd()) {
                    toChoose();
                }
            }
        });

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, getString(R.string.app_admob_id));
        addAdView();
    }
    private void toPreview(){
        Intent intent = new Intent(WallpaperPreferenceActivity.this, MainActivity.class);
        WallpaperPreferenceActivity.this.startActivity(intent);
    }
    private void toChoose(){
        Toast obj = Toast.makeText(WallpaperPreferenceActivity.this, "Choose " + getString(R.string.app_name_wallpaper) + " from the list", Toast.LENGTH_LONG);
        obj.setGravity(17, 0, 0);
        ((Toast) (obj)).show();
        Intent Intent = new Intent();
        Intent.setAction("android.service.wallpaper.LIVE_WALLPAPER_CHOOSER");
        startActivity(Intent);
    }


    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.loader);
        builder.setTitle(R.string.tip_title);
        builder.setMessage(R.string.tip_msg);
        builder.setPositiveButton(R.string.btn_sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton(R.string.btn_preview, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    private void addAdView() {
        ViewGroup.LayoutParams ad_layout_params =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        mAdView = new AdView(this.getApplicationContext());
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(getString(R.string.app_admob_banner_id));
//        mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        mAdView.setLayoutParams(ad_layout_params);
        ((LinearLayout) findViewById(R.id.ad_layout)).addView(mAdView);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.app_admob_interstitial_id));
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                isAdLoaded = true;
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                isAdLoaded = false;
                if(mClickId == R.id.btn_choose){
                    toChoose();
                }else if(mClickId == R.id.btn_preview){
                    toPreview();
                }
                mClickId = 0;
            }
        });

    }

    private boolean showAd(){
        if(isAdLoaded && mInterstitialAd != null && mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
            return true;
        }
        return false;
    }
}

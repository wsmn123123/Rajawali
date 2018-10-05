package com.tubug.wall.ballrotate.wallpaper;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.tubug.wall.ballrotate.MainActivity;
import com.tubug.wall.ballrotate.R;


/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class WallpaperPreferenceActivity extends Activity {
    SharedPreferences preferences;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mHandler = new Handler(getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(isFinishing()){
                    mInterstitialAd.show();
                }
            }
        };
        preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        Switch dragRotate = findViewById(R.id.switch_drag_rotate);
        dragRotate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.edit().putBoolean(Const.TAG_DRAG,b).apply();
            }
        });
        dragRotate.setChecked(preferences.getBoolean(Const.TAG_DRAG,true));


        Switch autoRotate = findViewById(R.id.switch_auto_rotate);
        autoRotate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.edit().putBoolean(Const.TAG_AUTO_ROTATE,b).apply();
            }
        });
        autoRotate.setChecked(preferences.getBoolean(Const.TAG_AUTO_ROTATE,true));

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, getString(R.string.app_admob_id));
        addAdView();
    }

    private void addAdView(){
        ViewGroup.LayoutParams ad_layout_params =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        mAdView = new AdView(this.getApplicationContext());
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(getString(R.string.app_admob_banner_id));
//        mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        mAdView.setLayoutParams(ad_layout_params);
        ((LinearLayout)findViewById(R.id.ad_layout)).addView(mAdView);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.app_admob_interstitial_id));
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mHandler.sendEmptyMessage(0);
            }
            @Override
            public void onAdFailedToLoad(int errorCode) { }
            @Override
            public void onAdOpened() { }
            @Override
            public void onAdLeftApplication() { }
            @Override
            public void onAdClosed() {
                //mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }
}

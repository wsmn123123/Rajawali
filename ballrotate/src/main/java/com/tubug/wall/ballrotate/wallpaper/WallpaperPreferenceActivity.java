package com.tubug.wall.ballrotate.wallpaper;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.tubug.wall.ballrotate.MainActivity;
import com.tubug.wall.ballrotate.R;


/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class WallpaperPreferenceActivity extends Activity {
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        Switch dragRotate = findViewById(R.id.switch_drag_rotate);
        dragRotate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.edit().putBoolean(Const.TAG_DRAG,b).apply();
            }
        });
        dragRotate.setChecked(preferences.getBoolean(Const.TAG_DRAG,false));


        Switch autoRotate = findViewById(R.id.switch_auto_rotate);
        autoRotate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferences.edit().putBoolean(Const.TAG_AUTO_ROTATE,b).apply();
            }
        });
        autoRotate.setChecked(preferences.getBoolean(Const.TAG_AUTO_ROTATE,false));
    }


}

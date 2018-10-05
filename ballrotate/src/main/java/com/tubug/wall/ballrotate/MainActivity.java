package com.tubug.wall.ballrotate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;


import com.tubug.wall.ballrotate.wallpaper.BallRenderer;
import com.tubug.wall.ballrotate.wallpaper.WallpaperPreferenceActivity;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.EllipticalOrbitAnimation3D;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.ISurfaceRenderer;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.view.ISurface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends Activity implements View.OnTouchListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences preferences;
    protected ISurface mRenderSurface;
    protected ISurfaceRenderer mRenderer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rajawali_textureview_fragment);
        findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WallpaperPreferenceActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        preferences.registerOnSharedPreferenceChangeListener(this);
        // Find the TextureView
        mRenderSurface = (ISurface) findViewById(R.id.rajwali_surface);
        ((View) mRenderSurface).setOnTouchListener(this);
//         Create the renderer
        mRenderer = createRenderer();
        mRenderer.setFrameRate(25);
        onBeforeApplyRenderer();
        applyRenderer();
    }


    protected void onBeforeApplyRenderer() {
    }

    protected void applyRenderer() {
        mRenderSurface.setSurfaceRenderer(mRenderer);
    }

    public ISurfaceRenderer createRenderer() {
        return new BallRenderer(this.getApplicationContext(),PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                ((BallRenderer) mRenderer).onTouchEvent(event);
                break;
//                ((BallRenderer) mRenderer).moveSelectedObject(event.getX(),
//                        event.getY());
//                ((BallRenderer)mRenderer).rotateObj(event.getX(),event.getY());
//                break;
//                ((BallRenderer) mRenderer).stopMovingSelectedObject();
//                break;
        }
        return true;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        ((BallRenderer)mRenderer).preferenceChanged(sharedPreferences,s);
    }
}

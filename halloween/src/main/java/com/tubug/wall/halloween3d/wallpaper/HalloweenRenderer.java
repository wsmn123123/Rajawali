package com.tubug.wall.halloween3d.wallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.MotionEvent;


import org.rajawali3d.Object3D;
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.util.ObjectColorPicker;

import javax.microedition.khronos.opengles.GL10;

public class HalloweenRenderer extends WallpaperRenderer {

    private ObjectColorPicker mPicker;
    private Object3D mSelectedObject;

    private int[] mViewport;
    private double[] mNearPos4;
    private double[] mFarPos4;
    private Vector3 mNearPos;
    private Vector3 mFarPos;
    private Vector3 mNewObjPos;
    private Matrix4 mViewMatrix;
    private Matrix4 mProjectionMatrix;
    private float touchBeginX,touchBeginY;
    RotateOnAxisAnimation earthAnim;
    RotateOnAxisAnimation wallAnim;
    SharedPreferences mpreferences;

    private HalloweenScene halloweenScene;

    public HalloweenRenderer(Context context, SharedPreferences preferences) {
        super(context);
        mpreferences = preferences;
        halloweenScene = new HalloweenScene(this,mpreferences);

    }

    @Override
    protected void initScene() {
        try {
            halloweenScene.init(mContext);
            replaceAndSwitchScene(getCurrentScene(), halloweenScene);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    @Override
    public void onResume() {
        super.onResume();
        if(mbPrefernceChanged){
            mbPrefernceChanged = false;
            halloweenScene = new HalloweenScene(this,mpreferences);
            initScene();
        }
    }

    @Override
    public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
        super.onRenderSurfaceSizeChanged(gl, width, height);

        halloweenScene.onRenderSurfaceSizeChanged(gl,width,height);
    }
    @Override
    public void onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                halloweenScene.getObjectAt(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
//                moveSelectedObject(event.getX(),
//                        event.getY());
                halloweenScene.rotateObj(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_UP:
                halloweenScene.stopMovingSelectedObject();
                break;
        }
    }
    public void preferenceChanged(SharedPreferences sharedPreferences, String s) {
        mbPrefernceChanged = true;
    }
    private boolean mbPrefernceChanged = false;


}
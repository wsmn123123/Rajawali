package com.tubug.wall.ballrotate.wallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;


import com.tubug.wall.ballrotate.R;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.animation.TranslateAnimation3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.MaterialManager;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.methods.SpecularMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.NormalMapTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.scene.ASceneFrameCallback;
import org.rajawali3d.scene.Scene;
import org.rajawali3d.scenegraph.IGraphNode;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;
import org.rajawali3d.util.RajLog;

import javax.microedition.khronos.opengles.GL10;

public class BallRenderer extends WallpaperRenderer {

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

    private BallScene ballScene;

    public BallRenderer(Context context,SharedPreferences preferences) {
        super(context);
        mpreferences = preferences;
        ballScene = new BallScene(this,mpreferences);

    }

    @Override
    protected void initScene() {
        try {
            ballScene.init();
            replaceAndSwitchScene(getCurrentScene(),ballScene);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    @Override
    public void onResume() {
        super.onResume();
        if(mbPrefernceChanged){
            mbPrefernceChanged = false;
            ballScene = new BallScene(this,mpreferences);
            initScene();
        }
    }

    @Override
    public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
        super.onRenderSurfaceSizeChanged(gl, width, height);

        ballScene.onRenderSurfaceSizeChanged(gl,width,height);
    }
    @Override
    public void onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ballScene.getObjectAt(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
//                moveSelectedObject(event.getX(),
//                        event.getY());
                ballScene.rotateObj(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_UP:
                ballScene.stopMovingSelectedObject();
                break;
        }
    }
    public void preferenceChanged(SharedPreferences sharedPreferences, String s) {
        mbPrefernceChanged = true;
    }
    private boolean mbPrefernceChanged = false;


}
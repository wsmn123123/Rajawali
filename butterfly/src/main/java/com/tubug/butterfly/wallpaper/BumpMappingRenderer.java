package com.tubug.butterfly.wallpaper;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.tubug.butterfly.R;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.animation.TranslateAnimation3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.methods.SpecularMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.NormalMapTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.util.GLU;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;
import org.rajawali3d.util.RajLog;

import javax.microedition.khronos.opengles.GL10;

public class BumpMappingRenderer extends WallpaperRenderer implements OnObjectPickedListener {
    private PointLight mLight;
    private Object3D mEarth;
    private Animation3D mLightAnim;
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

    public BumpMappingRenderer(Context context) {
        super(context);
    }

    @Override
    protected void initScene() {
        mViewport = new int[] { 0, 0, getViewportWidth(), getViewportHeight() };
        mNearPos4 = new double[4];
        mFarPos4 = new double[4];
        mNearPos = new Vector3();
        mFarPos = new Vector3();
        mNewObjPos = new Vector3();
        mViewMatrix = getCurrentCamera().getViewMatrix();
        mProjectionMatrix = getCurrentCamera().getProjectionMatrix();
        mPicker = new ObjectColorPicker(this);
        mPicker.setOnObjectPickedListener(this);

        mLight = new PointLight();
        mLight.setPosition(-2, -2, 0);
        mLight.setPower(5f);
        mLight.setLookAt(0,0,0);
        mLight.setColor(0.5f,0.5f,0.5f);


        getCurrentScene().addLight(mLight);
        getCurrentCamera().setPosition(0, 0, 7);


        try {
            Plane cube = new Plane(18, 12, 2, 2);
            Material material1 = new Material();
            material1.setDiffuseMethod(new DiffuseMethod.Lambert());
            material1.enableLighting(true);
            material1.addTexture(new Texture("wallDiffuseTex", R.drawable.masonry_wall_texture));
            material1.addTexture(new NormalMapTexture("wallNormalTex", R.drawable.masonry_wall_normal_map));
            material1.setColorInfluence(0);
            cube.setMaterial(material1);
            cube.setZ(-2);
            getCurrentScene().addChild(cube);

            RotateOnAxisAnimation anim = new RotateOnAxisAnimation(Vector3.Axis.Y, -5, 5);
            anim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
            anim.setDurationMilliseconds(5000);
            anim.setTransformable3D(cube);
            getCurrentScene().registerAnimation(anim);
            anim.play();

            mEarth = new Sphere(1, 32, 32);
            mEarth.setZ(-0f);
            getCurrentScene().addChild(mEarth);

            Material material2 = new Material();
            material2.setDiffuseMethod(new DiffuseMethod.Lambert());
            material2.setSpecularMethod(new SpecularMethod.Phong(Color.RED, 150));
            material2.enableLighting(true);
            material2.addTexture(new Texture("earthDiffuseTex", R.drawable.basketball_t));
            material2.addTexture(new NormalMapTexture("eartNormalTex", R.drawable.basketball_nor_1));
            material2.setColorInfluence(0);
            mEarth.setMaterial(material2);
            mPicker.registerObject(mEarth);

            earthAnim = new RotateOnAxisAnimation(new Vector3(1,1,0), 359);
            earthAnim.setDurationMilliseconds(6000);
            earthAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
            earthAnim.setTransformable3D(mEarth);
            getCurrentScene().registerAnimation(earthAnim);
//            earthAnim.play();



        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }

        mLightAnim = new TranslateAnimation3D(new Vector3(-5, 2, 4),
                new Vector3(5, -2, 4));
        mLightAnim.setDurationMilliseconds(4000);
        mLightAnim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
        mLightAnim.setTransformable3D(mLight);
        mLightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        getCurrentScene().registerAnimation(mLightAnim);
        mLightAnim.play();

        DirectionalLight mLight = new DirectionalLight();
        mLight.setPosition(2, 2, 0);
        mLight.setPower(3f);
        mLight.setColor(0.5f,0.5f,0.5f);
        mLight.setLookAt(0,0,0);
        getCurrentScene().addLight(mLight);

        mLightAnim = new TranslateAnimation3D(new Vector3(-5, -3, 2),
                new Vector3(5, -3, 2));
        mLightAnim.setDurationMilliseconds(4000);
        mLightAnim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
        mLightAnim.setTransformable3D(mLight);
        mLightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        getCurrentScene().registerAnimation(mLightAnim);
        mLightAnim.play();

    }
    @Override
    public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
        super.onRenderSurfaceSizeChanged(gl, width, height);
        mViewport[2] = getViewportWidth();
        mViewport[3] = getViewportHeight();
        mViewMatrix = getCurrentCamera().getViewMatrix();
        mProjectionMatrix = getCurrentCamera().getProjectionMatrix();
    }
    @Override
    public void onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getObjectAt(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
//                moveSelectedObject(event.getX(),
//                        event.getY());
                    rotateObj(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_UP:
                stopMovingSelectedObject();
                break;
        }
    }
    public void getObjectAt(float x, float y) {
        mPicker.getObjectAt(x, y);
        touchBeginX = x;
        touchBeginY = y;
    }

    public void rotateObj(float x, float y){
        if(mSelectedObject == null){
            return;
        }
        float dx = x - touchBeginX;
        float dy = y - touchBeginY;


        String str = "";
        double distance = Math.sqrt(dx*dx + dy*dy);
        str += " dis:";
        str += distance;
        str += " dx:";
        str += dx;
        str += " dy:";
        str += dy;
        double angle = Math.asin(dx/mViewport[2]);
        double degree = angle * 180;
        if(Math.abs(degree) <= 180) {
            mSelectedObject.setRotY(mSelectedObject.getRotY() + -degree);
        }
//
        str += " rY:";
        str+= -degree;
        angle = Math.asin(dy / mViewport[3]);
        degree = angle  * 180;
//        if(Math.abs(degree) <= 180) {
//            mSelectedObject.setRotZ(mSelectedObject.getRotZ() + -degree);
//        }
        str+=" rX:";
        str+= -degree;

        RajLog.w("move:"+str);

        touchBeginX = x;
        touchBeginY = y;
    }
//    public void moveSelectedObject(float x, float y) {
//        if (mSelectedObject == null)
//            return;
//
//        //
//        // -- unproject the screen coordinate (2D) to the camera's near plane
//        //
//
//        GLU.gluUnProject(x, getViewportHeight() - y, 0, mViewMatrix.getDoubleValues(), 0,
//                mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mNearPos4, 0);
//
//        //
//        // -- unproject the screen coordinate (2D) to the camera's far plane
//        //
//
//        GLU.gluUnProject(x, getViewportHeight() - y, 1.f, mViewMatrix.getDoubleValues(), 0,
//                mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mFarPos4, 0);
//
//        //
//        // -- transform 4D coordinates (x, y, z, w) to 3D (x, y, z) by dividing
//        // each coordinate (x, y, z) by w.
//        //
//
//        mNearPos.setAll(mNearPos4[0] / mNearPos4[3], mNearPos4[1]
//                / mNearPos4[3], mNearPos4[2] / mNearPos4[3]);
//        mFarPos.setAll(mFarPos4[0] / mFarPos4[3],
//                mFarPos4[1] / mFarPos4[3], mFarPos4[2] / mFarPos4[3]);
//
//        //
//        // -- now get the coordinates for the selected object
//        //
//
//        double factor = (Math.abs(mSelectedObject.getZ()) + mNearPos.z)
//                / (getCurrentCamera().getFarPlane() - getCurrentCamera()
//                .getNearPlane());
//
//        mNewObjPos.setAll(mFarPos);
//        mNewObjPos.subtract(mNearPos);
//        mNewObjPos.multiply(factor);
//        mNewObjPos.add(mNearPos);
//
//        mSelectedObject.setX(mNewObjPos.x);
//        mSelectedObject.setY(mNewObjPos.y);
//    }

    public void stopMovingSelectedObject() {
        if(earthAnim.isPaused()){
            earthAnim.play();
        }
        mSelectedObject = null;
    }
    @Override
    public void onObjectPicked(@NonNull Object3D object) {
        mSelectedObject = object;
        earthAnim.pause();
    }

    @Override
    public void onNoObjectPicked() {
        RajLog.w("No object picked!");
    }
}
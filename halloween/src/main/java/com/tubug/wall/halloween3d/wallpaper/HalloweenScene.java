package com.tubug.wall.halloween3d.wallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;


import com.tubug.wall.halloween3d.R;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.ColorAnimation3D;
import org.rajawali3d.animation.IAnimationListener;
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.animation.TranslateAnimation3D;
import org.rajawali3d.bounds.BoundingBox;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.lights.SpotLight;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.methods.SpecularMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.NormalMapTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.scene.Scene;
import org.rajawali3d.scenegraph.IGraphNode;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;
import org.rajawali3d.util.RajLog;

import javax.microedition.khronos.opengles.GL10;

class HalloweenScene extends Scene implements OnObjectPickedListener {
    private static final String TAG = "HalloweenScene";
    Plane wall;
//    private Object3D mBall;
    SharedPreferences mpreferences;
    RotateOnAxisAnimation earthAnim;
    RotateOnAxisAnimation wallAnim;
    private SpotLight mFrontLight;
    private DirectionalLight mBackLight;
    private Animation3D mFountLightAnim,mBackLightAnim;
    private ObjectColorPicker mPicker;
    private Object3D mSelectedObject;
    private Object3D nanGua;

    private int[] mViewport;
    private double[] mNearPos4;
    private double[] mFarPos4;
    private Vector3 mNearPos;
    private Vector3 mFarPos;
    private Vector3 mNewObjPos;
    private Matrix4 mViewMatrix;
    private Matrix4 mProjectionMatrix;
    private float touchBeginX,touchBeginY;
    Camera mCamera2;
    public HalloweenScene(Renderer renderer, SharedPreferences preferences) {
        super(renderer);
        mpreferences = preferences;
//        displaySceneGraph(true);
      initCamera();
    }

    public HalloweenScene(Renderer renderer, SharedPreferences preferences, IGraphNode.GRAPH_TYPE type) {
        super(renderer, type);
        mpreferences = preferences;
//        displaySceneGraph(true);
        initCamera();
    }

    private void initCamera(){
        mCamera2 = new Camera(); //Lets create a second camera for the scene.
        mCamera2.setPosition(0, 0, 7);
        mCamera2.setLookAt(0.0f, 0.0f, 0.0f);
        mCamera2.setFarPlane(10);
        mCamera2.setFieldOfView(60);
        mCamera2.updateFrustum(mInvVPMatrix);
        replaceAndSwitchCamera(mCamera2,0);
    }

    public void init(Context pContext)throws Exception{
        mViewport = new int[] { 0, 0, mRenderer.getViewportWidth(), mRenderer.getViewportHeight() };
        mNearPos4 = new double[4];
        mFarPos4 = new double[4];
        mNearPos = new Vector3();
        mFarPos = new Vector3();
        mNewObjPos = new Vector3();
        mViewMatrix = getCamera().getViewMatrix();
        mProjectionMatrix = getCamera().getProjectionMatrix();
        mPicker = new ObjectColorPicker(mRenderer);
        mPicker.setOnObjectPickedListener(this);

       float z = mpreferences.getInt(Const.TAG_SCALE_RATE,30);
       z /= 10f;
       z = 7-z;
        mCamera2.setPosition(0, 0, z);
        initWall();

        initNanGua();

//        mFountLightAnim = new TranslateAnimation3D(new Vector3(-5, 2, 4),
//                new Vector3(5, -2, 4));
//        mFountLightAnim.setDurationMilliseconds(4000);
//        mFountLightAnim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
//        mFountLightAnim.setTransformable3D(mFrontLight);
//        mFountLightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
//        registerAnimation(mFountLightAnim);
//        mFountLightAnim.play();

        initBackLight();
        startTintLight();

    }

    public void getObjectAt(float x, float y) {
        mPicker.getObjectAt(x, y);
        touchBeginX = x;
        touchBeginY = y;
    }

    private void initBackLight(){
        BoundingBox boundingBox =  nanGua.getBoundingBox();
        double v = Vector3.distanceTo(boundingBox.getMax(),boundingBox.getMin());
        Log.e(TAG,"v:"+v);

        mFrontLight = new SpotLight();
        mFrontLight.setPosition(0, 0, 0);
        mFrontLight.setPower(5f);
        mFrontLight.setLookAt(0,0,v/2);
        mFrontLight.setColor(1f,1f,0f);
        addLight(mFrontLight);

        mBackLight = new DirectionalLight();
        mBackLight.setPosition(2, 2, 0);
        mBackLight.setPower(3f);
        mBackLight.setColor(0.5f,0.5f,0.5f);
        mBackLight.setLookAt(0,0,0);
        addLight(mBackLight);


        mBackLightAnim = new TranslateAnimation3D(new Vector3(-5, 5, -0),
                new Vector3(5, 3, -2));
        mBackLightAnim.setDurationMilliseconds(4000);
        mBackLightAnim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
        mBackLightAnim.setTransformable3D(mBackLight);
        mBackLightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        registerAnimation(mBackLightAnim);
        mBackLightAnim.play();
    }

    private void initNanGua() throws ParsingException {
        LoaderOBJ objParser = new LoaderOBJ(mRenderer, R.raw.untitled1_obj);
        objParser.parse();
        nanGua = objParser.getParsedObject();
        nanGua.setPosition(0,0,0);
        addChild(nanGua);

//        mBall = new Sphere(1, 32, 32);
//        mBall.setZ(0f);
//        addChild(mBall);

        Material material2 = new Material();
        material2.setDiffuseMethod(new DiffuseMethod.Lambert());
        material2.setSpecularMethod(new SpecularMethod.Phong(Color.WHITE, 520));
        material2.enableLighting(true);
//        int ballType = mpreferences.getInt(Const.TAG_BALL_TYPE,R.id.radio_baskball);
//        if(ballType == R.id.radio_football){
//            material2.addTexture(new Texture("earthDiffuseTex", R.drawable.football));
//            material2.addTexture(new NormalMapTexture("eartNormalTex", R.drawable.football_nor));
//        }else {
//            material2.addTexture(new Texture("earthDiffuseTex", R.drawable.basketball));
//            material2.addTexture(new NormalMapTexture("eartNormalTex", R.drawable.basketball_nor_1));
//        }
//        material2.setColorInfluence(0);
//        mBall.setMaterial(material2);

        Material phong = new Material();
        phong.enableLighting(true);
        phong.setDiffuseMethod(new DiffuseMethod.Lambert());
//        phong.setSpecularMethod(new SpecularMethod.Phong(Color.WHITE, 60));
        nanGua.setMaterial(phong);
        nanGua.setColor(0xfafaff00);

        if(mpreferences.getBoolean(Const.TAG_DRAG,true))
        {
            mPicker.registerObject(nanGua);
        }

//        if(mpreferences.getBoolean(Const.TAG_AUTO_ROTATE,true))
        {
            earthAnim = new RotateOnAxisAnimation(new Vector3(1, 1, 0), 359);
            earthAnim.setDurationMilliseconds(6000);
            earthAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
            earthAnim.setTransformable3D(nanGua);
            registerAnimation(earthAnim);
//            earthAnim.play();
        }
    }

    private void initWall() throws ATexture.TextureException {
        wall = new Plane(20, 20, 2, 2);
        Material material1 = new Material();
        material1.setDiffuseMethod(new DiffuseMethod.Lambert());
        material1.enableLighting(true);
        int bgTexId = mpreferences.getInt(Const.TAG_BG_TEXURE,R.id.radio_default);

        switch (bgTexId){
            case R.id.radio_black_eye:
                material1.addTexture(new Texture("wallDiffuseTex", R.drawable.halloween_bg1));
                material1.addTexture(new NormalMapTexture("wallNormalTex", R.drawable.halloween_bg1_map));
                break;
            case R.id.radio_yellow_light:
                material1.addTexture(new Texture("wallDiffuseTex", R.drawable.halloween_bg2));
                break;
            default:
                material1.addTexture(new Texture("wallDiffuseTex", R.drawable.halloween_bg3));
                material1.addTexture(new NormalMapTexture("wallNormalTex", R.drawable.halloween_bg3_map));
                break;
        }

        material1.setColorInfluence(0);
        wall.setMaterial(material1);
        wall.setZ(-2);
        addChild(wall);

        RotateOnAxisAnimation wallAnim = new RotateOnAxisAnimation(Vector3.Axis.Y, -5, 5);
        wallAnim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
        wallAnim.setDurationMilliseconds(5000);
        wallAnim.setTransformable3D(wall);
        registerAnimation(wallAnim);
        wallAnim.play();
    }
    private void startTintLight(){
        final Object3D target = new Object3D();
        Animation3D anim = new ColorAnimation3D(0xaaff1111, 0xffffff11);
        anim.setTransformable3D(target);
        anim.setDurationMilliseconds(2000);
        anim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
        anim.registerListener(new IAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationUpdate(Animation animation, double v) {
                float power = (float) (v * 5.0f);
                mFrontLight.setPower(Math.abs(power));
                mFrontLight.setLookAt(nanGua.getRotX(),nanGua.getRotY(),nanGua.getRotZ());
            }
        });
        registerAnimation(anim);
        anim.play();
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
        angle = mSelectedObject.getRotY() + -degree;
        if(angle <= 90 && angle >= -90) {
            mSelectedObject.setRotY(mSelectedObject.getRotY() + -degree);
        }
//
//        str += " rY:";
//        str+= -degree;
        angle = Math.asin(dy / mViewport[3]);
        degree = angle  * 180;
        angle = mSelectedObject.getRotX() + -degree;
        if(angle <= 75 && angle >= -75) {
            mSelectedObject.setRotZ(mSelectedObject.getRotX() + -degree);
        }
//        str+=" rX:";
//        str+= -degree;

        RajLog.w("move:"+str);

        touchBeginX = x;
        touchBeginY = y;
    }

    public void stopMovingSelectedObject() {
        if(earthAnim != null && earthAnim.isPaused()){
//            earthAnim.play();
        }
        mSelectedObject = null;
    }

    @Override
    public void onObjectPicked(@NonNull Object3D object) {
        mSelectedObject = object;
        if(earthAnim != null){
            earthAnim.pause();
        }
    }

    @Override
    public void onNoObjectPicked() {
        RajLog.w("No object picked!");
    }

    public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
        mViewport[2] = width;
        mViewport[3] = height;
        mViewMatrix = getCamera().getViewMatrix();
        mProjectionMatrix = getCamera().getProjectionMatrix();
    }
}

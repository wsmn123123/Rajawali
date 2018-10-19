package com.tubug.wall.ballrotate.wallpaper;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.tubug.wall.ballrotate.R;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.animation.TranslateAnimation3D;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.methods.SpecularMethod;
import org.rajawali3d.materials.textures.NormalMapTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.scene.Scene;
import org.rajawali3d.scenegraph.IGraphNode;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;
import org.rajawali3d.util.RajLog;

import javax.microedition.khronos.opengles.GL10;

class BallScene extends Scene implements OnObjectPickedListener {
    Plane wall;
    private Object3D mBall;
    SharedPreferences mpreferences;
    RotateOnAxisAnimation earthAnim;
    RotateOnAxisAnimation wallAnim;
    private PointLight mFrontLight;
    private DirectionalLight mBackLight;
    private Animation3D mFountLightAnim,mBackLightAnim;
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
    Camera mCamera2;
    public BallScene(Renderer renderer,SharedPreferences preferences) {
        super(renderer);
        mpreferences = preferences;
//        displaySceneGraph(true);
      initCamera();
    }

    public BallScene(Renderer renderer, SharedPreferences preferences, IGraphNode.GRAPH_TYPE type) {
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

    public void init()throws Exception{
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

        wall = new Plane(18, 12, 2, 2);
        Material material1 = new Material();
        material1.setDiffuseMethod(new DiffuseMethod.Lambert());
        material1.enableLighting(true);
        int bgTexId = mpreferences.getInt(Const.TAG_BG_TEXURE,R.id.radio_default);
        int diffTexId = R.drawable.masonry_wall_texture;
        int norTexId = R.drawable.masonry_wall_normal_map;
        switch (bgTexId){
            case R.id.radio_bones:
                diffTexId = R.drawable.born;
                 norTexId = R.drawable.born_nor;
                break;
            case R.id.radio_drop:
                diffTexId = R.drawable.drop;
                norTexId = R.drawable.drop_nor;
                break;
            case R.id.radio_wave:
                diffTexId = R.drawable.wave;
                norTexId = R.drawable.wave_nor;
                break;
        }

        material1.addTexture(new Texture("wallDiffuseTex", diffTexId));
        material1.addTexture(new NormalMapTexture("wallNormalTex", norTexId));
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

        mBall = new Sphere(1, 32, 32);
        mBall.setZ(0f);
        addChild(mBall);

        Material material2 = new Material();
        material2.setDiffuseMethod(new DiffuseMethod.Lambert());
        material2.setSpecularMethod(new SpecularMethod.Phong(Color.WHITE, 520));
        material2.enableLighting(true);
        int ballType = mpreferences.getInt(Const.TAG_BALL_TYPE,R.id.radio_baskball);
        if(ballType == R.id.radio_football){
            material2.addTexture(new Texture("earthDiffuseTex", R.drawable.football));
            material2.addTexture(new NormalMapTexture("eartNormalTex", R.drawable.football_nor));
        }else if(ballType == R.id.radio_volleyball){
            material2.addTexture(new Texture("earthDiffuseTex", R.drawable.volleyball));
//        material2.addTexture(new NormalMapTexture("eartNormalTex", R.drawable.basketball_nor_1));
        }else {
            material2.addTexture(new Texture("earthDiffuseTex", R.drawable.basketball));
            material2.addTexture(new NormalMapTexture("eartNormalTex", R.drawable.basketball_nor_1));
        }

        material2.setColorInfluence(0);
        mBall.setMaterial(material2);

//        Material phong = new Material();
//        phong.enableLighting(true);
//        phong.setDiffuseMethod(new DiffuseMethod.Lambert());
//        phong.setSpecularMethod(new SpecularMethod.Phong(Color.WHITE, 60));
//        mMonkey3.setMaterial(phong);
//        mMonkey3.setColor(0xff00ff00);

        if(mpreferences.getBoolean(Const.TAG_DRAG,true)){
            mPicker.registerObject(mBall);
        }

        if(mpreferences.getBoolean(Const.TAG_AUTO_ROTATE,true)) {
            earthAnim = new RotateOnAxisAnimation(new Vector3(1, 1, 0), 359);
            earthAnim.setDurationMilliseconds(6000);
            earthAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
            earthAnim.setTransformable3D(mBall);
            registerAnimation(earthAnim);
            earthAnim.play();
        }

        mFrontLight = new PointLight();
        mFrontLight.setPosition(-2, -2, 0);
        mFrontLight.setPower(3f);
        mFrontLight.setLookAt(0,0,0);
        mFrontLight.setColor(0.5f,0.5f,0.5f);
        addLight(mFrontLight);


        mFountLightAnim = new TranslateAnimation3D(new Vector3(-5, 2, 4),
                new Vector3(5, -2, 4));
        mFountLightAnim.setDurationMilliseconds(4000);
        mFountLightAnim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
        mFountLightAnim.setTransformable3D(mFrontLight);
        mFountLightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        registerAnimation(mFountLightAnim);
        mFountLightAnim.play();


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
//        str += " rY:";
//        str+= -degree;
//        angle = Math.asin(dy / mViewport[3]);
//        degree = angle  * 180;
//        if(Math.abs(degree) <= 180) {
//            mSelectedObject.setRotZ(mSelectedObject.getRotZ() + -degree);
//        }
//        str+=" rX:";
//        str+= -degree;

        RajLog.w("move:"+str);

        touchBeginX = x;
        touchBeginY = y;
    }

    public void stopMovingSelectedObject() {
        if(earthAnim != null && earthAnim.isPaused()){
            earthAnim.play();
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

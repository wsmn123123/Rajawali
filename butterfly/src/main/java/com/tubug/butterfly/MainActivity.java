package com.tubug.butterfly;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.tubug.butterfly.wallpaper.BumpMappingRenderer;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.ColorAnimation3D;
import org.rajawali3d.animation.EllipticalOrbitAnimation3D;
import org.rajawali3d.animation.IAnimationListener;
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.bounds.BoundingBox;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.lights.SpotLight;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.loader.fbx.LoaderFBX;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.methods.SpecularMethod;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.ISurfaceRenderer;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.scene.ASceneFrameCallback;
import org.rajawali3d.view.ISurface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends Activity implements View.OnTouchListener {
    private static final String TAG = "MainActivity";
    protected ISurface mRenderSurface;
    protected ISurfaceRenderer mRenderer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rajawali_textureview_fragment);

        // Find the TextureView
        mRenderSurface = (ISurface) findViewById(R.id.rajwali_surface);
        ((View) mRenderSurface).setOnTouchListener(this);
        // Create the renderer
        mRenderer = createRenderer();
        onBeforeApplyRenderer();
        applyRenderer();
    }


    protected void onBeforeApplyRenderer() {
    }

    protected void applyRenderer() {
        mRenderSurface.setSurfaceRenderer(mRenderer);
    }

    public ISurfaceRenderer createRenderer() {
        return new LoadModelRenderer(this.getApplicationContext());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ((BumpMappingRenderer) mRenderer).getObjectAt(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
//                ((BumpMappingRenderer) mRenderer).moveSelectedObject(event.getX(),
//                        event.getY());
                ((BumpMappingRenderer)mRenderer).rotateObj(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_UP:
                ((BumpMappingRenderer) mRenderer).stopMovingSelectedObject();
                break;
        }
        return true;
    }


    private final class LoadModelRenderer extends Renderer {
        private PointLight mLight;
        private PointLight spotLight;
        private Object3D mObjectGroup;
        private Animation3D mCameraAnim, mLightAnim;

        public LoadModelRenderer(Context context) {
            super(context);
        }
        @Override
        public void onRenderSurfaceCreated(EGLConfig config, GL10 gl, int width, int height) {
//            if (exampleFragment != null) exampleFragment.showLoader();
            super.onRenderSurfaceCreated(config, gl, width, height);
//            if (exampleFragment != null) exampleFragment.hideLoader();
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
        }

        @Override
        protected void initScene()  {
            mLight = new PointLight();
            mLight.setPosition(0, 0, 0);
            mLight.setPower(5);
            getCurrentScene().addLight(mLight);

            spotLight = new PointLight();
            spotLight.setPower(2f);
            spotLight.enableLookAt();
            spotLight.setPosition(0, 0.0, 4.0);
            spotLight.setLookAt(0, 0, 0);
            getCurrentScene().addLight(spotLight);


            getCurrentCamera().setPosition(0,0,10);
            getCurrentCamera().setLookAt(0,0,0);


            LoaderOBJ objParser = new LoaderOBJ(mContext.getResources(),
                    mTextureManager, R.raw.untitled_obj);
            try {
                objParser.parse();
                mObjectGroup = objParser.getParsedObject();

//                LoaderFBX fbxLoader = new LoaderFBX(this, R.raw.teapot);
//                fbxLoader.parse();
//                mObjectGroup = fbxLoader.getParsedObject();
//                mObjectGroup.setY(-.5f);

//                BoundingBox bsphere = mObjectGroup.getGeometry()
//                        .getBoundingBox();
//                bsphere.transform(mObjectGroup.getModelMatrix());
//                Vector3 size = Vector3.subtractAndCreate(bsphere.getMax(),bsphere.getMin());
//               double dis =  size.distanceTo(0,0,0);
                BoundingBox boundingBox =  mObjectGroup.getBoundingBox();
                double v = Vector3.distanceTo(boundingBox.getMax(),boundingBox.getMin());
                Log.d(TAG,"v:"+v);
                spotLight.setPosition(0,0,v);
                mObjectGroup.setPosition(0,0,0);
                Material material2 = new Material();
                material2.setDiffuseMethod(new DiffuseMethod.Lambert());
                material2.setSpecularMethod(new SpecularMethod.Phong(Color.RED, 150));
                material2.enableLighting(true);

//                try {
//                    material2.addTexture(new Texture("earthDiffuseTex", R.drawable.football));
//                    material2.addTexture(new NormalMapTexture("eartNormalTex", R.drawable.broccoli_bump));
//                } catch (ATexture.TextureException e) {
//                    e.printStackTrace();
//                }
                material2.setColorInfluence(0);
//                mObjectGroup.getChildByName("broccoli").setMaterial(material2);
                getCurrentScene().addChild(mObjectGroup);

                mCameraAnim = new RotateOnAxisAnimation(Vector3.Axis.Y, 360);
                mCameraAnim.setDurationMilliseconds(8000);
                mCameraAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
//                mCameraAnim.setTransformable3D(getCurrentCamera());
            } catch (ParsingException e) {
                e.printStackTrace();
            }

            mLightAnim = new EllipticalOrbitAnimation3D(new Vector3(),
                    new Vector3(0, 4, 0), Vector3.getAxisVector(Vector3.Axis.Z), 0,
                    360, EllipticalOrbitAnimation3D.OrbitDirection.CLOCKWISE);

            mLightAnim.setDurationMilliseconds(3000);
            mLightAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
//            mLightAnim.setTransformable3D(spotLight);

            getCurrentScene().registerAnimation(mCameraAnim);
//            getCurrentScene().registerAnimation(mLightAnim);

//            mCameraAnim.play();
//            mLightAnim.play();


            RotateOnAxisAnimation lightAnim =  new RotateOnAxisAnimation(Vector3.Axis.Y, 360);
            lightAnim.setDurationMilliseconds(8000);
            lightAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
            lightAnim.setTransformable3D(spotLight);
            getCurrentScene().registerAnimation(lightAnim);
            lightAnim.play();


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
                    mLight.setPower(Math.abs(power));
                }
            });
            getCurrentScene().registerAnimation(anim);
            anim.play();

            getCurrentScene().registerFrameCallback(new ElapsedTimeFrameCallback());
        }

        private final class ElapsedTimeFrameCallback extends ASceneFrameCallback {

            @Override
            public void onPreFrame(long sceneTime, double deltaTime) {
                // Do nothing
            }

            @Override
            public void onPreDraw(long l, double v) {

            }

            @Override
            public void onPostFrame(final long sceneTime, double deltaTime) {

            }

            @Override
            public boolean callPostFrame() {
                return true;
            }
        }

    }


}

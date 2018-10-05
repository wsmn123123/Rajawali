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

public class MainActivity extends Activity implements View.OnTouchListener {

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

        // Find the TextureView
//        mRenderSurface = (ISurface) findViewById(R.id.rajwali_surface);
//        ((View) mRenderSurface).setOnTouchListener(this);
        // Create the renderer
//        mRenderer = createRenderer();
//        mRenderer.setFrameRate(25);
//        onBeforeApplyRenderer();
//        applyRenderer();
    }


    protected void onBeforeApplyRenderer() {
    }

    protected void applyRenderer() {
        mRenderSurface.setSurfaceRenderer(mRenderer);
    }

//    public ISurfaceRenderer createRenderer() {
//        return new BallRenderer(this.getApplicationContext(),);
//    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ((BallRenderer) mRenderer).getObjectAt(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
//                ((BallRenderer) mRenderer).moveSelectedObject(event.getX(),
//                        event.getY());
                ((BallRenderer)mRenderer).rotateObj(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_UP:
                ((BallRenderer) mRenderer).stopMovingSelectedObject();
                break;
        }
        return true;
    }


    private final class LoadModelRenderer extends Renderer {
        private PointLight mLight;
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
        protected void initScene() {
            mLight = new PointLight();
            mLight.setPosition(0, 0, 4);
            mLight.setPower(3);

            getCurrentScene().addLight(mLight);
            getCurrentCamera().setZ(16);

//            LoaderOBJ objParser = new LoaderOBJ(mContext.getResources(),
//                    mTextureManager, R.raw.rose_obj);
//            try {
//                objParser.parse();
//                mObjectGroup = objParser.getParsedObject();

//                BoundingBox bsphere = mObjectGroup.getGeometry()
//                        .getBoundingBox();
//                bsphere.transform(mObjectGroup.getModelMatrix());
//                Vector3 size = Vector3.subtractAndCreate(bsphere.getMax(),bsphere.getMin());
//               double dis =  size.distanceTo(0,0,0);
//                BoundingBox boundingBox =  mObjectGroup.getBoundingBox();
//                double v = Vector3.distanceTo(boundingBox.getMax(),boundingBox.getMin());
//                mObjectGroup.setPosition(0,-v / 2,0);
//
//                getCurrentScene().addChild(mObjectGroup);
//
//                mCameraAnim = new RotateOnAxisAnimation(Vector3.Axis.Y, 360);
//                mCameraAnim.setDurationMilliseconds(8000);
//                mCameraAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
//                mCameraAnim.setTransformable3D(mObjectGroup);
//            } catch (ParsingException e) {
//                e.printStackTrace();
//            }

            mLightAnim = new EllipticalOrbitAnimation3D(new Vector3(),
                    new Vector3(0, 10, 0), Vector3.getAxisVector(Vector3.Axis.Z), 0,
                    360, EllipticalOrbitAnimation3D.OrbitDirection.CLOCKWISE);

            mLightAnim.setDurationMilliseconds(3000);
            mLightAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
            mLightAnim.setTransformable3D(mLight);

            getCurrentScene().registerAnimation(mCameraAnim);
            getCurrentScene().registerAnimation(mLightAnim);

            mCameraAnim.play();
            mLightAnim.play();
        }

    }

}

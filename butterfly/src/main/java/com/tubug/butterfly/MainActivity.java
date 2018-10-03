package com.tubug.butterfly;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.EllipticalOrbitAnimation3D;
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.bounds.BoundingBox;
import org.rajawali3d.bounds.BoundingSphere;
import org.rajawali3d.bounds.IBoundingVolume;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.ISurfaceRenderer;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.view.ISurface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends Activity {

    protected ISurface mRenderSurface;
    protected ISurfaceRenderer mRenderer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rajawali_textureview_fragment);

        // Find the TextureView
        mRenderSurface = (ISurface) findViewById(R.id.rajwali_surface);
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

            LoaderOBJ objParser = new LoaderOBJ(mContext.getResources(),
                    mTextureManager, R.raw.xx_obj);
            try {
                objParser.parse();
                mObjectGroup = objParser.getParsedObject();

//                BoundingBox bsphere = mObjectGroup.getGeometry()
//                        .getBoundingBox();
//                bsphere.transform(mObjectGroup.getModelMatrix());
//                Vector3 size = Vector3.subtractAndCreate(bsphere.getMax(),bsphere.getMin());
//               double dis =  size.distanceTo(0,0,0);
                BoundingBox boundingBox =  mObjectGroup.getBoundingBox();
                double v = Vector3.distanceTo(boundingBox.getMax(),boundingBox.getMin());
                mObjectGroup.setPosition(0,-v / 2,0);

                getCurrentScene().addChild(mObjectGroup);

                mCameraAnim = new RotateOnAxisAnimation(Vector3.Axis.Y, 360);
                mCameraAnim.setDurationMilliseconds(8000);
                mCameraAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
                mCameraAnim.setTransformable3D(mObjectGroup);
            } catch (ParsingException e) {
                e.printStackTrace();
            }

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

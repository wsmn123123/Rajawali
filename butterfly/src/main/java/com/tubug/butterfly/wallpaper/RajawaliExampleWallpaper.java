package com.tubug.butterfly.wallpaper;

import com.tubug.butterfly.Preferences;

import org.rajawali3d.renderer.ISurfaceRenderer;
import org.rajawali3d.util.RajLog;
import org.rajawali3d.view.ISurface;
import org.rajawali3d.wallpaper.Wallpaper;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class RajawaliExampleWallpaper extends Wallpaper {

    private ISurfaceRenderer mRenderer;

    @Override
    public Engine onCreateEngine() {
        RajLog.v("Creating wallpaper engine.");
        final int renderer = Integer.parseInt(Preferences.getInstance(this).getWallpaperRendererPreference());
            RajLog.i("Creating wallpaper engine: " + renderer);

        mRenderer = new BumpMappingRenderer(this);

            // TODO: I'm sure there is a better way to do this
            switch (renderer) {
//                case 0:
//                    mRenderer = new BasicRenderer(this, null);
//                    break;
//                case 1:
//                    mRenderer = new SkyboxRenderer(this, null);
//                    break;
//                case 2:
//                    mRenderer = new CollisionDetectionRenderer(this, null);
//                    break;
//                case 3:
//                    mRenderer = new TerrainRenderer(this, null);
//                    break;
//                case 4:
//                    mRenderer = new UpdateVertexBufferRenderer(this, null);
//                    break;
//                case 5:
//                    mRenderer = new BloomEffectRenderer(this, null);
//                    break;
//                case 6:
//                    mRenderer = new GaussianBlurFilterRenderer(this, null);
//                    break;
//                case 7:
//                    mRenderer = new MultiPassRenderer(this, null);
//                    break;
//                case 8:
//                    mRenderer = new CanvasTextRenderer(this, null);
//                    break;
//                default:
//                    mRenderer = new WallpaperRenderer(this);
            }
        return new WallpaperEngine(getBaseContext(), mRenderer,
                                   ISurface.ANTI_ALIASING_CONFIG.NONE);
    }
}

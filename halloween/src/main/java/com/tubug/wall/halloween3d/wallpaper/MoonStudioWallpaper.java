package com.tubug.wall.halloween3d.wallpaper;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;


import org.rajawali3d.util.RajLog;
import org.rajawali3d.view.ISurface;
import org.rajawali3d.wallpaper.Wallpaper;

/**
 * @author Jared Woolston (jwoolston@idealcorp.com)
 */
public class MoonStudioWallpaper extends Wallpaper implements SharedPreferences.OnSharedPreferenceChangeListener{

    private HalloweenRenderer mRenderer;
    private SharedPreferences preferences;
    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        preferences.registerOnSharedPreferenceChangeListener(this);
        RajLog.setDebugEnabled(false);
    }


    @Override
    public WallpaperService.Engine onCreateEngine() {
        RajLog.w("Creating wallpaper engine.");
//        final int renderer = Integer.parseInt(Preferences.getInstance(this).getWallpaperRendererPreference());
//            RajLog.i("Creating wallpaper engine: " + renderer);

        mRenderer = new HalloweenRenderer(this,preferences);
        mRenderer.setFrameRate(25);

            // TODO: I'm sure there is a better way to do this
//            switch (renderer) {
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
//            }
//        mRenderer = new WallpaperRenderer(this);
        return new WallpaperEngine(getBaseContext(), mRenderer,
                                   ISurface.ANTI_ALIASING_CONFIG.NONE);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        mRenderer.preferenceChanged(sharedPreferences,s);
    }
}

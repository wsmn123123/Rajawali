package com.tubug.wall.ballrotate.wallpaper;

import com.tubug.wall.ballrotate.R;

import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.NormalMapTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.scene.Scene;
import org.rajawali3d.scenegraph.IGraphNode;

class BallScene extends Scene {
    Plane wall;

    public BallScene(Renderer renderer) {
        super(renderer);
        displaySceneGraph(true);
        replaceAndSwitchCamera(renderer.getCurrentCamera(), 0);
    }

    public BallScene(Renderer renderer, IGraphNode.GRAPH_TYPE type) {
        super(renderer, type);
        displaySceneGraph(true);
        replaceAndSwitchCamera(renderer.getCurrentCamera(), 0);
    }

    public void init()throws Exception{
        wall = new Plane(18, 12, 2, 2);
        Material material1 = new Material();
        material1.setDiffuseMethod(new DiffuseMethod.Lambert());
        material1.enableLighting(true);
        material1.addTexture(new Texture("wallDiffuseTex", R.drawable.masonry_wall_texture));
        material1.addTexture(new NormalMapTexture("wallNormalTex", R.drawable.masonry_wall_normal_map));
        material1.setColorInfluence(0);
        wall.setMaterial(material1);
        wall.setZ(-2);
        mRenderer.getCurrentScene().addChild(wall);


    }

}

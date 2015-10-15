/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.fx.tutorials.gravity;

import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Universe {

    private Node anchor;
    private final SolarSystem solarSystem;

    public Universe(SolarSystem solarSystem) {
        this.solarSystem = solarSystem;
    }

    public Group newUI(double radius) {
        Group root = new Group();

        Sphere sky = new Sphere(radius, 16);

//        sky.setRotationAxis(new Point3D(1, 0, 0));
//        sky.setRotate(90);
        PhongMaterial m = new PhongMaterial(Color.WHITE);
//
        m.setDiffuseMap(new Image(getClass().getResourceAsStream("tycho8.jpg")));
        m.setSelfIlluminationMap(new Image(getClass().getResourceAsStream("tycho8.jpg")));

        sky.setMaterial(m);

        sky.setDrawMode(DrawMode.FILL);
        sky.setCullFace(CullFace.FRONT);

        Group solarSystemRoot = new Group();
        solarSystemRoot.getChildren().addAll(sky, solarSystem.toJavaFX());

        this.anchor = solarSystemRoot;

        root.getChildren().add(solarSystemRoot);

        VFX3DUtil.addMouseBehavior(solarSystemRoot, root, null);

        root.addEventHandler(ScrollEvent.ANY, new ZoomBehavior(anchor));

        return root;
    }

    public SubScene newScene() {
        // Create camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setFarClip(500);

        camera.getTransforms().add(new Translate(0, 0, -150));

        Group root = newUI(200);

        // add camera as node to scene graph
        root.getChildren().add(camera);

        // setup a subscene
        SubScene scene = new SubScene(root, 400, 400, true,
                SceneAntialiasing.BALANCED);
        scene.setCamera(camera);

        return scene;
    }

    /**
     * @return the anchor
     */
    public Node getAnchor() {
        return anchor;
    }

    class ZoomBehavior implements EventHandler<ScrollEvent> {

        private final Translate translateZ = new Translate();

        public ZoomBehavior(Node n) {
            n.getTransforms().add(translateZ);
        }

        @Override
        public void handle(ScrollEvent event) {
            double scale = solarSystem.getScale();
            scale += event.getDeltaY() * 0.0001;
            if (scale < 0.01) {
                scale = 0.01;
            }
            solarSystem.setScale(scale);

            double timeScale = solarSystem.getTimeScale();
            timeScale += event.getDeltaX() * 0.01;

            solarSystem.setTimeScale(timeScale);
        }
    }
}

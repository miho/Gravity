package eu.mihosoft.fx.tutorials.gravity;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Java FX Milk Glass Demo.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        // toplevel pane
        StackPane rootPane = new StackPane();

        SolarSystem solarSystem = new SolarSystem();

        Particle sun = new Particle();
//        sun.setColor(Color.YELLOW);
        sun.setTexture(new Image(getClass().getResourceAsStream("sunmap.jpg")));
        sun.setIlluminationMap(new Image(getClass().getResourceAsStream("sunmap.jpg")));
        sun.setRadius(4);

        sun.setMass(1.988544 * 1e30);
        sun.setR(5.440607969746718E+05, 1.291184237789655E+05, -2.366426995200343E+04);
        sun.setV(2.812876159621061E-03, 1.135404002354477E-02, -8.451005143556272E-05);

        solarSystem.addParticle(sun);

        Particle mercury = new Particle();
//        mercury.setColor(Color.LIGHTGRAY);
        mercury.setRadius(0.9);

        mercury.setMass(3.302 * 1e23);
        mercury.setR(9.467521718762722E+06, 4.516841265295381E+07, 2.837821706647277E+06);
        mercury.setV(-5.754918730631762E+01, 1.128923342528648E+01, 6.201516198653065E+00);

        mercury.setTexture(new Image(getClass().getResourceAsStream("mercurymap.jpg")));
//        mercury.setBumpMap(new Image(getClass().getResourceAsStream("mercurybump.jpg")));

        solarSystem.addParticle(mercury);

        Particle venus = new Particle();
//        venus.setColor(Color.ANTIQUEWHITE);
        venus.setRadius(1.5);
        venus.setTexture(new Image(getClass().getResourceAsStream("venusmap.jpg")));
//        venus.setBumpMap(new Image(getClass().getResourceAsStream("venusbump.jpg")));

        venus.setMass(48.685 * 1e23);

        venus.setR(6.227922176231733E+07, 8.875601624558377E+07, -2.371083671118647E+06);
        venus.setV(-2.884266600160281E+01, 1.987554053966993E+01, 1.936827588268871E+00);

        solarSystem.addParticle(venus);

        Particle earth = new Particle();
//        earth.setColor(Color.LIGHTBLUE);
        earth.setRadius(1.8);
        earth.setTexture(new Image(getClass().getResourceAsStream("earthmap.jpg")));

        earth.setMass(5.97219 * 1e24);
        earth.setR(1.415997854858718E+08, 4.903448305801202E+07, -2.623125933039933E+04);
        earth.setV(-1.024437306433082E+01, 2.805631550875579E+01, -2.068901522687128E-03);

        solarSystem.addParticle(earth);

        Particle mars = new Particle();
//        mars.setColor(Color.BROWN);
        mars.setTexture(new Image(getClass().getResourceAsStream("marsmap.jpg")));

        mars.setRadius(1.4);
        mars.setMass(6.4185 * 1e23);
        mars.setR(-1.868243966907549E+08, 1.625829536324923E+08, 7.978982103994295E+06);
        mars.setV(-1.495578873417891E+01, -1.623037808097023E+01, 2.671766635229478E-02);

        solarSystem.addParticle(mars);

        Particle jupiter = new Particle();
        jupiter.setTexture(new Image(getClass().getResourceAsStream("jupitermap.jpg")));

        jupiter.setRadius(3.8);
        jupiter.setMass(1898.13 * 1e24);
        jupiter.setR(-7.415787896507579E+08, 3.185705818345428E+08, 1.525989238631602E+07);
        jupiter.setV(-5.310217187020113E+00, -1.138968398795950E+01, 1.661377214488216E-01);

        solarSystem.addParticle(jupiter);

        Particle saturn = new Particle();
        saturn.setTexture(new Image(getClass().getResourceAsStream("saturnmap.jpg")));

        saturn.setRadius(3.5);
        saturn.setMass(5.68319 * 1e26);
        saturn.setR(-6.129078386199176E+08, -1.363426054679566E+09, 4.809920340769100E+07);
        saturn.setV(8.280434166975880E+00, -3.989700283579174E+00, -2.598442725937820E-01);

        solarSystem.addParticle(saturn);

        Particle uranus = new Particle();
        uranus.setTexture(new Image(getClass().getResourceAsStream("uranusmap.jpg")));

        uranus.setRadius(3.2);
        uranus.setMass(86.8103 * 1e24);
        uranus.setR(2.838672564744431E+09, 9.382029429658228E+08, -3.329121615713465E+07);
        uranus.setV(-2.186822920334619E+00, 6.148550596114975E+00, 5.112530132395499E-02);

        solarSystem.addParticle(uranus);

        Particle neptune = new Particle();
        neptune.setTexture(new Image(getClass().getResourceAsStream("neptunemap.jpg")));

        neptune.setRadius(3.0);
        neptune.setMass(102.41 * 1e24);
        neptune.setR(4.168536549806355E+09, -1.647290066539579E+09, -6.214531510753727E+07);
        neptune.setV(1.962040995224717E+00, 5.087023414173791E+00, -1.505998386566345E-01);

        solarSystem.addParticle(neptune);

        Particle halley = new Particle();
        halley.setColor(Color.WHITE);
        halley.setRadius(1);
        halley.setMass(2 * 1e14);
        halley.setR(-3.062879144795248E+09, 3.811478871128524E+09, -1.470690092228038E+09);
        halley.setV(-1.485510146110784E-02, 1.504766777956282E+00, -2.534487428307657E-01);

        solarSystem.addParticle(halley);

        Particle eros = new Particle();
        eros.setColor(Color.WHITE);
        eros.setRadius(0.3);
        eros.setMass(6.687 * 1e14);
        eros.setR(-1.845416074252093E+08, -2.621758113949205E+07, -3.210124467533641E+07);
        eros.setV(-7.274789935476060E-01, -2.792582336997302E+01, -3.129181377389385E+00);

        solarSystem.addParticle(eros);

        Particle chury = new Particle();
        chury.setColor(Color.WHITE);
        chury.setRadius(0.3);
        chury.setMass(1 * 1e13);
        chury.setR(-8.257658832669835E+07, 1.982623128356111E+08, 2.353959264802276E+07);
        chury.setV(-3.097214533812052E+01, -2.142759570616175E+00, 2.765586105209052E+00);

        solarSystem.addParticle(chury);

//        Particle sun2 = new Particle();
////        sun.setColor(Color.YELLOW);
//        sun2.setTexture(new Image(getClass().getResourceAsStream("sunmap.jpg")));
//        sun2.setIlluminationMap(new Image(getClass().getResourceAsStream("sunmap.jpg")));
//        sun2.setRadius(4);
//
//        sun2.setMass(1.988544 * 1e30);
//        sun2.setR(5.440607969746718E+05+3e8, 1.291184237789655E+05, -2.366426995200343E+04);
//        sun2.setV(2.812876159621061E-03-8, 1.135404002354477E-02+2e1, -8.451005143556272E-05);
//
//        solarSystem.addParticle(sun2);

        Universe universe = new Universe(solarSystem);

        solarSystem.runSimulation();

        SubScene uScene = universe.newScene();
        uScene.heightProperty().bind(rootPane.heightProperty());
        uScene.widthProperty().bind(rootPane.widthProperty());

        rootPane.getChildren().add(uScene);

        // create a scene with specified size
        Scene scene = new Scene(rootPane, 1024, 768);

        //setupODE(pane);
        // final stage setup
        primaryStage.setTitle("VUniverse Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}

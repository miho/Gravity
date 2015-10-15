package eu.mihosoft.fx.tutorials.gravity.twod;

import eu.mihosoft.fx.tutorials.gravity.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

/**
 * Java FX Milk Glass Demo.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Main extends Application {

    // Circle colors
    Color[] colors = {
        new Color(0.2, 0.5, 0.8, 1.0).saturate().brighter().brighter(),
        new Color(0.3, 0.2, 0.7, 1.0).saturate().brighter().brighter(),
        new Color(0.8, 0.3, 0.9, 1.0).saturate().brighter().brighter(),
        new Color(0.4, 0.3, 0.9, 1.0).saturate().brighter().brighter(),
        new Color(0.2, 0.5, 0.7, 1.0).saturate().brighter().brighter()};

    private long lastTimeStamp;

    private double remainingSimulationTime;

    private double time = 0;

    private Circle[] nodes;

    private Pane pane;

    private double initialX;
    private double initialY;
    private double initialZ;

    public FirstOrderDifferentialEquations createODE(
            final int numBodies, final double[] y, final double[] m,
            final boolean[] ignoreFlags, final double G) {

        return new FirstOrderDifferentialEquations() {

            @Override
            public int getDimension() {
                return numBodies * Particle.getStructSize();
            }

            @Override
            public void computeDerivatives(double t, double[] y, double[] yDot)
                    throws MaxCountExceededException, DimensionMismatchException {

                Particle pI = new Particle(y, m, ignoreFlags, 0);
                Particle pJ = new Particle(y, m, ignoreFlags, 1);

                // d2 rI / dt^2 = GmJ*(rJ-rI)/|rJ-rI|^3
                // http://www.physics.buffalo.edu/phy410-505/topic5/
                // http://physics.princeton.edu/~fpretori/Nbody/intro.htm
                // http://de.wikipedia.org/wiki/Newtonsches_Gravitationsgesetz
                // outer sum
                for (int i = 0; i < numBodies; i++) {

                    double aIX = 0;
                    double aIY = 0;
                    double aIZ = 0;

                    pI.setIndex(i);

                    if (pI.isIgnored()) {
                        continue;
                    }

                    // inner sum
                    for (int j = 0; j < numBodies; j++) {

                        if (i == j) {
                            continue;
                        }

                        pJ.setIndex(j);

                        if (pJ.isIgnored()) {
                            continue;
                        }

                        double rJMinusRIX = pJ.getRX() - pI.getRX();
                        double rJMinusRIY = pJ.getRY() - pI.getRY();
                        double rJMinusRIZ = pJ.getRZ() - pI.getRZ();

                        double magnitudeRJMinusRISquare
                                = rJMinusRIX * rJMinusRIX
                                + rJMinusRIY * rJMinusRIY
                                + rJMinusRIZ * rJMinusRIZ;

                        double magnitudeRJMinusRI
                                = Math.sqrt(magnitudeRJMinusRISquare);

                        aIX += pI.getMass() * pJ.getMass() * rJMinusRIX
                                / (magnitudeRJMinusRISquare * magnitudeRJMinusRI);
                        aIY += pI.getMass() * pJ.getMass() * rJMinusRIY
                                / (magnitudeRJMinusRISquare * magnitudeRJMinusRI);
                        aIZ += pI.getMass() * pJ.getMass() * rJMinusRIZ
                                / (magnitudeRJMinusRISquare * magnitudeRJMinusRI);
                    }

                    pI.setRDerivativeTo(yDot);

                    aIX *= G / pI.getMass();
                    aIY *= G / pI.getMass();
                    aIZ *= G / pI.getMass();

                    pI.setVDerivativeTo(yDot, aIX, aIY, aIZ);
                }
            }
        };
    }

    @Override
    public void start(Stage primaryStage) {

        // toplevel pane
        StackPane rootPane = new StackPane();

        // circle container
        pane = new Pane();

        // circle container is a child of the root pane
        rootPane.getChildren().add(pane);

        // background style for the container
        pane.setStyle("-fx-background-color: rgb(25,40,80)");

        // create a scene with size 1280x800
        Scene scene = new Scene(rootPane, 1024, 768);

        setupODE(pane);

        // final stage setup
        primaryStage.setTitle("Gravity Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupODE(Pane pane) {

        int numBodies = 6;

        double[] y = new double[numBodies * Particle.getStructSize()];
        double[] m = new double[numBodies];
        boolean[] ignoreFlags = new boolean[numBodies];

        double G = 6.672e-11 * 0.000000001;

        Particle sun = new Particle(y, m, ignoreFlags, 0);
        sun.setMass(1.988544 * 1e30);
        sun.setR(5.440607969746718E+05, 1.291184237789655E+05, -2.366426995200343E+04);
        sun.setV(2.812876159621061E-03, 1.135404002354477E-02, -8.451005143556272E-05);
        initialX = sun.getRX();
        initialY = sun.getRY();
        initialZ = sun.getRZ();

        Particle earth = new Particle(y, m, ignoreFlags, 1);
        earth.setMass(5.97219 * 1e24);
        earth.setR(1.415997854858718E+08, 4.903448305801202E+07, -2.623125933039933E+04);
        earth.setV(-1.024437306433082E+01, 2.805631550875579E+01, -2.068901522687128E-03);

        Particle mars = new Particle(y, m, ignoreFlags, 2);
        mars.setMass(6.4185 * 1e23);
        mars.setR(-1.868243966907549E+08, 1.625829536324923E+08, 7.978982103994295E+06);
        mars.setV(-1.495578873417891E+01, -1.623037808097023E+01, 2.671766635229478E-02);

        Particle venus = new Particle(y, m, ignoreFlags, 3);
        venus.setMass(48.685 * 1e23);

        venus.setR(6.227922176231733E+07, 8.875601624558377E+07, -2.371083671118647E+06);
        venus.setV(-2.884266600160281E+01, 1.987554053966993E+01, 1.936827588268871E+00);

        Particle mercury = new Particle(y, m, ignoreFlags, 4);
        mercury.setMass(3.302 * 1e23);
        mercury.setR(9.467521718762722E+06, 4.516841265295381E+07, 2.837821706647277E+06);
        mercury.setV(-5.754918730631762E+01, 1.128923342528648E+01, 6.201516198653065E+00);

        Particle moon = new Particle(y, m, ignoreFlags, 5);
        moon.setMass(734.9);
        moon.setR(1.412165075135136E+08, 4.890202513961375E+07, -1.451432212365419E+04);
        moon.setV(-9.914920907770702E+00, 2.714402406106908E+01, 8.249292317013790E-02);

        nodes = new Circle[6];

        nodes[0] = new Circle(0);
        nodes[0].setFill(Color.YELLOW);
        nodes[0].setRadius(50/*sun.getMass() * 3e-24*/);
        pane.getChildren().add(nodes[0]);
        nodes[1] = new Circle(0);
        nodes[1].setFill(Color.CORNFLOWERBLUE);
        nodes[1].setRadius(10);//earth.getMass() * 3e-24);
        pane.getChildren().add(nodes[1]);
        nodes[2] = new Circle(0);
        nodes[2].setFill(Color.BROWN);
        nodes[2].setRadius(10);//earth.getMass() * 3e-24);
        pane.getChildren().add(nodes[2]);
        nodes[3] = new Circle(0);
        nodes[3].setFill(Color.BISQUE);
        nodes[3].setRadius(10);//earth.getMass() * 3e-24);
        pane.getChildren().add(nodes[3]);
        nodes[4] = new Circle(0);
        nodes[4].setFill(Color.LIGHTGRAY);
        nodes[4].setRadius(10);//earth.getMass() * 3e-24);
        pane.getChildren().add(nodes[4]);

        nodes[5] = new Circle(0);
        nodes[5].setFill(Color.LIGHTGRAY);
        nodes[5].setRadius(5);//earth.getMass() * 3e-24);
        pane.getChildren().add(nodes[5]);

//        nodes[2] = new Circle(0);
//        nodes[2].setFill(Color.LIGHTGRAY);
//        nodes[2].setRadius(5);//moon.getMass() * 2e-22);
//        pane.getChildren().add(nodes[2]);
//        for (int i = 0; i < nodes.length; i++) {
//            Particle p = new Particle(y, m, ignoreFlags, i);
//            nodes[i] = new Circle(0);
//            nodes[i].setFill(colors[(int) (colors.length * Math.random())]);
//            nodes[i].setRadius(Math.sqrt(p.getMass()));
//            if (i == 0) {
//                nodes[i].setFill(Color.YELLOW);
//                nodes[i].setEffect(new BoxBlur(10, 10, 3));
//                nodes[i].setRadius(Math.min(p.getMass(), 45));
//            }
//
//            pane.getChildren().add(nodes[i]);
//        }
        initFrameListener(
                createODE(numBodies, y, m, ignoreFlags, G),
                y, m, ignoreFlags, 0.001);
    }

    public void initFrameListener(
            FirstOrderDifferentialEquations ode,
            double[] y,
            final double[] m,
            final boolean[] ignoreFlags, double dt) {

        final double timeScale = 3600 * 24 * 365;

        FirstOrderIntegrator integrator
                = new ClassicalRungeKuttaIntegrator(dt * timeScale);

        double[] yPrev = new double[y.length];
        double[] interpolatedY = new double[y.length];

        // create frame listener 
        AnimationTimer frameListener = new AnimationTimer() {

            @Override
            public void handle(long now) {

                // thanks to http://gafferongames.com/game-physics/fix-your-timestep/
                // measure elapsed time between last and current pulse (frame)
                double frameDuration = (now - lastTimeStamp) / 1e9;
                lastTimeStamp = now;

                // we don't allow frame durations above 2*dt
                if (frameDuration > 2 * dt) {
                    frameDuration = 2 * dt;
                }

                // add elapsed time to remaining simulation interval
                remainingSimulationTime += frameDuration;

                // copy current state to prev state
                System.arraycopy(y, 0, yPrev, 0, yPrev.length);

                // simulate remaining interval
                while (remainingSimulationTime >= dt) {

                    double scaledT = time * timeScale;
                    double scaledDT = dt * timeScale;

                    // integrate
                    try {
                        integrator.integrate(
                                ode, scaledT, y, scaledT + scaledDT, y);

                    } catch (Exception ex) {
                        ex.printStackTrace(System.err);
                    }
                    // remove integrated interval from remaining simulation time
                    remainingSimulationTime -= dt;

                    // update t
                    time = time + dt;
                }

                // interpolate state
                double alpha = remainingSimulationTime / dt;

                // set interpolated state
                for (int i = 0; i < y.length; i++) {
                    interpolatedY[i] = 
                            y[i] * alpha + yPrev[i] * (1.0 - alpha);
                }

                // update properties for visualization
                updateView(nodes, interpolatedY, m, ignoreFlags);
            }
        };

        // finally, start the framle listener
        frameListener.start();
    }

    public void updateView(
            Node[] nodes, double[] y, double[] m, boolean[] ignoreFlags) {

        Particle p = new Particle(y, m, ignoreFlags, 0);

        double xCenterOffset = p.getRX() - initialX;
        double yCenterOffset = p.getRY() - initialY;
        double zCenterOffset = p.getRZ() - initialZ;

        for (int i = 0; i < nodes.length; i++) {
            p.setIndex(i);

            double scale = 1e-6 * 1.5;

            nodes[i].setTranslateX(512 + (p.getRX()) * scale);
            nodes[i].setTranslateY(334 + (p.getRY()) * scale);
            nodes[i].setTranslateZ((p.getRZ() - zCenterOffset) * scale);

        }
    }

}

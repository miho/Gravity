package eu.mihosoft.fx.tutorials.milkglass;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
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

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main2(String[] args) throws IOException {
//        launch(args);

//        System.out.println("n-body");
        double minStep = 1e-6;
        double maxStep = 1e6;
        double absTol = 1e-10;
        double relTol = 1e-10;

        double t0 = 0;
        double tn = 60 * 60 * 24 * 365 * 10;

        double G = 6.672e-11;

        final int numBodies = 3;

        final double[] m = new double[numBodies];
//        m[0] = 5.98e24;
//        m[1] = 7.35e22;

        final double y[] = new double[numBodies * Particle.getStructSize()];

        final boolean[] ignoreFlag = new boolean[numBodies];

        Particle earth = new Particle(y, m, ignoreFlag, 0);
        earth.setMass(5.98e24);
        earth.setR(0, 0, 0);
        earth.setV(0, 0, 0);

        Particle moon = new Particle(y, m, ignoreFlag, 1);
        moon.setMass(7.35e22);
        moon.setR(3.84e8, 0, 0);
        moon.setV(0, 1023.2, 0);

        final Particle moon2 = new Particle(y, m, ignoreFlag, 2);
        moon2.setMass(7.35e22);
        moon2.setR(3.84e8 * 2, 0, 0);
        moon2.setV(0, 1023.2 / 1.2, 0);
//        moon2.setIgnored(false);

        FirstOrderIntegrator integrator = new DormandPrince853Integrator(minStep, maxStep, absTol, relTol);
//        FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(6e3);
        BufferedWriter w = new BufferedWriter(new FileWriter(new File("result.txt")));

        StepHandler stepHandler = new StepHandler() {
            @Override
            public void init(double t0, double[] y0, double t) {

                try {
                    earth.setY(y0);
                    moon.setY(y0);
                    moon2.setY(y0);
                    w.append(t0 / tn + " " + earth.getRX() + " " + +earth.getRY() + " " + moon.getRX() + " " + +moon.getRY() + " " + moon2.getRX() + " " + +moon2.getRY());
                    w.newLine();
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void handleStep(StepInterpolator interpolator, boolean isLast) {
                double t = interpolator.getCurrentTime();
                double[] y = interpolator.getInterpolatedState();

                try {
                    earth.setY(y);
                    moon.setY(y);
                    moon2.setY(y);
                    w.append(t / tn + " " + earth.getRX() + " " + +earth.getRY() + " " + moon.getRX() + " " + moon.getRY() + " " + moon2.getRX() + " " + +moon2.getRY());
                    w.newLine();

                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        integrator.addStepHandler(stepHandler);

        integrator.integrate(new FirstOrderDifferentialEquations() {

            @Override
            public int getDimension() {
                return numBodies * Particle.getStructSize();
            }

            @Override
            public void computeDerivatives(double t, double[] y, double[] yDot)
                    throws MaxCountExceededException, DimensionMismatchException {

                Particle pI = new Particle(y, m, ignoreFlag, 0);
                Particle pJ = new Particle(y, m, ignoreFlag, 1);

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

                        double magnitudeRJMinusRI = Math.sqrt(magnitudeRJMinusRISquare);

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
        }, t0, y, tn, y
        );

        w.close();
    }

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

                        double magnitudeRJMinusRI = Math.sqrt(magnitudeRJMinusRISquare);

                        if (magnitudeRJMinusRI < 1e8) {
                            pJ.setIgnored(true);
                            pI.setMass(pI.getMass() + pJ.getMass());
                            pane.getChildren().remove(nodes[j]);
//                            nodes[i].setRadius(pI.getMass()*0.5e-22);

                            nodes[i].setRadius(Math.min(pI.getMass() * 2e-22, 50));
                        }

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
        Scene scene = new Scene(rootPane, 1280, 800);

        setupODE(pane);

        // final stage setup
        primaryStage.setTitle("Milk Glass Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupODE(Pane pane) {

        int numBodies = 500;

        double[] y = new double[numBodies * Particle.getStructSize()];
        double[] m = new double[numBodies];
        boolean[] ignoreFlags = new boolean[numBodies];

        double G = 6.672e-11;

        Particle sun = new Particle(y, m, ignoreFlags, 0);
        sun.setMass(2.5e25);
        sun.setR(3.84e8 * 5 * 3, 3.84e8 * 5 * 1.5, 0);
        sun.setV(0, 0, 0);

        initialX = sun.getRX();
        initialY = sun.getRY();

//        Particle earth = new Particle(y, m, ignoreFlags, 0);
//        earth.setMass(5.98e24);
//        earth.setR(0, 0, 0);
//        earth.setV(0, 0, 0);
//
//        Particle moon = new Particle(y, m, ignoreFlags, 1);
//        moon.setMass(7.35e22);
//        moon.setR(3.84e8, 0, 0);
//        moon.setV(0, 1023.2, 0);
//
//        final Particle moon2 = new Particle(y, m, ignoreFlags, 2);
//        moon2.setMass(7.35e22);
//        moon2.setR(3.84e8 * 2, 0, 0);
//        moon2.setV(0, 1023.2 / 1.2, 0);
        nodes = new Circle[numBodies];

//        nodes[0] = new Circle(0);
//        nodes[0].setFill(colors[(int) (colors.length * Math.random())]);
//        nodes[0].setRadius(earth.getMass()*3e-24);
//        pane.getChildren().add(nodes[0]);
//        nodes[1] = new Circle(0);
//        nodes[1].setFill(colors[(int) (colors.length * Math.random())]);
//        nodes[1].setRadius(moon.getMass()*2e-22);
//        pane.getChildren().add(nodes[1]);
//        nodes[2] = new Circle(0);
//        nodes[2].setFill(colors[(int) (colors.length * Math.random())]);
//        nodes[2].setRadius(moon2.getMass()*2e-22);
//        pane.getChildren().add(nodes[2]);
        for (int i = 1; i < numBodies; i++) {
            Particle p = new Particle(y, m, ignoreFlags, i);
            p.setMass(1e22 + 8.35e22 * Math.random());
            p.setR(3.84e8 * Math.random() * 5 * 10 - 3.84e8 * 10 * 5/4.5, 3.84e8 * Math.random() * 5 * 10- 3.84e8 * 10 * 5/4.5, 0);
            p.setV(1023.2 * Math.random() * 0.7 - 1023.2 / 2, 1023.2 * Math.random() * 0.7 - 1023.2 / 2, 0);

//            if (i > numBodies/10) {
            p.setMass(1e22 + 7.35e22 * Math.random() * 0.1);
//            }
        }

        for (int i = 0; i < nodes.length; i++) {
            Particle p = new Particle(y, m, ignoreFlags, i);
            nodes[i] = new Circle(0);
            nodes[i].setFill(colors[(int) (colors.length * Math.random())]);
            if (i == 0) {
                nodes[i].setFill(Color.YELLOW);
                nodes[i].setEffect(new BoxBlur(10, 10, 3));
            }
            nodes[i].setRadius(Math.min(p.getMass() * 2e-22, 50));

            pane.getChildren().add(nodes[i]);
        }

//        for (int i = 0; i < nodes.length; i++) {
//            nodes[i] = new Circle(0);
//            nodes[i].setFill(colors[(int)(colors.length*Math.random())]);
//            nodes[i].setRadius(25);
//            
//            pane.getChildren().add(nodes[i]);
//        }
        initFrameListener(createODE(numBodies, y, m, ignoreFlags, G), y, m, ignoreFlags, 0.001);
    }

    public void initFrameListener(FirstOrderDifferentialEquations ode, double[] y, final double[] m, final boolean[] ignoreFlags, double dt) {

        FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(9e3);

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

//                System.out.println("frameDuration: " + frameDuration + ", t: " + t);
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

                    double tPlusDt = time + dt;

                    // integrate
                    try {
                        integrator.integrate(ode, time * 60 * 60 * 24 * 20 * 10, y, tPlusDt * 60 * 60 * 24 * 20 * 10, y);

                    } catch (Exception ex) {
                        ex.printStackTrace(System.err);
                    }
                    // remove integrated interval from remaining simulation time
                    remainingSimulationTime -= dt;

                    // update t
                    time = tPlusDt;
                }

                // interpolate state
                double alpha = remainingSimulationTime / dt;

                // set interpolated state
                for (int i = 0; i < y.length; i++) {
                    interpolatedY[i] = y[i] * alpha + yPrev[i] * (1.0 - alpha);
                }

                // update properties for visualization
                updateView(nodes, interpolatedY, m, ignoreFlags);
            }
        };

        // finally, start the framle listener
        frameListener.start();
    }

    public void updateView(Node[] nodes, double[] y, double[] m, boolean[] ignoreFlags) {

        Particle p = new Particle(y, m, ignoreFlags, 0);

        double xCenterOffset = p.getRX() - initialX;
        double yCenterOffset = p.getRY() - initialY;

        for (int i = 0; i < nodes.length; i++) {
            p.setIndex(i);

            nodes[i].setLayoutX((p.getRX() - xCenterOffset) * 0.06e-6+300);
            nodes[i].setLayoutY((p.getRY() - yCenterOffset) * 0.06e-6+300);
        }
    }

    /**
     * Spawns a node (circle).
     *
     * @param scene scene
     * @param container circle container
     */
    private void spawnNode(Scene scene, Pane container) {

        // create a circle node
        Circle node = new Circle(0);

        // circle shall be ignored by parent layout
        node.setManaged(false);

        // randomly pick one of the colors
        node.setFill(colors[(int) (Math.random() * colors.length)]);

        // choose a random position
        node.setCenterX(Math.random() * scene.getWidth());
        node.setCenterY(Math.random() * scene.getHeight());

        // add the container to the circle container
        container.getChildren().add(node);

        // create a timeline that fades the circle in and and out and also moves
        // it across the screen
        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(node.radiusProperty(), 0),
                        new KeyValue(node.centerXProperty(), node.getCenterX()),
                        new KeyValue(node.centerYProperty(), node.getCenterY()),
                        new KeyValue(node.opacityProperty(), 0)),
                new KeyFrame(
                        Duration.seconds(5 + Math.random() * 5),
                        new KeyValue(node.opacityProperty(), Math.random()),
                        new KeyValue(node.radiusProperty(), Math.random() * 20)),
                new KeyFrame(
                        Duration.seconds(10 + Math.random() * 20),
                        new KeyValue(node.radiusProperty(), 0),
                        new KeyValue(node.centerXProperty(), Math.random() * scene.getWidth()),
                        new KeyValue(node.centerYProperty(), Math.random() * scene.getHeight()),
                        new KeyValue(node.opacityProperty(), 0))
        );

        // timeline shall be executed once
        timeline.setCycleCount(1);

        // when we are done we spawn another node
        timeline.setOnFinished(evt -> {
            container.getChildren().remove(node);
            spawnNode(scene, container);
        });

        // finally, we play the timeline
        timeline.play();
    }
}

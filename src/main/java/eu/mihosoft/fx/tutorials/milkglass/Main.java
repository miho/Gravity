package eu.mihosoft.fx.tutorials.milkglass;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.nonstiff.RungeKuttaIntegrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

/**
 * Java FX Milk Glass Demo.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Main /*extends Application*/ {

    // Circle colors
    Color[] colors = {
        new Color(0.2, 0.5, 0.8, 1.0).saturate().brighter().brighter(),
        new Color(0.3, 0.2, 0.7, 1.0).saturate().brighter().brighter(),
        new Color(0.8, 0.3, 0.9, 1.0).saturate().brighter().brighter(),
        new Color(0.4, 0.3, 0.9, 1.0).saturate().brighter().brighter(),
        new Color(0.2, 0.5, 0.7, 1.0).saturate().brighter().brighter()};

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
//        launch(args);

//        System.out.println("n-body");
        double minStep = 1e-6;
        double maxStep = 1e6;
        double absTol = 1e-10;
        double relTol = 1e-10;

        double t0 = 0;
        double tn = 60 * 60 * 24 * 3650;

        double G = 6.672e-11;

        final int numBodies = 3;

        double[] m = new double[numBodies];
//        m[0] = 5.98e24;
//        m[1] = 7.35e22;

        double y[] = new double[numBodies * Particle.getStructSize()];

        boolean[] ignoreFlag = new boolean[numBodies];

        final Particle earth = new Particle(y, m, ignoreFlag, 0);
        earth.setMass(5.98e24);
        earth.setR(0, 0, 0);
        earth.setV(0, 0, 0);

        final Particle moon = new Particle(y, m, ignoreFlag, 1);
        moon.setMass(7.35e22);
        moon.setR(3.84e8, 0, 0);
        moon.setV(0, 1023.2, 0);
        
//        final Particle sun = new Particle(y, m, ignoreFlag, 2);
//        sun.setMass(5.98e24);
//        sun.setR(3.84e8+3000, 0, 0);
//        sun.setV(0, 1023.2/2, 0);

//        for(int i =0; i < numBodies; i++) {
//            Particle p = new Particle(y, m, ignoreFlag, i);
//            
//        }
//        y[0] = 0;
//        y[1] = 0;
//        y[2] = 0;
//        y[3] = 0;
//
//        y[4] = 3.84e8;
//        y[5] = 0;
//        y[6] = 0;
//        y[7] = 1023.2;
//        for (int i = 0; i < y.length; i += 4) {
////            System.out.println("i: " + i);
//
//            if (i >= 4) {
//                // r
//                y[i] = 4;
//                y[i + 1] = 5;
//            } else {
//                y[i] = 0;
//                y[i + 1] = 1;
//            }
//
//            if (i >= 4) {
//                // v
//                y[i + 2] = 0;
//                y[i + 3] = 1023.2;
//            }
////            // a
////            y[i + 4] = 0;
////            y[i + 5] = 0;
//        }
        FirstOrderIntegrator integrator = new DormandPrince853Integrator(minStep, maxStep, absTol, relTol);
//        FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(6e3);
        BufferedWriter w = new BufferedWriter(new FileWriter(new File("result.txt")));

        StepHandler stepHandler = new StepHandler() {
            @Override
            public void init(double t0, double[] y0, double t) {

                try {
                    earth.setY(y0);
                    moon.setY(y0);
                    w.append(t0 / tn + " " + earth.getRX() + " " + +earth.getRY() + " " + moon.getRX() + " " + +moon.getRY() + " " + sun.getRX() + " " + sun.getRY());
                    w.newLine();
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }

//                for (int i = 0; i < y.length; i += 6) {
//                    if (i % 6 == 0) {
//                        
//                        try {
//                            w.append(t0 + " " + y0[i] + " " + y0[i+1]);
//                        } catch (IOException ex) {
//                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                }
            }

            @Override
            public void handleStep(StepInterpolator interpolator, boolean isLast) {
                double t = interpolator.getCurrentTime();
                double[] y = interpolator.getInterpolatedState();

                try {
//                    System.out.println(" -->");
                    earth.setY(y);
                    moon.setY(y);
                    w.append(t / tn + " " + earth.getRX() + " " + +earth.getRY() + " " + moon.getRX() + " " + moon.getRY() + " " + sun.getRX() + " " + sun.getRY());
                    w.newLine();
//                    System.out.println(" <--");
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }

//                for (int i = 0; i < y.length; i += 6) {
//                    if (i % 6 == 0) {
//                        System.out.println(t + " " + y[i] + " " + y[i + 1]);
//                    }
//                }
            }
        };

        integrator.addStepHandler(stepHandler);

        integrator.integrate(new FirstOrderDifferentialEquations() {

            @Override
            public int getDimension() {
                return numBodies * Particle.getStructSize();
            }

            @Override
            public void computeDerivatives(double t, double[] y, double[] yDot) throws MaxCountExceededException, DimensionMismatchException {

                // d2 rI / dt^2 = GmJ*(rJ-rI)/|rJ-rI|^3
                // http://www.physics.buffalo.edu/phy410-505/topic5/
                // http://physics.princeton.edu/~fpretori/Nbody/intro.htm
                // outer sum
//                for (int i = 0; i < getDimension(); i += 4) {
//
//                    double fXI = 0;
//                    double fYI = 0;
//
//                    // inner sum
//                    for (int j = 0; j < getDimension(); j += 4) {
//
//                        if (i == j) {
//                            continue;
//                        }
//
//                        double rXI = y[i];
//                        double rYI = y[i + 1];
//
//                        double rXJ = y[j];
//                        double rYJ = y[j + 1];
//
//                        double rJMinusRIX = rXJ - rXI;
//                        double rJMinusRIY = rYJ - rYI;
//
//                        double magnitudeRJMinusRISquare = rJMinusRIX * rJMinusRIX + rJMinusRIY * rJMinusRIY;
//                        double magnitudeRJMinusRI = Math.sqrt(magnitudeRJMinusRISquare);
//
//                        fXI += m[j / 4] * rJMinusRIX / (magnitudeRJMinusRISquare * magnitudeRJMinusRI);
//                        fYI += m[j / 4] * rJMinusRIY / (magnitudeRJMinusRISquare * magnitudeRJMinusRI);
//                    }
//
//                    fXI *= G * m[i / 4];
//                    fYI *= G * m[i / 4];
//
//                    int offset = 0;
//
//                    // write v into new r
//                    yDot[i + offset] = y[i + offset + 2];
//                    yDot[i + offset + 1] = y[i + offset + 2 + 1];
//
//                    offset = 2;
//
//                    // a into new v
//                    yDot[i + offset] = fXI;
//                    yDot[i + offset + 1] = fYI;
//
////                    offset = 4;
////
////                    yDot[i + offset] = y[i + offset];
////                    yDot[i + offset + 1] = y[i + offset + 1];
//                }
                
                
//                double diffX = y[0] - y[4];
//                double diffY = y[1] - y[4 + 1];
//
//                double magnitudeSquare = diffX * diffX + diffY * diffY;
//
//                double aX = diffX * (G * m[0] * m[1]) / (magnitudeSquare * Math.sqrt(magnitudeSquare));
//                double aY = diffY * (G * m[0] * m[1]) / (magnitudeSquare * Math.sqrt(magnitudeSquare));
//
//                // write earth v, i.e. r'
//                yDot[0] = y[2];
//                yDot[0 + 1] = y[2 + 1];
//
//                // write moon v, i.e., r'
//                yDot[4] = y[4 + 2];
//                yDot[4 + 1] = y[4 + 2 + 1];
//
//                double aEX = aX / (-m[0]);
//                double aEY = aY / (-m[0]);
//
//                double aMX = aX / (m[1]);
//                double aMY = aY / (m[1]);
//
//                // write earth a, i.e. v'
//               yDot[2] = aEX;
//                yDot[2 + 1] = aEY;
//
//                // write moon a, i.e. v'
//                yDot[6] = aMX;
//                yDot[6 + 1] = aMY;
                
                
                earth.setY(y);
                moon.setY(y);

                double diffX = earth.getRX() - moon.getRX();
                double diffY = earth.getRY() - moon.getRY();
                double diffZ = earth.getRZ() - moon.getRZ();

                double magnitudeSquare = diffX * diffX + diffY * diffY + diffZ * diffZ;

                double aX = diffX * (G * earth.getMass() * moon.getMass()) / (magnitudeSquare * Math.sqrt(magnitudeSquare));
                double aY = diffY * (G * earth.getMass() * moon.getMass()) / (magnitudeSquare * Math.sqrt(magnitudeSquare));
                double aZ = diffZ * (G * earth.getMass() * moon.getMass()) / (magnitudeSquare * Math.sqrt(magnitudeSquare));

                double aEX = aX / (-earth.getMass());
                double aEY = aY / (-earth.getMass());
                double aEZ = aZ / (-earth.getMass());

                double aMX = aX / (moon.getMass());
                double aMY = aY / (moon.getMass());
                double aMZ = aZ / (moon.getMass());

                earth.setRDerivativeTo(yDot);
                earth.setVDerivativeTo(yDot, aEX, aEY, aEZ);

                moon.setRDerivativeTo(yDot);
                moon.setVDerivativeTo(yDot, aMX, aMY, aMZ);

            }
        }, t0, y, tn, y
        );

        w.close();

    }

//    @Override
//    public void start(Stage primaryStage) {
//
//        // toplevel pane
//        StackPane rootPane = new StackPane();
//
//        // circle container
//        Pane container = new Pane();
//
//        // circle container is a child of the root pane
//        rootPane.getChildren().add(container);
//
//        // background style for the container
//        container.setStyle("-fx-background-color: rgb(25,40,80)");
//
//        // create a scene with size 1280x800
//        Scene scene = new Scene(rootPane, 1280, 800);
//
//        // number of nodes that shall be spawned
//        int spawnNodes = 800;
//
//        // spawn the nodes (circles)
//        for (int i = 0; i < spawnNodes; i++) {
//            spawnNode(scene, container);
//        }
//
//        // create the milk glass pane
//        MilkGlassPane milkGlassPane = new MilkGlassPane(container);
//        milkGlassPane.setMaxSize(600, 400);
//        
//        // add the milk glass pane to the root pane
//        rootPane.getChildren().add(milkGlassPane);
//
//        // final stage setup
//        primaryStage.setTitle("Milk Glass Demo");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
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

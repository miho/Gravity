/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.fx.tutorials.gravity;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class SolarSystem {

    /**
     * Particle objects (used for simulation and visualizations).
     */
    private final List<Particle> particles = new ArrayList<>();
    /**
     * UI nodes.
     */
    private Node[] nodes;
    /**
     * Node transformations for planetary rotations.
     */
    private Rotate[] rotations;

    /**
     * Time stamp for measuring frame duration.
     */
    private long lastTimeStamp;

    /**
     * Accumulated simulation time.
     */
    private double remainingSimulationTime;

    /**
     * Current time.
     */
    private double time = 0;

    /**
     * UI scale (used for transforming the solar system).
     */
    private double scale = 1.0;

    /**
     * Time scale used for accelerating and deaccelerating simulation.
     */
    private double timeScale = 1.0;

    /**
     * UI frame listener.
     */
    private AnimationTimer frameListener;

    /**
     * Returns this solar system as JavaFX node.
     *
     * @return
     */
    public Group toJavaFX() {

        Group root = new Group();

        if (nodes == null) {
            return root;
        }

        root.getChildren().addAll(nodes);

        return root;
    }

    /**
     * Adds a particle to this simulation system.
     *
     * @param p particle that shall be added
     */
    public void addParticle(Particle p) {
        particles.add(p);
    }

    /**
     * Stops the current simulation. Does nothing if no simulation is running.
     */
    public void stopSimulation() {
        if (frameListener != null) {
            frameListener.stop();
            frameListener = null;
        }
    }

    /**
     * Runs the simulation.
     */
    public void runSimulation() {

        // stop previous running simulation
        // does nothing if no simulation is running
        stopSimulation();

        // state vector
        int numBodies = particles.size();
        double[] y = new double[numBodies * ODEParticle.getStructSize()];
        double[] m = new double[numBodies];
        boolean[] ignoreFlags = new boolean[numBodies];

        // ui nodes
        nodes = new Node[numBodies];
        rotations = new Rotate[numBodies];

        // gravitational constant
        // see https://en.wikipedia.org/wiki/Gravitational_constant
        double G = 6.672e-11 * 0.000000001; // scaled G

        // initialize particles
        for (int i = 0; i < numBodies; i++) {
            Particle p = particles.get(i);
            p.apply(i, y, m);
            nodes[i] = p.toJFXNode();
            rotations[i] = new Rotate(0, new Point3D(0, 1, 0));
            nodes[i].getTransforms().add(rotations[i]);
        }

        // setup ODE system
        FirstOrderDifferentialEquations odeEqSys
                = createODE(numBodies, y, m, ignoreFlags, G);

        // start frame listener
        initFrameListener(odeEqSys, y, m, ignoreFlags, 0.001);

    }

    /**
     * Creates a system of odes that describe the physical behavior of the solar
     * system.
     *
     * @param numBodies number of bodies to simulate
     * @param y state vector (location and velocity)
     * @param m particle masses
     * @param ignoreFlags ignore flags (for simulating collisions)
     * @param G gravitational constant
     * @return system of first order differential equations
     */
    private FirstOrderDifferentialEquations createODE(
            final int numBodies, final double[] y, final double[] m,
            final boolean[] ignoreFlags, final double G) {

        return new FirstOrderDifferentialEquations() {

            @Override
            public int getDimension() {
                return numBodies * ODEParticle.getStructSize();
            }

            @Override
            public void computeDerivatives(double t, double[] y, double[] yDot)
                    throws MaxCountExceededException, DimensionMismatchException {

                ODEParticle pI = new ODEParticle(y, m, ignoreFlags, 0);
                ODEParticle pJ = new ODEParticle(y, m, ignoreFlags, 1);

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

    /**
     * Initializes the ui frame listener.
     *
     * @param ode ode to solve
     * @param y state vector (location and velocity)
     * @param m particle masses
     * @param ignoreFlags ignore flags (used for collisions)
     * @param dt time step size
     */
    private void initFrameListener(
            FirstOrderDifferentialEquations ode,
            double[] y,
            final double[] m,
            final boolean[] ignoreFlags, double dt) {

        // local time scale (sec per h * h per day * days per year)
        final double localTimeScale = 3600 * 24 * 365;

        // integrator
        FirstOrderIntegrator integrator
                = new ClassicalRungeKuttaIntegrator(dt * localTimeScale);

        double[] yPrev = new double[y.length];
        double[] interpolatedY = new double[y.length];

        // create frame listener 
        frameListener = new AnimationTimer() {

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

                    double scaledT = time * localTimeScale;
                    double scaledDT = dt * localTimeScale * timeScale;

                    // integrate one step
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
                    interpolatedY[i]
                            = y[i] * alpha + yPrev[i] * (1.0 - alpha);
                }

                // update properties for visualization
                updateView(nodes, interpolatedY, m, ignoreFlags);
            }
        };

        // finally, start the framle listener
        frameListener.start();
    }

    /**
     * Updates the visualization of this solar system.
     *
     * @param nodes ui nodes
     * @param y state vector
     * @param m particle masses
     * @param ignoreFlags ignore flags (used for collisions)
     */
    public void updateView(
            Node[] nodes, double[] y, double[] m, boolean[] ignoreFlags) {

        ODEParticle p = new ODEParticle(y, m, ignoreFlags, 0);

        double initX = 0;
        double initY = 0;
        double initZ = 0;

//        initX = p.getRX();
//        initY = p.getRY();
//        initZ = p.getRZ();
        for (int i = 0; i < nodes.length; i++) {
            p.setIndex(i);

            double s = 1e-6 * getScale() * 0.1;

            nodes[i].setScaleX(getScale() * 0.8);
            nodes[i].setScaleY(getScale() * 0.8);
            nodes[i].setScaleZ(getScale() * 0.8);

//            if (i != 0) {
            nodes[i].setTranslateX((p.getRX() - initX) * s);
            nodes[i].setTranslateY((p.getRY() - initY) * s);
            nodes[i].setTranslateZ((p.getRZ() - initZ) * s);
//            }

            // rotation is only estimated (no simulation involved)
            rotations[i].setAngle(rotations[i].getAngle()
                    - 1e25 / p.getMass() * 0.8);
        }
    }

    /**
     * @return the current scale
     */
    public double getScale() {
        return scale;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(double scale) {
        this.scale = scale;
    }

    /**
     * @return the current time scale
     */
    public double getTimeScale() {
        return timeScale;
    }

    /**
     * @param timeScale the time scale to set
     */
    public void setTimeScale(double timeScale) {
        this.timeScale = timeScale;
    }
}

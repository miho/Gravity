/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.fx.tutorials.gravity.twod;

import eu.mihosoft.fx.tutorials.gravity.*;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Particle {

    private double[] y;
    private double[] masses;
    private boolean[] ignoreFlags;

    private int index;

    private final static int STRUCT_SIZE = 6;

    private static final int RX_OFFSET = 0;
    private static final int RY_OFFSET = 1;
    private static final int RZ_OFFSET = 2;
    private static final int VX_OFFSET = 3;
    private static final int VY_OFFSET = 4;
    private static final int VZ_OFFSET = 5;

    public Particle() {
    }

    public static int getStructSize() {
        return STRUCT_SIZE;
    }

    public Particle(
            double[] y,
            double[] masses,
            boolean[] ignoreFlags,
            int index) {
        this.y = y;
        this.masses = masses;
        this.ignoreFlags = ignoreFlags;
        this.index = index;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setR(double rx, double ry, double rz) {
        setRX(rx);
        setRY(ry);
        setRZ(rz);
    }

    public void setV(double vx, double vy, double vz) {
        setVX(vx);
        setVY(vy);
        setVZ(vz);
    }

    public void setRDerivativeTo(double[] yDot) {
        yDot[index * STRUCT_SIZE + RX_OFFSET] = getVX();
        yDot[index * STRUCT_SIZE + RY_OFFSET] = getVY();
        yDot[index * STRUCT_SIZE + RZ_OFFSET] = getVZ();
    }

    public void setVDerivativeTo(
            double[] yDot,
            double ax, double ay, double az) {

        yDot[index * STRUCT_SIZE + VX_OFFSET] = ax;
        yDot[index * STRUCT_SIZE + VY_OFFSET] = ay;
        yDot[index * STRUCT_SIZE + VZ_OFFSET] = az;
    }

    public double getRX() {
        return y[index * STRUCT_SIZE + RX_OFFSET];
    }

    public double getRY() {
        return y[index * STRUCT_SIZE + RY_OFFSET];
    }

    public double getRZ() {
        return y[index * STRUCT_SIZE + RZ_OFFSET];
    }

    public double getVX() {
        return y[index * STRUCT_SIZE + VX_OFFSET];
    }

    public double getVY() {
        return y[index * STRUCT_SIZE + VY_OFFSET];
    }

    public double getVZ() {
        return y[index * STRUCT_SIZE + VZ_OFFSET];
    }

    public double getMass() {
        return masses[index];
    }

    public void setRX(double rx) {
        y[index * STRUCT_SIZE + RX_OFFSET] = rx;
    }

    public void setRY(double ry) {
        y[index * STRUCT_SIZE + RY_OFFSET] = ry;
    }

    public void setRZ(double rz) {
        y[index * STRUCT_SIZE + RZ_OFFSET] = rz;
    }

    public void setVX(double vx) {
        y[index * STRUCT_SIZE + VX_OFFSET] = vx;
    }

    public void setVY(double vy) {
        y[index * STRUCT_SIZE + VY_OFFSET] = vy;
    }

    public void setVZ(double vz) {
        y[index * STRUCT_SIZE + VZ_OFFSET] = vz;
    }

    public void setMass(double mass) {
        masses[index] = mass;
    }

    public boolean isIgnored() {
        return ignoreFlags[index];
    }

    public void setIgnored(boolean state) {
        ignoreFlags[index] = state;
    }

    /**
     * @return the y
     */
    public double[] getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double[] y) {
        this.y = y;
    }

    /**
     * @return the masses
     */
    public double[] getMasses() {
        return masses;
    }

    /**
     * @param masses the masses to set
     */
    public void setMasses(double[] masses) {
        this.masses = masses;
    }

    /**
     * @return the ignoreFlags
     */
    public boolean[] getIgnoreFlags() {
        return ignoreFlags;
    }

    /**
     * @param ignoreFlags the ignoreFlags to set
     */
    public void setIgnoreFlags(boolean[] ignoreFlags) {
        this.ignoreFlags = ignoreFlags;
    }

}

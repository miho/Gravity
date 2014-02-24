/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.fx.tutorials.milkglass;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Particle {

    private double[] y;
    private double[] masses;
    private boolean[] ignoreFlags;

    private int index;

    private final static int size = 6;

    private static final int rxOffset = 0;
    private static final int ryOffset = 1;
    private static final int rzOffset = 2;
    private static final int vxOffset = 3;
    private static final int vyOffset = 4;
    private static final int vzOffset = 5;

    public Particle() {
    }

    public static int getSize() {
        return size;
    }

    public Particle(double[] y, double[] masses, boolean[] ignoreFlags, int index) {
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
        yDot[index * size + rxOffset] = getVX();
        yDot[index * size + ryOffset] = getVY();
        yDot[index * size + rzOffset] = getVZ();
    }

    public void setVDerivativeTo(double[] yDot, double ax, double ay, double az) {
        yDot[index * size + vxOffset] = ax;
        yDot[index * size + vyOffset] = ay;
        yDot[index * size + vxOffset] = az;
    }

    public double getRX() {
        System.out.println("get-value: " + index + " = " + y[index * size + rxOffset]);
        return y[index * size + rxOffset];
    }

    public double getRY() {
        return y[index * size + ryOffset];
    }

    public double getRZ() {
        return y[index * size + rzOffset];
    }

    public double getVX() {
        return y[index * size + vxOffset];
    }

    public double getVY() {
        return y[index * size + vyOffset];
    }

    public double getVZ() {
        return y[index * size + vzOffset];
    }

    public double getMass() {
        return masses[index];
    }

    public void setRX(double rx) {
        y[index * size + rxOffset] = rx;
    }

    public void setRY(double ry) {
        System.out.println("set-value: " + ry);
        y[index * size + ryOffset] = ry;
    }

    public void setRZ(double rz) {
        y[index * size + rzOffset] = rz;
    }

    public void setVX(double vx) {
        y[index * size + vxOffset] = vx;
    }

    public void setVY(double vy) {
        y[index * size + vyOffset] = vy;
    }

    public void setVZ(double vz) {
        y[index * size + vzOffset] = vz;
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

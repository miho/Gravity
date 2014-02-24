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

    private final static int structSize = 6;

    private static final int rxOffset = 0;
    private static final int ryOffset = 1;
    private static final int rzOffset = 2;
    private static final int vxOffset = 3;
    private static final int vyOffset = 4;
    private static final int vzOffset = 5;

    public Particle() {
    }

    public static int getStructSize() {
        return structSize;
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
        yDot[getIndex() * structSize + rxOffset] = getVX();
        yDot[getIndex() * structSize + ryOffset] = getVY();
        yDot[getIndex() * structSize + rzOffset] = getVZ();
    }

    public void setVDerivativeTo(double[] yDot, double ax, double ay, double az) {
        
        yDot[getIndex() * structSize + vxOffset] = ax;
        yDot[getIndex() * structSize + vyOffset] = ay;
        yDot[getIndex() * structSize + vzOffset] = az;
    }

    public double getRX() {
        return y[getIndex() * structSize + rxOffset];
    }

    public double getRY() {
        return y[getIndex() * structSize + ryOffset];
    }

    public double getRZ() {
        return y[getIndex() * structSize + rzOffset];
    }

    public double getVX() {
        return y[getIndex() * structSize + vxOffset];
    }

    public double getVY() {
        return y[getIndex() * structSize + vyOffset];
    }

    public double getVZ() {
        return y[getIndex() * structSize + vzOffset];
    }

    public double getMass() {
        return masses[getIndex()];
    }

    public void setRX(double rx) {
        y[getIndex() * structSize + rxOffset] = rx;
    }

    public void setRY(double ry) {
        y[getIndex() * structSize + ryOffset] = ry;
    }

    public void setRZ(double rz) {
        y[getIndex() * structSize + rzOffset] = rz;
    }

    public void setVX(double vx) {
        y[getIndex() * structSize + vxOffset] = vx;
    }

    public void setVY(double vy) {
        y[getIndex() * structSize + vyOffset] = vy;
    }

    public void setVZ(double vz) {
        y[getIndex() * structSize + vzOffset] = vz;
    }

    public void setMass(double mass) {
        masses[getIndex()] = mass;
    }

    public boolean isIgnored() {
        return ignoreFlags[getIndex()];
    }

    public void setIgnored(boolean state) {
        ignoreFlags[getIndex()] = state;
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

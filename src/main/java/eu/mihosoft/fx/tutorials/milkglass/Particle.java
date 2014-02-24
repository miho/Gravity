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
    
    private static final int rxOffset = 0;
    private static final int ryOffset = 1;
    private static final int rzOffset = 2;
    private static final int vxOffset = 3;
    private static final int vyOffset = 4;
    private static final int vzOffset = 5;

    public Particle() {
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

    public double getRX() {
        return y[index + rxOffset];
    }

    public double getRY() {
        return y[index + ryOffset];
    }

    public double getRZ() {
        return y[index + rzOffset];
    }

    public double getVX() {
        return y[index + vxOffset];
    }

    public double getVY() {
        return y[index + vyOffset];
    }

    public double getVZ() {
        return y[index + vzOffset];
    }

    public double getMass() {
        return masses[index];
    }

    public void setRX(double rx) {
        y[index + rxOffset] = rx;
    }

    public void setRY(double ry) {
        y[index + ryOffset] = ry;
    }

    public void setRZ(double rz) {
        y[index + rzOffset] = rz;
    }
    
    public void setVX(double vx) {
        y[index + vxOffset] = vx;
    }

    public void setVY(double vy) {
        y[index + vyOffset] = vy;
    }

    public void setVZ(double vz) {
        y[index + vzOffset] = vz;
    }
    
    public void setMass(double mass) {
        masses[index] = mass;
    }

    /**
     * @return the rzOffset
     */
    public int getRzOffset() {
        return rzOffset;
    }
    
    public boolean isIgnored() {
        return ignoreFlags[index];
    }
    
    public void setIgnored(boolean state) {
        ignoreFlags[index] = state;
    }

}

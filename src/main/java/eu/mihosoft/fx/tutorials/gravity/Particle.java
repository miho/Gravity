/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.fx.tutorials.gravity;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Particle {

    private double rx;
    private double ry;
    private double rz;
    private double vx;
    private double vy;
    private double vz;
    private double m;

    private Image texture;
    private Color color = Color.WHITE;
    private double radius;

    private final ODEParticle p = new ODEParticle();
    private Image illuminationMap;
    private Image bumpMap;

    void apply(int i, double[] y, double[] m) {
        p.setIndex(i);
        p.setY(y);
        p.setMasses(m);
        p.setR(rx, ry, rz);
        p.setV(vx, vy, vz);
        p.setMass(this.m);
    }

    public void setR(double rx, double ry, double rz) {
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
    }

    public void setV(double vx, double vy, double vz) {
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
    }

    /**
     * @return the rx
     */
    public double getRx() {
        return rx;
    }

    /**
     * @param rx the rx to set
     */
    public void setRx(double rx) {
        this.rx = rx;
    }

    /**
     * @return the ry
     */
    public double getRy() {
        return ry;
    }

    /**
     * @param ry the ry to set
     */
    public void setRy(double ry) {
        this.ry = ry;
    }

    /**
     * @return the vx
     */
    public double getVx() {
        return vx;
    }

    /**
     * @param vx the vx to set
     */
    public void setVx(double vx) {
        this.vx = vx;
    }

    /**
     * @return the vy
     */
    public double getVy() {
        return vy;
    }

    /**
     * @param vy the vy to set
     */
    public void setVy(double vy) {
        this.vy = vy;
    }

    /**
     * @return the m
     */
    public double getM() {
        return m;
    }

    /**
     * @param m the m to set
     */
    public void setMass(double m) {
        this.m = m;
    }

    public Node toJFXNode() {
        Sphere sphere = new Sphere(getRadius());

        PhongMaterial mat = new PhongMaterial(getColor());

        if (getTexture() != null) {
            mat.setDiffuseMap(getTexture());
        }

        if (getBumpMap() != null) {
            mat.setBumpMap(getBumpMap());
        }

        if (getIlluminationMap() != null) {
            mat.setSelfIlluminationMap(getIlluminationMap());
        }

        sphere.setMaterial(mat);
        
        sphere.setRotationAxis(new Point3D(1, 0, 0));
        sphere.setRotate(90);

        sphere.setDrawMode(DrawMode.FILL);
        sphere.setCullFace(CullFace.NONE);

        return sphere;

    }

    /**
     * @return the radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * @param radius the radius to set
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return the texture
     */
    public Image getTexture() {
        return texture;
    }

    /**
     * @param texture the texture to set
     */
    public void setTexture(Image texture) {
        this.texture = texture;
    }

    public void setIlluminationMap(Image texture) {
        this.illuminationMap = texture;
    }

    public Image getIlluminationMap() {
        return illuminationMap;
    }

    public void setBumpMap(Image texture) {
        this.bumpMap = texture;
    }

    public Image getBumpMap() {
        return bumpMap;
    }
}

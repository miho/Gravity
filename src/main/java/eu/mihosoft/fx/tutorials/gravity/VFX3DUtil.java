package eu.mihosoft.fx.tutorials.gravity;

import javafx.event.EventHandler;
import javafx.scene.Node;

import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * Utility class that allows to visualize meshes created with null {@link MathUtil#evaluateFunction(
 *   eu.mihosoft.vrl.javaone2013.math.Function2D,
 *   int, int, float, float, float, double, double, double, double)
 * }.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VFX3DUtil {

    private VFX3DUtil() {
        throw new AssertionError("don't instanciate me!");
    }

    /**
     * Adds rotation behavior to the specified node.
     *
     * @param n node
     * @param eventReceiver receiver of the event
     * @param btn mouse button that shall be used for this behavior
     */
    public static void addMouseBehavior(
            Node n, Scene eventReceiver, MouseButton btn) {
        eventReceiver.addEventHandler(MouseEvent.ANY,
                new MouseBehaviorImpl1(n, btn));
    }

    /**
     * Adds rotation behavior to the specified node.
     *
     * @param n node
     * @param eventReceiver receiver of the event
     * @param btn mouse button that shall be used for this behavior
     */
    public static void addMouseBehavior(
            Node n, Node eventReceiver, MouseButton btn) {
        eventReceiver.addEventHandler(MouseEvent.ANY,
                new MouseBehaviorImpl1(n, btn));
    }

    
    /**
     * Adds a zoom behavior (zoom by scroll) to the specified node.
     * @param n node
     * @param eventReceiver receiver of the event
     */
    public static void addZoomBehavior(Node n, Node eventReceiver) {
        eventReceiver.addEventHandler(ScrollEvent.ANY,
                new ZoomBehavior(n));
    }
}

class ZoomBehavior implements EventHandler<ScrollEvent> {

    private final Translate translateZ = new Translate();

    public ZoomBehavior(Node n) {
        n.getTransforms().add(translateZ);
    }

    @Override
    public void handle(ScrollEvent event) {
        translateZ.setZ(translateZ.getZ() + event.getDeltaY());
    }

}

// rotation behavior implementation
class MouseBehaviorImpl1 implements EventHandler<MouseEvent> {

    private double anchorAngleX;
    private double anchorAngleY;
    private double anchorX;
    private double anchorY;
    private final Rotate rotateX = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
    private MouseButton btn;

    public MouseBehaviorImpl1(Node n, MouseButton btn) {
        n.getTransforms().addAll(rotateX, rotateY);
        this.btn = btn;

        if (btn == null) {
            this.btn = MouseButton.PRIMARY;
        }
    }

    @Override
    public void handle(MouseEvent t) {
        if (!btn.equals(t.getButton())) {
            return;
        }
        
        t.consume();

        if (MouseEvent.MOUSE_PRESSED.equals(t.getEventType())) {
            anchorX = t.getSceneX();
            anchorY = t.getSceneY();
            anchorAngleX = rotateX.getAngle();
            anchorAngleY = rotateY.getAngle();
            t.consume();
        } else if (MouseEvent.MOUSE_DRAGGED.equals(t.getEventType())) {
            rotateY.setAngle(anchorAngleY + (anchorX - t.getSceneX()) * 0.7);
            rotateX.setAngle(anchorAngleX - (anchorY - t.getSceneY()) * 0.7);
        }

    }
}

package oogasalad.view.renderer.componentRenderer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.Transform;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.framework.junit5.ApplicationTest;

/**
 * Abstract base test class for ComponentRenderer tests.
 */
public abstract class AbstractComponentRendererTest extends ApplicationTest {

  protected Pane pane;
  protected Canvas canvas;
  protected GraphicsContext graphicsContext;
  protected GameObject parent;


  @BeforeEach
  void baseSetup() {
    pane = new Pane();
    canvas = new Canvas(800, 600); // Real canvas
    graphicsContext = canvas.getGraphicsContext2D();

    parent = new GameObject("parent");
    Transform transform = parent.addComponent(Transform.class);
    transform.setX(100);
    transform.setY(150);
    transform.setScaleX(50);
    transform.setScaleY(80);

    customSetup();
  }

  /**
   * Subclasses can optionally override to do extra setup (e.g., set text, image path, etc.).
   */
  protected void customSetup() {
    // Default no-op
  }
}

package oogasalad.view.renderer.componentRenderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import oogasalad.model.engine.component.Camera;
import oogasalad.model.engine.component.Transform;

/**
 * The renderer to render a Camera.
 */
public class CameraRenderer extends ComponentRenderer<Camera> {

  @Override
  protected void onEditorRender(Camera component, Pane graphicPane, double cameraX,
      double cameraY) {

    Transform transform = component.getParent().getComponent(Transform.class);

    Rectangle rect = new Rectangle(
        transform.getX() - cameraX,
        transform.getY() - cameraY,
        transform.getScaleX(),
        transform.getScaleY());

    rect.setFill(Color.rgb(255, 182, 193, 0.3));
    graphicPane.getChildren().add(rect);

  }

  @Override
  protected void onCanvasRender(Camera component, GraphicsContext graphicsContext, double cameraX,
      double cameraY) {
    // DO NOTHING
  }
}

package oogasalad.view.renderer.componentRenderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import oogasalad.model.engine.component.Transform;

/**
 * The renderer to render a Transform.
 */
public class TransformRenderer extends ComponentRenderer<Transform> {

  @Override
  protected void onEditorRender(Transform component, Pane graphicPane, double cameraX,
      double cameraY) {

    double scaleFactor = 1;
    if (graphicPane.getParent() instanceof Pane holder) {
      scaleFactor = holder.getScaleX();
    }

    Rectangle rect = new Rectangle(
        component.getX() - cameraX,
        component.getY() - cameraY,
        component.getScaleX(),
        component.getScaleY());
    rect.setFill(Color.TRANSPARENT);
    rect.setStroke(Color.GRAY);
    rect.setStrokeWidth(2 / scaleFactor);
    graphicPane.getChildren().add(rect);
  }

  @Override
  protected void onCanvasRender(Transform component, GraphicsContext graphicsContext,
      double cameraX, double cameraY) {
    // DO NOTHING
  }
}

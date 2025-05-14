package oogasalad.view.renderer.sceneRenderer;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.Transform;
import oogasalad.view.renderer.componentRenderer.ComponentRenderer;
import oogasalad.view.scene.builder.ObjectDragger;

/**
 * BuilderSceneRenderer constructs a node-based representation of a GameScene into a JavaFX Pane,
 * suitable for interactive editing and display.
 */
public class BuilderSceneRenderer extends SceneRenderer<Pane> {

  private static final double BASE_HANDLE_SIZE = ObjectDragger.HANDLE_SIZE;

  /**
   * Constructor for BuilderSceneRenderer.
   *
   * @param renderContext Pane within which objects will be rendered
   */
  public BuilderSceneRenderer(Pane renderContext) {
    super(renderContext);
  }

  @Override
  public void clearRenderContext() {
    getRenderContext().getChildren().clear();
  }

  @Override
  protected void renderGameObject(GameObject obj) {
    // Render transform if contains no other components
    int oneComponent = 1;
    if (obj.getRenderableComponents().size() == oneComponent
        && obj.getComponent(Transform.class) != null) {
      ComponentRenderer.render(obj.getComponent(Transform.class), getRenderContext(), getCameraX(),
          getCameraY());
    }

    // Render other components
    else {
      obj.getRenderableComponents().stream()
          .filter(component -> !(component instanceof Transform))
          .forEach(
              component -> ComponentRenderer.render(component, getRenderContext(), getCameraX(),
                  getCameraY()));
    }
  }

  /**
   * Render an object that is selected with selection UI overlay.
   *
   * @param obj the selected object.
   */
  public void renderSelectionOverlay(GameObject obj) {
    if (obj == null) {
      return;
    }

    Transform t = obj.getComponent(Transform.class);
    double x = t.getX() - getCameraX();
    double y = t.getY() - getCameraY();
    double w = t.getScaleX();
    double h = t.getScaleY();
    double scaleFactor = 1;
    if (getRenderContext().getParent() instanceof Pane holder) {
      scaleFactor = holder.getScaleX();
    }

    Rectangle outline = new Rectangle(x, y, w, h);
    outline.setStroke(Color.LIGHTBLUE);
    outline.setStrokeWidth(2 / scaleFactor);
    outline.setFill(Color.TRANSPARENT);
    getRenderContext().getChildren().add(outline);

    double handleSize = BASE_HANDLE_SIZE / scaleFactor;

    double[][] positions = getPositionsForSelectionOverlay(x, y, w, h);
    for (double[] p : positions) {
      Rectangle handle = new Rectangle(p[0] - handleSize / 2, p[1] - handleSize / 2, handleSize,
          handleSize);
      handle.setFill(Color.LIGHTBLUE);
      getRenderContext().getChildren().add(handle);
    }
  }


  private static double[][] getPositionsForSelectionOverlay(double x, double y, double w,
      double h) {
    return new double[][]{{x, y}, {x + w / 2, y}, {x + w, y},
        {x + w, y + h / 2}, {x + w, y + h},
        {x + w / 2, y + h}, {x, y + h}, {x, y + h / 2}};
  }
}

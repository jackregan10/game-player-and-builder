package oogasalad.view.renderer.sceneRenderer;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.Renderable;
import oogasalad.view.renderer.componentRenderer.ComponentRenderer;


/**
 * The GameSceneRenderer class is responsible for rendering game objects and their components
 */

public class GameSceneRenderer extends SceneRenderer<GraphicsContext> {

  /**
   * Constructor for GameSceneRenderer
   *
   * @param renderContext GraphicsContext within which objects will be rendered
   */
  public GameSceneRenderer(GraphicsContext renderContext) {
    super(renderContext);
  }

  /**
   * Remove all displayed objects in the current GraphicsContext.
   */
  @Override
  public void clearRenderContext() {
    GraphicsContext gc = getRenderContext();

    gc.getCanvas().setWidth(getCameraScaleX());
    gc.getCanvas().setHeight(getCameraScaleY());

    gc.setFill(Color.LIGHTGRAY);
    gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
  }

  @Override
  protected void renderGameObject(GameObject obj) {
    for (Renderable component : obj.getRenderableComponents()) {
      ComponentRenderer.render(component, getRenderContext(), getCameraX(), getCameraY());
    }
  }
}

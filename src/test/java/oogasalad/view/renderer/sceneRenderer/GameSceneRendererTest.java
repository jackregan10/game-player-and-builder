package oogasalad.view.renderer.sceneRenderer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class GameSceneRendererTest extends AbstractSceneRendererTest<GraphicsContext, GameSceneRenderer> {

  @Override
  protected GraphicsContext createRenderContext() {
    Canvas canvas = new Canvas(800, 600);
    return canvas.getGraphicsContext2D();
  }

  @Override
  protected GameSceneRenderer createSceneRenderer(GraphicsContext renderContext) {
    return new GameSceneRenderer(renderContext);
  }
}
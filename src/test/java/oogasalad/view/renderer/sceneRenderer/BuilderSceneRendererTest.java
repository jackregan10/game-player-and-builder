package oogasalad.view.renderer.sceneRenderer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;

public class BuilderSceneRendererTest extends AbstractSceneRendererTest<Pane, BuilderSceneRenderer> {

  @Override
  protected Pane createRenderContext() {
    return new Pane();
  }

  @Override
  protected BuilderSceneRenderer createSceneRenderer(Pane renderContext) {
    return new BuilderSceneRenderer(renderContext);
  }

  @Test
  void renderSelectionOverlay_DoesNotCrash() {
    Platform.runLater(() -> assertDoesNotThrow(() -> sceneRenderer.renderSelectionOverlay(object)));
    WaitForAsyncUtils.waitForFxEvents();
  }
}

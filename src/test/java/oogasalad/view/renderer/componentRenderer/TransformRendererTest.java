package oogasalad.view.renderer.componentRenderer;

import static org.junit.jupiter.api.Assertions.*;

import javafx.application.Platform;
import oogasalad.model.engine.component.Transform;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;

class TransformRendererTest extends AbstractComponentRendererTest {
  private Transform transform;

  @Override
  protected void customSetup() {
    transform = parent.getComponent(Transform.class);
  }

  @Test
  void renderPane_RealTransformComponent_DoesNotCrash() {
    assertDoesNotThrow(() -> ComponentRenderer.render(transform, pane, 0, 0));
  }

  @Test
  void renderCanvas_RealTransformComponent_DoesNotCrash() {
    Platform.runLater(() -> assertDoesNotThrow(() -> ComponentRenderer.render(transform, graphicsContext, 0, 0)));
    WaitForAsyncUtils.waitForFxEvents();
  }
}

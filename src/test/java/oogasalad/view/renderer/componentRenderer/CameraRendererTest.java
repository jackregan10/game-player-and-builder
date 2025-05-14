package oogasalad.view.renderer.componentRenderer;

import static org.junit.jupiter.api.Assertions.*;

import javafx.application.Platform;
import oogasalad.model.engine.component.Camera;
import oogasalad.model.serialization.serializable.TypeReferenceException;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;

class CameraRendererTest extends AbstractComponentRendererTest {

  private Camera camera;

  @Override
  protected void customSetup() {
    camera = new Camera();
    parent.addComponent(camera);
  }

  @Test
  void renderPane_RealCameraComponent_RendererCalledSuccessfully() {
    assertDoesNotThrow(() -> ComponentRenderer.render(camera, pane, 0, 0));
  }

  @Test
  void renderCanvas_RealCameraComponent_RendererCalledSuccessfully() {
    Platform.runLater(
        () -> assertDoesNotThrow(() -> ComponentRenderer.render(camera, graphicsContext, 0, 0)));
    WaitForAsyncUtils.waitForFxEvents();
  }

  @Test
  void renderPane_NoRendererRegistered_ThrowsTypeReferenceException() {
    DummyRenderable dummyRenderable = new DummyRenderable();
    assertThrows(TypeReferenceException.class,
        () -> ComponentRenderer.render(dummyRenderable, pane, 0, 0));
  }

  @Test
  void renderCanvas_NoRendererRegistered_ThrowsTypeReferenceException() {
    DummyRenderable dummyRenderable = new DummyRenderable();
    Platform.runLater(() -> assertThrows(TypeReferenceException.class,
        () -> ComponentRenderer.render(dummyRenderable, graphicsContext, 0, 0)));
    WaitForAsyncUtils.waitForFxEvents();
  }

  private static class DummyRenderable implements oogasalad.model.engine.component.Renderable {

  }
}

package oogasalad.view.renderer.componentRenderer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.WritableImage;
import oogasalad.model.engine.component.SpriteRenderer;
import oogasalad.model.engine.component.Transform;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.resource.ResourceCache;
import oogasalad.model.resource.ResourcePath;
import oogasalad.view.gui.hoverInfo.HoverInfo;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.testfx.util.WaitForAsyncUtils;

class SpriteRendererRendererTest extends AbstractComponentRendererTest {

  private SpriteRenderer spriteRenderer;

  @Override
  protected void customSetup() {
    spriteRenderer = parent.addComponent(SpriteRenderer.class);
    ResourcePath path = new ResourcePath();
    path.setPath(getClass().getResource("/image/test.png").toExternalForm());
    spriteRenderer.setImagePath(path);

    WritableImage dummyImage = new WritableImage(10, 10);
    ResourceCache.registerCache(spriteRenderer.getImagePath().getPath(), dummyImage);
  }

  @Test
  void renderPane_RealSpriteRendererComponent_WithoutToolkitCrash() {
    try (MockedStatic<HoverInfo> mockedHoverInfo = mockStatic(HoverInfo.class)) {
      mockedHoverInfo.when(
              () -> HoverInfo.installHoverTooltip(any(Node.class), any(GameObject.class)))
          .thenAnswer(invocation -> null);
      assertDoesNotThrow(() -> ComponentRenderer.render(spriteRenderer, pane, 0, 0));
    }
  }

  @Test
  void renderCanvas_RealSpriteRendererComponent_WithoutToolkitCrash() {
    Platform.runLater(() -> assertDoesNotThrow(
        () -> ComponentRenderer.render(spriteRenderer, graphicsContext, 0, 0)));
    WaitForAsyncUtils.waitForFxEvents();
  }

  @Test
  void renderPane_RealSpriteRendererComponent_LogsErrorOnImageLoadFail() {
    SpriteRenderer badSpriteRenderer = new SpriteRenderer();
    ResourcePath path = new ResourcePath();
    path.setPath("this/does/not/exist.png");
    badSpriteRenderer.setImagePath(path);

    GameObject parent = new GameObject("badParent");
    Transform transform = parent.addComponent(Transform.class);
    transform.setX(0);
    transform.setY(0);
    transform.setScaleX(10);
    transform.setScaleY(10);
    parent.addComponent(badSpriteRenderer);

    assertDoesNotThrow(() -> ComponentRenderer.render(badSpriteRenderer, pane, 0, 0));
  }

  @Test
  void renderCanvas_RealSpriteRendererComponent_RenderedSuccessfully() {
    Platform.runLater(() -> assertDoesNotThrow(
        () -> ComponentRenderer.render(spriteRenderer, graphicsContext, 0, 0)));
    WaitForAsyncUtils.waitForFxEvents();
  }

  @Test
  void renderCanvas_RealSpriteRendererComponent_LogsErrorOnImageLoadFail() {
    SpriteRenderer badSpriteRenderer = new SpriteRenderer();
    ResourcePath path = new ResourcePath();
    path.setPath("this/does/not/exist.png");
    badSpriteRenderer.setImagePath(path);

    GameObject parent = new GameObject("badParent");
    Transform transform = parent.addComponent(Transform.class);
    transform.setX(0);
    transform.setY(0);
    transform.setScaleX(10);
    transform.setScaleY(10);
    parent.addComponent(badSpriteRenderer);

    Platform.runLater(() -> assertDoesNotThrow(
        () -> ComponentRenderer.render(badSpriteRenderer, graphicsContext, 0, 0)));
    WaitForAsyncUtils.waitForFxEvents();
  }
}

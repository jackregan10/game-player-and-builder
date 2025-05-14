package oogasalad.view.renderer.sceneRenderer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import javafx.application.Platform;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.engine.component.Camera;
import oogasalad.model.engine.component.Transform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

/**
 * Abstract test class for scene renderers.
 * @param <R> Render context type (Pane, GraphicsContext)
 * @param <T> SceneRenderer under test
 */
public abstract class AbstractSceneRendererTest<R, T extends SceneRenderer<R>> extends ApplicationTest {

  protected R renderContext;
  protected T sceneRenderer;
  protected GameScene gameScene;
  protected GameObject object;

  @BeforeEach
  void setup() {
    renderContext = createRenderContext();
    sceneRenderer = createSceneRenderer(renderContext);

    gameScene = new GameScene("TestScene");

    object = new GameObject("testObject");
    Transform transform = object.addComponent(Transform.class);
    transform.setX(100);
    transform.setY(150);
    transform.setScaleX(50);
    transform.setScaleY(80);
    gameScene.registerObject(object);
  }

  protected abstract R createRenderContext();
  protected abstract T createSceneRenderer(R renderContext);

  @Test
  void clearRenderContext_DoesNotCrash() {
    Platform.runLater(() -> assertDoesNotThrow(() -> sceneRenderer.clearRenderContext()));
    WaitForAsyncUtils.waitForFxEvents();
  }

  @Test
  void renderGameObject_DoesNotCrash() {
    Platform.runLater(() -> assertDoesNotThrow(() -> sceneRenderer.renderGameObject(object)));
    WaitForAsyncUtils.waitForFxEvents();
  }

  @Test
  void renderWithoutCamera_DoesNotCrash() {
    Platform.runLater(() -> assertDoesNotThrow(() -> sceneRenderer.renderWithoutCamera(gameScene)));
    WaitForAsyncUtils.waitForFxEvents();
  }

  @Test
  void renderWithCamera_DoesNotCrash() {
    GameObject cameraObject = new GameObject("cameraObject");
    Transform cameraTransform = cameraObject.addComponent(Transform.class);
    cameraTransform.setX(0);
    cameraTransform.setY(0);
    cameraTransform.setScaleX(800);
    cameraTransform.setScaleY(600);

    Camera camera = cameraObject.addComponent(Camera.class);
    gameScene.registerObject(cameraObject);
    camera.setIsMainCamera(true);

    Platform.runLater(() -> assertDoesNotThrow(() -> sceneRenderer.renderWithCamera(gameScene)));
    WaitForAsyncUtils.waitForFxEvents();
  }
}

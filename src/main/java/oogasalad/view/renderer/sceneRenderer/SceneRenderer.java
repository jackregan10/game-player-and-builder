package oogasalad.view.renderer.sceneRenderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.engine.component.Camera;
import oogasalad.model.engine.component.Transform;

/**
 * This abstraction serves as a template for different types of scene renders involving GameObjects.
 * For example, GameSceneRenderer renders game objects to a Canvas, while BuilderSceneRenderer
 * renders game objects to a Pane.
 *
 * @author Calvin Chen, Reyan Shariff, Hsuan-Kai Liao
 */
public abstract class SceneRenderer<T> {

  private final T renderContext;

  private double cameraX;
  private double cameraY;
  private double cameraScaleX;
  private double cameraScaleY;

  /**
   * Constructor for SceneRenderer.
   *
   * @param renderContext the context within which objects will be rendered, such as a Pane
   */
  public SceneRenderer(T renderContext) {
    this.renderContext = renderContext;
  }

  /**
   * For scenes that have a camera: renders the game objects in the given scene onto the canvas.
   * If a scene doesn't have a camera, this is identical to call renderWithoutCamera.
   *
   * @param scene The game scene to render, containing all game objects.
   */
  public final void renderWithCamera(GameScene scene) {
    Camera camera = scene.getMainCamera();
    if (camera == null) {
      renderWithoutCamera(scene);
      return;
    }

    Transform cameraTransform = camera.getComponent(Transform.class);
    cameraX = cameraTransform.getX();
    cameraY = cameraTransform.getY();
    cameraScaleX = cameraTransform.getScaleX();
    cameraScaleY = cameraTransform.getScaleY();

    clearRenderContext();
    Collection<GameObject> objects = scene.getAllObjectsInView();
    objects = applyRenderOrder(objects);
    for (GameObject obj : objects) {
      renderGameObject(obj);
    }
  }

  /**
   * Renders entire scene without camera; optionally highlights a selected object.
   *
   * @param scene the GameScene to render
   */
  public final void renderWithoutCamera(GameScene scene) {
    cameraX = 0.0;
    cameraY = 0.0;
    cameraScaleX = 0.0;
    cameraScaleY = 0.0;

    clearRenderContext();
    Collection<GameObject> objects = scene.getActiveObjects();
    Collection<GameObject> renderOrder = applyRenderOrder(objects);
    for (GameObject obj : renderOrder) {
      renderGameObject(obj);
    }
  }

  /**
   * Render the game component onto the render context.
   *
   * @param obj the given gameObject
   */
  protected abstract void renderGameObject(GameObject obj);

  // NOTE: Ideally this should not be called every frame, there should be a listener observing the
  //       list that changes
  //   By: Hsuan-Kai Liao
  private Collection<GameObject> applyRenderOrder(Collection<GameObject> objects) {
    List<GameObject> renderObjects = objects.stream()
        .map(obj -> obj.getComponent(Transform.class))
        .sorted((Comparator.comparingInt(Transform::getZIndex)))
        .map(GameComponent::getParent)
        .toList();

    return new ArrayList<>(renderObjects);
  }

  /* SUBCLASS API */

  /**
   * Remove all displayed objects in the current render platform, such as GraphicsContext or Pane.
   */
  protected abstract void clearRenderContext();

  /**
   * Get the horizontal position of the camera relative to world coordinates.
   *
   * @return X-coordinate of camera's top-left corner
   */
  protected double getCameraX() {
    return cameraX;
  }

  /**
   * Get the vertical position of the camera relative to world coordinates.
   *
   * @return Y-coordinate of camera's top-left corner
   */
  protected double getCameraY() {
    return cameraY;
  }

  /**
   * Get the horizontal width of the camera relative to world coordinates.
   */
  protected double getCameraScaleX() {
    return cameraScaleX;
  }

  /**
   * Get the vertical height of the camera relative to world coordinates.
   */
  protected double getCameraScaleY() {
    return cameraScaleY;
  }

  /**
   * Get the render context for this class or its subclasses, such as a Pane or Canvas.
   *
   * @return the JavaFX object which the game objects will render onto.
   */
  protected T getRenderContext() {
    return renderContext;
  }
}

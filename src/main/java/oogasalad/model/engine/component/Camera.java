package oogasalad.model.engine.component;

import static oogasalad.model.config.GameConfig.LOGGER;

import java.util.ArrayList;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.MissingParentSceneException;
import java.util.List;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.serialization.serializable.SerializableField;

/**
 * The CameraComponent class is used to represent a camera in the game. It is responsible for
 * determining which objects are in view.
 */

public class Camera extends GameComponent implements Renderable {

  @SerializableField
  private boolean isMainCamera;

  private Transform transform;

  /**
   * Default constructor for the Camera class.
   */
  public Camera() {
    super();
  }

  @Override
  public void awake() {
    transform = getParent().getComponent(Transform.class);
    if (transform == null) {
      throw new IllegalArgumentException(GameConfig.getText("cameraMustHaveTransformInfo"));
    }
  }

  @Override
  public ComponentTag componentTag() {
    return ComponentTag.CAMERA;
  }

  /**
   * Get all objects in the view of the camera.
   *
   * @return a list of GameObjects that are in the view of the camera.
   */
  public List<GameObject> getObjectsInView() {
    List<GameObject> objects;
    try {
      GameScene scene = getParent().getScene();
      objects = new ArrayList<>(scene.getActiveObjects());
    } catch (MissingParentSceneException e) {
      LOGGER.error("Camera does not have a parent scene", e);
      return new ArrayList<>();
    }
    List<GameObject> objectsInView = new ArrayList<>();
    for (GameObject object : objects) {
      Transform transform = object.getComponent(Transform.class);
      if (isInView(transform)) {
        objectsInView.add(object);
      }
    }
    return objectsInView;
  }


  private boolean isInView(Transform transform) {
    double x = transform.getX();
    double y = transform.getY();
    double width = transform.getScaleX();
    double height = transform.getScaleY();

    double cameraX = this.transform.getX();
    double cameraY = this.transform.getY();
    double cameraWidth = this.transform.getScaleX();
    double cameraHeight = this.transform.getScaleY();
    return (x < cameraX + cameraWidth && x + width > cameraX && y < cameraY + cameraHeight
        && y + height > cameraY);
  }

  /**
   * Get whether the current camera is the main Camera.
   */
  public boolean getIsMainCamera() {
    return isMainCamera;
  }

  /**
   * Set current scene's main render camera to this camera.
   */
  public void setIsMainCamera(boolean mainCamera) {
    this.isMainCamera = mainCamera;
    if (getParent() != null && getParent().getScene() != null) {
      if (isMainCamera) {
        getParent().getScene().setMainCamera(this);
      } else if (getParent().getScene().getMainCamera() == this) {
        getParent().getScene().setMainCamera(null);
      }
    }
  }

  /**
   * Set current scene's main render camera to this camera without updating the scene
   */
  public void noSceneSetMainCamera(boolean mainCamera) {
    this.isMainCamera = mainCamera;
  }
}

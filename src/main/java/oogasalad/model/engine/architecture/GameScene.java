package oogasalad.model.engine.architecture;

import java.text.MessageFormat;
import java.util.*;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.component.ComponentTag;
import oogasalad.model.engine.component.Camera;

/**
 * The GameScene class is the base class for all game scenes. It manages the game objects and
 * components within the scene. It is responsible for updating the game objects and components every
 * frame.
 *
 * @author Hsuan-Kai Liao, Christian Bepler
 */
public class GameScene {

  public static final String PREFAB_SCENE_NAME = "PrefabScene";

  private final UUID id;
  private final Map<UUID, GameObject> activeObjects;
  private final Map<UUID, GameObject> storeObjects;
  private final Map<ComponentTag, List<GameComponent>> allComponents;
  private final Queue<Runnable> subscribedEvents;
  private final Set<GameObject> awakeList;

  private String name;
  private Game game;
  private Camera mainCamera;
  private Double deltaTime;

  /**
   * Constructor for GameScene
   *
   * @param name the name of the scene
   */
  public GameScene(String name) {
    this.id = UUID.randomUUID();
    this.name = name;
    this.activeObjects = new HashMap<>();
    this.storeObjects = new HashMap<>();
    this.allComponents = new EnumMap<>(ComponentTag.class);
    for (ComponentTag tag : ComponentTag.values()) {
      allComponents.put(tag, new ArrayList<>());
    }
    this.subscribedEvents = new LinkedList<>();
    this.awakeList = new HashSet<>();
  }

  /**
   * Set the game that this scene belongs to.
   *
   * @param game the game that this scene belongs to
   */
  final void setGame(Game game) {
    this.game = game;
  }

  /**
   * Get the game that this scene belongs to.
   */
  public Game getGame() {
    return game;
  }

  /**
   * Get the name of the scene
   */
  public final String getName() {
    return name;
  }

  /**
   * Set the name of the scene
   */
  public final void setName(String name) {
    if (game != null && game.getLevelOrder().contains(this.name)) {
      List<String> levelOrder = game.getLevelOrder();
      levelOrder.set(levelOrder.indexOf(this.name), name);
    }
    this.name = name;
  }

  /**
   * Get the UUID of the scene
   */
  public final UUID getId() {
    return id;
  }

  /**
   * Get the Object based on the UUID
   *
   * @param id the UUID of the object
   */
  public final GameObject getObject(UUID id) {
    return activeObjects.get(id);
  }

  /**
   * return the delta time
   * 
   * @return the delta time
   */
  public final Double getDeltaTime() {
    return deltaTime;
  }

  /**
   * set the delta time
   * 
   * @param deltaTime the delta time
   * @apiNote Should really only be used for testing purposes
   */
  public final void setDeltaTime(Double deltaTime) {
    this.deltaTime = deltaTime;
  }

  /**
   * Store the object in the scene
   *
   * @param gameObject the object to be stored
   */
  public final void storeObject(GameObject gameObject) {
    if (storeObjects.containsKey(gameObject.getId())) {
      throw new IllegalArgumentException(
          MessageFormat.format(GameConfig.getText("duplicateGameObject"), gameObject.getName()));
    }
    storeObjects.put(gameObject.getId(), gameObject);
  }

  /**
   * Get the Object based on the name
   *
   * @param name the name of the object
   */
  public final GameObject getObject(String name) {
    return getGameObject(name, activeObjects);
  }

  /**
   * Getter to return a Collection of all the GameObjects
   */
  public final Collection<GameObject> getActiveObjects() {
    return Collections.unmodifiableCollection(activeObjects.values());
  }

  /**
   * Get the store object based on the UUID
   * 
   * @param id the object UUID
   * @return the object based on the UUID
   */
  public final GameObject getStoreObject(UUID id) {
    return storeObjects.get(id);
  }

  /**
   * Get the store object based on the name
   *
   * @param name the name of the object
   * @return the object based on the name
   */
  public final GameObject getStoreObject(String name) {
    return getGameObject(name, storeObjects);
  }

  /**
   * Remove the store object based on the ID
   */
  public final void removeStoreObject(UUID id) {
    storeObjects.remove(id);
  }

  private GameObject getGameObject(String name, Map<UUID, GameObject> objects) {
    for (GameObject object : objects.values()) {
      if (object.getName().equals(name)) {
        return object;
      }
    }
    return null;
  }

  /**
   * Get all the store objects in the scene
   *
   * @return a collection of all the store objects in the scene
   */
  public final Collection<GameObject> getAllStoreObjects() {
    return Collections.unmodifiableCollection(storeObjects.values());
  }

  /**
   * Get all the objects in the view of the mainCamera
   *
   * @return a collection of all the objects in the view of the mainCamera
   */
  public final Collection<GameObject> getAllObjectsInView() {
    getMainCamera();
    if (mainCamera == null || mainCamera.getParent() == null) {
      return Collections.emptyList();
    }

    wakeObject(mainCamera.getParent());
    return mainCamera.getObjectsInView();
  }

  /**
   * Get the mainCamera in the scene
   *
   * @return the mainCamera in the scene
   */
  public final Camera getMainCamera() {
    if (mainCamera == null) {
      mainCamera =
          allComponents.get(ComponentTag.CAMERA).stream().map(component -> (Camera) component)
              .filter(Camera::getIsMainCamera).findFirst().orElse(null);
    }
    return mainCamera;
  }

  /**
   * Set the mainCamera to the given camera component
   * 
   * @param camera the givenCamera
   */
  public final void setMainCamera(Camera camera) {
    if (mainCamera != null) {
      mainCamera.noSceneSetMainCamera(false);
    }
    if (camera != null) {
      camera.noSceneSetMainCamera(true);
    }
    mainCamera = camera;
  }

  /**
   * Getter to return a Collection of all the GameComponents
   */
  public final Map<ComponentTag, List<GameComponent>> getAllComponents() {
    return Collections.unmodifiableMap(allComponents);
  }

  /**
   * This will be called every frame.
   *
   * @param deltaTime the elapsed time between two frames
   */
  public final void step(double deltaTime) {
    this.deltaTime = deltaTime;

    // 1. Run the subscribed event
    runSubscribedEvents();

    // 3. Update the components of objects
    updateComponents(activeObjects.values(), deltaTime);
  }

  private void runSubscribedEvents() {
    while (!subscribedEvents.isEmpty()) {
      subscribedEvents.poll().run();
    }
  }

  private void updateComponents(Collection<GameObject> objectsInView, double deltaTime) {
    for (ComponentTag order : ComponentTag.values()) {
      if (order == ComponentTag.NONE) {
        continue;
      }
      updateObjects(order, objectsInView, deltaTime);
    }
  }

  private void updateObjects(ComponentTag order, Collection<GameObject> objectsInView,
      double deltaTime) {
    Iterator<GameObject> iterator = objectsInView.iterator();

    // NOTE: Avoid concurrent modification
    while (iterator.hasNext()) {
      GameObject object = iterator.next();
      wakeObject(object);
      Iterator<GameComponent> componentIterator = object.getComponents(order).iterator();
      while (componentIterator.hasNext()) {
        GameComponent component = componentIterator.next();
        component.update(deltaTime);
      }
    }
  }

  private void wakeObject(GameObject object) {
    if (awakeList.contains(object)) {
      object.wakeUp();
      awakeList.remove(object);
    }
  }

  /**
   * Subscribe the runnable event to the next frame to execute. Events will only be called once and
   * then removed from the subscribed list.
   *
   * @param event the event to be subscribed
   */
  public final void subscribeEvent(Runnable event) {
    subscribedEvents.add(event);
  }

  /**
   * Register the component from the gameObject onto the scene
   *
   * @param gameComponent the component to be registered
   */
  final void registerComponent(GameComponent gameComponent) {
    // Main Camera check
    if (gameComponent instanceof Camera camera && camera.getIsMainCamera()) {
      if (mainCamera != null && mainCamera.getIsMainCamera()) {
        camera.noSceneSetMainCamera(false);
      } else {
        mainCamera = camera;
      }
    }

    allComponents.get(gameComponent.componentTag()).add(gameComponent);
  }

  /**
   * Unregister the component from the scene.
   *
   * @param gameComponent the gameComponent to be unregistered
   */
  final void unregisterComponent(GameComponent gameComponent) {
    if (mainCamera == gameComponent) {
      mainCamera = null;
    }
    allComponents.get(gameComponent.componentTag()).remove(gameComponent);
  }

  /**
   * Register the existing gameObject to the scene.
   *
   * @param gameObject the gameObject to be registered.
   */
  public final void registerObject(GameObject gameObject) {
    if (activeObjects.containsKey(gameObject.getId())) {
      throw new IllegalArgumentException(
          MessageFormat.format(GameConfig.getText("duplicateGameObject"), gameObject.getName()));
    }

    gameObject.setScene(this);

    // Register components
    for (GameComponent component : gameObject.getAllComponents().values()) {
      registerComponent(component);
    }

    awakeList.add(gameObject);
    activeObjects.put(gameObject.getId(), gameObject);
  }

  /**
   * unregister the gameObject specified.
   *
   * @param gameObject the gameObject to be destroyed
   */
  public final void unregisterObject(GameObject gameObject) {
    // Unregister components
    List<GameComponent> componentsToDelete = new ArrayList<>();
    for (ComponentTag order : ComponentTag.values()) {
      for (GameComponent component : allComponents.get(order)) {
        if (component.getParent().equals(gameObject)) {
          componentsToDelete.add(component);
        }
      }
    }
    componentsToDelete.forEach(this::unregisterComponent);

    gameObject.setScene(null);
    activeObjects.remove(gameObject.getId());
  }

  /**
   * Move the store object to the scene.
   *
   * @param name the name of the store object
   */
  public void moveStoreToScene(String name) {
    GameObject storeObject = getStoreObject(name);
    storeObjects.remove(storeObject.getId());
    registerObject(storeObject);
  }

  /**
   * Move the object to the store.
   *
   * @param name the name of the object
   */
  public void moveSceneToStore(String name) {
    GameObject object = getObject(name);
    activeObjects.remove(object.getId());
    unregisterObject(object);
    object.setScene(this);
    storeObject(object);
  }

  /**
   * Change the scene to the specified scene name.
   *
   * @param sceneName the name of the scene to be changed to
   */
  final void changeScene(String sceneName) {
    game.changeScene(sceneName);
  }


  /**
   * Event that will be called when the gameScene is set to active.
   */
  public void onActivated() {
    // Override if needed
  }

  /**
   * Event that will be called when the gameScene is set to inactive.
   */
  public void onDeactivated() {
    // Override if needed
  }
}

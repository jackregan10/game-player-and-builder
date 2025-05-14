package oogasalad.model.engine.datastore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.engine.subComponent.behavior.BehaviorComponent;
import oogasalad.model.engine.component.BehaviorController;
import oogasalad.model.serialization.serializable.GetSerializedFieldException;

/**
 * ScriptableDataStore provides a centralized access layer for shared data and dynamic field
 * tracking within a GameScene.
 * <p>
 * It supports both manually stored key-value pairs and automatic reflection-based access to
 *
 * @author Logan Dracos
 * @SerializableField values within GameObjects. This enables dynamic binding for features like HUD
 * displays, score tracking, or other observable values used in rendering or scripting.
 *
 * @author Logan Dracos
 */
public class ScriptableDataStore {

  private static final int KEY_PATH_PARTS_EXPECTED_LENGTH = 2;

  private final Map<String, Object> data = new HashMap<>();
  private GameScene scene;

  /**
   * Stores a manually set value under the given key.
   *
   * @param key   the identifier for the stored value
   * @param value the value to store (can be any Object)
   */
  public void set(String key, Object value) {
    data.put(key, value);
  }

  /**
   * Retrieves the value associated with a manually stored key.
   *
   * @param key the identifier to look up
   * @return the value associated with the key
   * @throws MissingKeyException if the key is not found in the store
   */
  public Object get(String key) {
    ParsedKey parsed = new ParsedKey(key);
    if (!has(parsed.getBaseKeyPath())) {
      throw new MissingKeyException(
          GameConfig.getText("noValueInScriptable", parsed.getBaseKeyPath()));
    }
    return data.get(parsed.getBaseKeyPath());
  }

  /**
   * Dynamically retrieves the value of a field annotated with @SerializableField from a GameObject
   * in the scene using a dot-separated key path (e.g., "player1.score"). Note: if the user wants
   * the value to be saved to long-term storage (saved between gameScenes), then they should add
   * "/save" to the end of the string, which is handled by the first line of the method
   *
   * @param keyPath the key path in the format "objectId.fieldName"
   * @return the current value of the field
   * @throws IllegalArgumentException if the format is incorrect
   * @throws MissingKeyException      if the GameObject or field is not found
   * @throws FieldAccessException     if the field could not be accessed
   */
  public Object getValue(String keyPath) {
    ParsedKey parsed = new ParsedKey(keyPath);
    String[] parts = parsed.getBaseKeyPath().split("\\.");

    if (parts.length != KEY_PATH_PARTS_EXPECTED_LENGTH) {
      throw new IllegalArgumentException(GameConfig.getText("invalidKeyPath", keyPath));
    }

    String objectName = parts[0];
    String fieldName = parts[1];

    GameObject obj;
    try {
      obj = scene.getObject(objectName);
      assert obj != null;
    } catch (AssertionError e) {
      throw new MissingKeyException(GameConfig.getText("noMatchingGameObject", objectName), e);
    }

    Object retrieveFromSerializable = getObject(obj, fieldName, objectName);
    if (parsed.shouldSave()) {
      Object usedForOperation = retrieveFromSerializable;
      if (has(parsed.getSaveAlias())) {
        usedForOperation = DataStoreOp.ADD.apply(retrieveFromSerializable,
            get(parsed.getSaveAlias()));
      }
      return parsed.applyOperations(usedForOperation, this);
    }
    return parsed.applyOperations(retrieveFromSerializable, this);
  }

  private static Object getObject(GameObject obj, String fieldName, String objectName) {
    return obj == null ? null :
        obj.getAllComponents().values().stream()
            .flatMap(c -> c.getSerializedFields().stream())
            .filter(sf -> sf.getFieldName().equals(fieldName))
            .findFirst()
            .map(sf -> {
              try {
                return sf.getValue();
              } catch (GetSerializedFieldException e) {
                throw new FieldAccessException(GameConfig.getText("fieldAccessException", fieldName), e);
              }
            })
            .orElseThrow(() ->
                new MissingKeyException(
                    GameConfig.getText("missingKeyException", fieldName, objectName)));
  }

  /**
   * Retrieves a manually stored value and attempts to cast it to a specified type.
   *
   * @param key  the key of the value to retrieve
   * @param type the Class type to cast to
   * @param <T>  the expected type of the value
   * @return the casted value
   * @throws MissingKeyException if the key is not found
   * @throws ClassCastException  if the value is not of the expected type
   */
  public <T> T getAs(String key, Class<T> type) {
    Object value = get(key);
    if (!type.isInstance(value)) {
      throw new ClassCastException(
          GameConfig.getText("mismatchKeyValue", key, type.getSimpleName()));
    }
    return type.cast(value);
  }


  /**
   * Checks whether a manually stored value exists for the given key.
   *
   * @param key the key to check
   * @return true if the key exists in the data store, false otherwise
   */
  public boolean has(String key) {
    return data.containsKey(key);
  }

  /**
   * Public method for setting current data store game scene for accessing serializable values.
   *
   * @param scene the GameScene to bind to this data store
   */
  public void setScene(GameScene scene) {
    this.scene = scene;
  }

  /**
   * A method to be called when a scene is being deactivated that saves all relevant data into
   * long-term storage for use in other scenes. Does so using the /save tag present on the parameter
   * of any field to be saved to long-term
   */
  public void saveMarkedKeys() {
    scene.getAllComponents().values().stream()
        .flatMap(Collection::stream)
        .filter(c -> c instanceof BehaviorController)
        .map(BehaviorController.class::cast)
        .flatMap(controller -> controller.getBehaviors().stream())
        .flatMap(behavior -> behavior.getActions().stream())
        .forEach(component -> trySaveParameter(component));
  }

  /**
   * Attempts to save a BehaviorComponent's parameter if it is marked with /save.
   *
   * @param component the BehaviorComponent to process
   */
  private void trySaveParameter(BehaviorComponent<?> component) {
    Object param = component.getParameter();
    if (param instanceof String fieldName) {
      ParsedKey parsedKey = new ParsedKey(fieldName);
      if (parsedKey.shouldSave()) {
        String saveKey = parsedKey.getSaveAlias() != null
            ? parsedKey.getSaveAlias()
            : parsedKey.getBaseKeyPath();
        set(saveKey, getValue(parsedKey.getOriginal()));
      }
    }
  }

  /**
   * Clears long term data storage
   */
  public void clear(){
    data.clear();
  }
}
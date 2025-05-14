package oogasalad.view.gui.panel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import java.util.function.Predicate;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import oogasalad.model.config.GameConfig;
import oogasalad.model.builder.UndoRedoManager;
import oogasalad.model.builder.actions.BuilderPanelEditAction;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.engine.component.Transform;
import oogasalad.view.gui.label.EditableLabel;

/**
 * This is the object panel that holds all the in scene objects in the
 * scene in the current game, and their selection handling and storage switching.
 *
 * @author Hsuan-Kai Liao
 */
public class ObjectPanel extends VBox {

  private static final String DELETE_BUTTON = "-";
  private static final String SAVE_AS_PREFAB_BUTTON = "⧉";
  private static final String STORE_BUTTON = "▣";
  private static final String UNSTORE_BUTTON = "⬚";
  private static final double DEFAULT_OBJECT_SIZE = 100;
  private static final String DEFAULT_BG_COLOR = "rgba(216, 216, 216, 1)";
  private static final String SELECTED_BG_COLOR = "rgba(160, 196, 255, 1)";
  private static final String STORED_BG_COLOR = "rgba(216,216,216,0.5)";


  private final VBox objectListContainer;
  private GameScene scene;
  private String selectedObjectName = null;

  private Consumer<GameObject> onObjectClicked;
  private Consumer<GameObject> onObjectDeleted;
  private Consumer<GameObject> onObjectSavedAsPrefab;

  /**
   * Sets a callback for when a GameObject is clicked.
   */
  public void setOnObjectClicked(Consumer<GameObject> callback) {
    this.onObjectClicked = callback;
  }

  /**
   * Sets a callback for when a GameObject is deleted.
   */
  public void setOnObjectDeleted(Consumer<GameObject> callback) {
    this.onObjectDeleted = callback;
  }

  /**
   * Sets a callback for when a GameObject is saved as a prefab.
   */
  public void setOnObjectSavedAsPrefab(Consumer<GameObject> callback) {
    this.onObjectSavedAsPrefab = callback;
  }

  /**
   * Constructs a new ObjectPanel for displaying and editing GameObjects.
   */
  public ObjectPanel() {
    super(6);
    this.setPadding(new Insets(8));

    objectListContainer = new VBox(6);
    this.getChildren().add(objectListContainer);

    Button addObjectButton = new Button(GameConfig.getText("addGameObject"));
    addObjectButton.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
    addObjectButton.setMaxWidth(Double.MAX_VALUE);
    addObjectButton.setOnAction(e -> handleAddObject());

    this.getChildren().add(addObjectButton);
  }

  /**
   * Sets the GameScene to display and modify objects from.
   */
  public void setScene(GameScene scene) {
    this.scene = scene;
    this.selectedObjectName = null;
    objectsSync();
  }

  /**
   * Sets the selected GameObject by name.
   */
  public void setObjectSelection(GameObject object) {
    this.selectedObjectName = (object != null) ? object.getName() : null;
    objectsSync();
  }

  /**
   * Refreshes the object list view from the scene.
   */
  public void objectsSync() {
    objectListContainer.getChildren().clear();

    record NamedGameObject(GameObject object, boolean isStored) {}

    List<NamedGameObject> allObjects = new ArrayList<>();
    scene.getAllStoreObjects().forEach(obj -> allObjects.add(new NamedGameObject(obj, true)));
    scene.getActiveObjects().forEach(obj -> allObjects.add(new NamedGameObject(obj, false)));

    allObjects.stream()
        .sorted(Comparator.comparing(o -> o.object().getName(), String.CASE_INSENSITIVE_ORDER))
        .forEach(entry -> objectListContainer.getChildren().add(createObjectBox(entry.object(), entry.isStored())));
  }

  private HBox createObjectBox(GameObject obj, boolean isStored) {
    HBox objBox = createBaseBox(obj, isStored);
    objBox.setOnMouseClicked(e -> handleBoxClick(obj));

    EditableLabel nameLabel = createEditableLabel(obj.getName(), newName -> {
      if (scene.getObject(newName) == null || newName.equals(obj.getName())) {
        obj.setName(newName);
        return true;
      }
      return false;
    });
    nameLabel.setAlignment(Pos.CENTER_LEFT);
    Region spacer = createSpacer();
    EditableLabel tagLabel = createEditableLabel(obj.getTag(), newTag -> {
      obj.setTag(newTag);
      return true;
    });
    tagLabel.setAlignment(Pos.CENTER_RIGHT);
    Button storeToggleButton = createStoreToggleButton(obj, isStored);
    Button saveButton = createActionButton(SAVE_AS_PREFAB_BUTTON, e -> handlePrefabSave(obj));
    Button deleteButton = createActionButton(DELETE_BUTTON, e -> handleDelete(obj));

    objBox.getChildren().addAll(nameLabel, spacer, tagLabel, storeToggleButton, saveButton, deleteButton);
    return objBox;
  }

  private HBox createBaseBox(GameObject obj, boolean isStored) {
    HBox box = new HBox(4);
    box.setAlignment(Pos.CENTER_LEFT);
    String bgColor = isSelected(obj) ? SELECTED_BG_COLOR : (isStored ? STORED_BG_COLOR : DEFAULT_BG_COLOR);
    box.setStyle("-fx-background-color: " + bgColor + "; -fx-padding: 4px; -fx-background-radius: 6px;");
    return box;
  }

  private void handleBoxClick(GameObject obj) {
    if (!obj.getName().equals(selectedObjectName)) {
      selectedObjectName = obj.getName();
      if (onObjectClicked != null) {
        onObjectClicked.accept(obj);
      }
      objectsSync();
    }
  }

  private EditableLabel createEditableLabel(String initValue, Predicate<String> changeListener) {
    EditableLabel label = new EditableLabel(initValue);
    label.setCursor(Cursor.HAND);
    label.setStyle("-fx-font-size: 13px;");
    label.setCompleteListener(this::objectsSync);
    label.setChangeListener(changeListener);
    HBox.setHgrow(label, Priority.NEVER);
    return label;
  }

  private Region createSpacer() {
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    return spacer;
  }

  private Button createStoreToggleButton(GameObject obj, boolean isStored) {
    String buttonText = isStored ? UNSTORE_BUTTON : STORE_BUTTON;
    return createActionButton(buttonText, e -> handleStoreToggle(obj, isStored));
  }

  private void handleStoreToggle(GameObject obj, boolean wasStored) {
    if (wasStored) {
      scene.moveStoreToScene(obj.getName());
    } else {
      scene.moveSceneToStore(obj.getName());
    }
    objectsSync();

    UndoRedoManager.addAction(new BuilderPanelEditAction<>(
        () -> undoStoreToggle(obj, wasStored),
        () -> redoStoreToggle(obj, wasStored)
    ));
  }

  private void undoStoreToggle(GameObject obj, boolean wasStored) {
    if (wasStored) {
      scene.moveSceneToStore(obj.getName());
    } else {
      scene.moveStoreToScene(obj.getName());
    }
    objectsSync();
  }

  private void redoStoreToggle(GameObject obj, boolean wasStored) {
    if (wasStored) {
      scene.moveStoreToScene(obj.getName());
    } else {
      scene.moveSceneToStore(obj.getName());
    }
    objectsSync();
  }


  private Button createActionButton(String text, EventHandler<ActionEvent> action) {
    Button button = new Button(text);
    button.setOnAction(action);
    button.setStyle("-fx-font-size: 12px; -fx-background-color: transparent;");
    button.setCursor(Cursor.HAND);
    HBox.setHgrow(button, Priority.NEVER);
    return button;
  }

  private boolean isSelected(GameObject obj) {
    return obj.getName().equals(selectedObjectName);
  }

  private void handleAddObject() {
    int index = scene.getActiveObjects().size() + 1;
    String newName = "Object" + index;
    while (scene.getObject(newName) != null) {
      index++;
      newName = "Object" + index;
    }

    GameObject newObj = new GameObject(newName, "");
    Transform transform = newObj.addComponent(Transform.class);
    transform.setScaleX(DEFAULT_OBJECT_SIZE);
    transform.setScaleY(DEFAULT_OBJECT_SIZE);
    scene.registerObject(newObj);
    selectedObjectName = newObj.getName();
    if (onObjectClicked != null) {
      onObjectClicked.accept(newObj);
    }
    objectsSync();

    UndoRedoManager.addAction(new BuilderPanelEditAction<>(
        () -> {
          GameObject existing = scene.getObject(newObj.getName());
          if (existing != null) {
            scene.unregisterObject(existing);
            objectsSync();
          }
          selectedObjectName = null;
        },
        () -> {
          scene.registerObject(newObj);
          objectsSync();
        }
    ));

  }

  private void handlePrefabSave(GameObject obj) {
    GameObject prefab = obj.clone();
    prefab.setName(obj.getName() + GameConfig.getText("prefabNote"));

    if (onObjectSavedAsPrefab != null) {
      onObjectSavedAsPrefab.accept(prefab);
    }

    objectsSync();


  }

  private void handleDelete(GameObject obj) {
    scene.unregisterObject(obj);
    selectedObjectName = null;

    if (onObjectDeleted != null) {
      onObjectDeleted.accept(obj);
    }

    objectsSync();

    UndoRedoManager.addAction(new BuilderPanelEditAction<>(
        () -> {
          scene.registerObject(obj);
          objectsSync();
        },
        () -> {
          GameObject existing = scene.getObject(obj.getName());
          if (existing != null) {
            scene.unregisterObject(existing);
            objectsSync();
          }
          selectedObjectName = null;
        }
    ));
  }
}

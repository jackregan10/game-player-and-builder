package oogasalad.view.gui.panel;

import java.util.Collections;
import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import oogasalad.model.builder.UndoRedoManager;
import oogasalad.model.builder.actions.BuilderPanelEditAction;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.Game;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.engine.component.Camera;
import oogasalad.model.engine.component.Transform;
import oogasalad.view.gui.label.EditableLabel;

/**
 * This is the scene panel that holds all the scene in the current game and handle their switching.
 *
 * @author Hsuan-Kai Liao, Reyan Shariff
 */
public class ScenePanel extends VBox {

  private static final String MOVE_UP_BUTTON = "▲";
  private static final String MOVE_DOWN_BUTTON = "▼";
  private static final String DELETE_BUTTON = "-";
  private static final String SELECTED_SCENE_BG_COLOR = "rgba(144, 238, 144, 1)";
  private static final String PREFAB_SCENE_BG_COLOR = "rgba(255, 228, 181, 1)";
  private static final String DEFAULT_SCENE_BG_COLOR = "rgba(211, 211, 211, 1)";

  private final VBox sceneListContainer;

  private Game game;
  private GameScene prefabScene;
  private Consumer<GameScene> onSceneClicked;
  private String selectedSceneName = null;

  /**
   * Set a callback for when a scene is clicked
   *
   * @param callback function to execute when a scene is selected
   */
  public void setOnSceneClicked(Consumer<GameScene> callback) {
    this.onSceneClicked = callback;
  }

  /**
   * Constructor for creating Scene UI panel.
   */
  public ScenePanel() {
    super(6);
    this.setPadding(new Insets(10));

    sceneListContainer = new VBox(3);
    this.getChildren().add(sceneListContainer);
    Button addSceneButton = new Button(GameConfig.getText("addScene"));
    addSceneButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
    addSceneButton.setOnAction(e -> handleAddScene());

    VBox.setMargin(addSceneButton, new Insets(10, 0, 0, 0));
    addSceneButton.setMaxWidth(Double.MAX_VALUE);
    addSceneButton.setAlignment(Pos.CENTER);

    this.getChildren().add(addSceneButton);
  }

  /**
   * Load game data and display scene list
   *
   * @param game the game instance containing all scenes
   */
  public void setGame(Game game) {
    this.game = game;

    if (game.getScene(GameScene.PREFAB_SCENE_NAME) == null) {
      prefabScene = new GameScene(GameScene.PREFAB_SCENE_NAME);
      game.addScene(prefabScene);
    } else {
      prefabScene = game.getScene(GameScene.PREFAB_SCENE_NAME);
    }

    selectedSceneName = game.getCurrentScene().getName();
    refreshSceneList();
  }

  /**
   * Returns the special Prefab Scene.
   *
   * @return the prefab scene object
   */
  public GameScene getPrefabScene() {
    return prefabScene;
  }

  private void refreshSceneList() {
    sceneListContainer.getChildren().clear();
    for (String sceneName : game.getLevelOrder()) {
      if (!sceneName.equals(GameScene.PREFAB_SCENE_NAME)) {
        GameScene scene = game.getScene(sceneName);
        if (scene != null)
        {
          sceneListContainer.getChildren().add(createSceneBox(scene, false));
        }
      }
    }
    sceneListContainer.getChildren().add(createSceneBox(prefabScene, true));
  }

  private HBox createSceneBox(GameScene scene, boolean isPrefab) {
    HBox sceneBox = createBaseSceneBox(scene, isPrefab);
    EditableLabel label = createSceneLabel(scene);
    label.setEditable(!isPrefab);
    HBox buttonBox = createSceneButtonBox(scene, isPrefab);

    sceneBox.setOnMouseClicked(event -> handleSceneClick(scene));
    sceneBox.getChildren().addAll(label, buttonBox);
    return sceneBox;
  }

  private HBox createBaseSceneBox(GameScene scene, boolean isPrefab) {
    HBox box = new HBox();
    box.setAlignment(Pos.CENTER);
    String bgColor = isSelected(scene) ? SELECTED_SCENE_BG_COLOR : (isPrefab ? PREFAB_SCENE_BG_COLOR : DEFAULT_SCENE_BG_COLOR);
    box.setStyle("-fx-background-color: " + bgColor + "; -fx-padding: 4px; -fx-background-radius: 6px;");
    return box;
  }

  private EditableLabel createSceneLabel(GameScene scene) {
    EditableLabel label = new EditableLabel(scene.getName());
    label.setCursor(Cursor.HAND);
    label.setCompleteListener(() -> {
      selectedSceneName = scene.getName();
      refreshSceneList();
    });
    label.setChangeListener(newName -> {
      if (game.getScene(newName) == null) {
        scene.setName(newName);
        return true;
      }
      return game.getScene(newName).equals(scene);
    });
    return label;
  }

  private void handleSceneClick(GameScene scene) {
    if (isSceneAlreadySelected(scene)) {
      return;
    }

    String previousSceneName = selectedSceneName;
    String newSceneName = scene.getName();

    updateSelectedScene(scene);
    createSceneSwitchUndoAction(previousSceneName, newSceneName);
  }

  private boolean isSceneAlreadySelected(GameScene scene) {
    return selectedSceneName.equals(scene.getName());
  }

  private void updateSelectedScene(GameScene scene) {
    selectedSceneName = scene.getName();
    if (onSceneClicked != null) {
      onSceneClicked.accept(scene);
    }
    UndoRedoManager.setCurrentScene(scene);
    refreshSceneList();
  }

  private void createSceneSwitchUndoAction(String previousSceneName, String newSceneName) {
    UndoRedoManager.addAction(new BuilderPanelEditAction<>(
        () -> undoSceneSwitch(previousSceneName),
        () -> redoSceneSwitch(newSceneName)
    ));
  }

  private void undoSceneSwitch(String previousSceneName) {
    GameScene prevScene = game.getScene(previousSceneName);
    if (prevScene != null) {
      updateSelectedScene(prevScene);
    }
  }

  private void redoSceneSwitch(String newSceneName) {
    GameScene newScene = game.getScene(newSceneName);
    if (newScene != null) {
      updateSelectedScene(newScene);
    }
  }


  private HBox createSceneButtonBox(GameScene scene, boolean isPrefab) {
    HBox buttonBox = new HBox(0);
    buttonBox.setAlignment(Pos.CENTER_RIGHT);

    if (isPrefab) return buttonBox;

    ObservableList<String> levelOrder = FXCollections.observableArrayList(game.getLevelOrder());
    int index = levelOrder.indexOf(scene.getName());

    int zero = 0;
    if (index > zero) {
      buttonBox.getChildren().add(createActionButton(MOVE_UP_BUTTON, e -> moveSceneUp(scene)));
    }

    int one = 1;
    if (index < levelOrder.size() - one) {
      buttonBox.getChildren().add(createActionButton(MOVE_DOWN_BUTTON, e -> moveSceneDown(scene)));
    }

    int two = 2;
    if (game.getAllScenes().size() > two) {
      buttonBox.getChildren().add(createActionButton(DELETE_BUTTON, e -> handleDelete(scene)));
    }

    return buttonBox;
  }

  private Button createActionButton(String text, EventHandler<ActionEvent> action) {
    Button button = new Button(text);
    button.setOnAction(action);
    button.setStyle("-fx-font-size: 14px; -fx-background-color: transparent;");
    button.setCursor(Cursor.HAND);
    HBox.setHgrow(button, Priority.NEVER);
    return button;
  }

  private boolean isSelected(GameScene scene) {
    return scene != null && scene.getName().equals(selectedSceneName);
  }

  private void moveSceneUp(GameScene scene) {
    ObservableList<String> levelOrder = FXCollections.observableArrayList(game.getLevelOrder());
    int index = levelOrder.indexOf(scene.getName());
    int zero = 0;
    if (index > zero) {
      Collections.swap(levelOrder, index, index - 1);
      game.setLevelOrder(levelOrder);
      refreshSceneList();
      this.requestLayout();
    }

    UndoRedoManager.addAction(new BuilderPanelEditAction<>(
        () -> {
          ObservableList<String> undoOrder = FXCollections.observableArrayList(game.getLevelOrder());
          Collections.swap(undoOrder, index - 1, index);
          game.setLevelOrder(undoOrder);
          refreshSceneList();
          this.requestLayout();
        },
        () -> {
          ObservableList<String> redoOrder = FXCollections.observableArrayList(game.getLevelOrder());
          Collections.swap(redoOrder, index, index - 1);
          game.setLevelOrder(redoOrder);
          refreshSceneList();
          this.requestLayout();
        }
    ));
  }

  private void moveSceneDown(GameScene scene) {
    ObservableList<String> levelOrder = FXCollections.observableArrayList(game.getLevelOrder());
    int index = levelOrder.indexOf(scene.getName());
    int one = 1;
    if (index < levelOrder.size() - one) {
      Collections.swap(levelOrder, index, index + 1);
      game.setLevelOrder(levelOrder);
      refreshSceneList();
      this.requestLayout();
    }

    UndoRedoManager.addAction(new BuilderPanelEditAction<>(
        () -> {
          ObservableList<String> undoOrder = FXCollections.observableArrayList(game.getLevelOrder());
          Collections.swap(undoOrder, index + 1, index);
          game.setLevelOrder(undoOrder);
          refreshSceneList();
          this.requestLayout();
        },
        () -> {
          ObservableList<String> redoOrder = FXCollections.observableArrayList(game.getLevelOrder());
          Collections.swap(redoOrder, index, index + 1);
          game.setLevelOrder(redoOrder);
          refreshSceneList();
          this.requestLayout();
        }
    ));
  }

  private void handleDelete(GameScene scene) {
    if (scene == null || !canDeleteScene()) {
      return;
    }

    DeletionContext context = prepareDeletionContext(scene);

    deleteScene(scene);
    registerUndoRedoForDeletion(context);
  }

  private boolean canDeleteScene() {
    return game.getAllScenes().size() > 2;
  }

  private DeletionContext prepareDeletionContext(GameScene scene) {
    GameScene copyOfScene = deepCopyScene(scene);
    String deletedSceneName = scene.getName();
    ObservableList<String> prevLevelOrder = FXCollections.observableArrayList(game.getLevelOrder());
    String previousSelected = selectedSceneName;
    return new DeletionContext(copyOfScene, deletedSceneName, prevLevelOrder, previousSelected);
  }

  private void deleteScene(GameScene scene) {
    game.removeScene(scene.getName());
    updateSelectionAfterDeletion(scene);
    refreshSceneList();
  }

  private void updateSelectionAfterDeletion(GameScene scene) {
    if (isSelected(scene)) {
      selectedSceneName = game.getLevelOrder().getFirst();
      GameScene newSelected = game.getScene(selectedSceneName);
      if (onSceneClicked != null) {
        onSceneClicked.accept(newSelected);
      }
      UndoRedoManager.setCurrentScene(newSelected);
    }
  }

  private void registerUndoRedoForDeletion(DeletionContext context) {
    UndoRedoManager.addAction(new BuilderPanelEditAction<>(
        () -> undoDeleteScene(context),
        () -> redoDeleteScene(context)
    ));
  }

  private void undoDeleteScene(DeletionContext context) {
    game.addScene(context.copyOfScene());
    game.setLevelOrder(context.prevLevelOrder());
    selectedSceneName = context.previousSelected();
    notifySceneSelected(context.previousSelected());
    refreshSceneList();
  }

  private void redoDeleteScene(DeletionContext context) {
    game.removeScene(context.deletedSceneName());
    if (context.deletedSceneName().equals(context.previousSelected())) {
      selectedSceneName = game.getLevelOrder().getFirst();
      notifySceneSelected(selectedSceneName);
    }
    refreshSceneList();
  }

  private void notifySceneSelected(String sceneName) {
    GameScene scene = game.getScene(sceneName);
    if (onSceneClicked != null && scene != null) {
      onSceneClicked.accept(scene);
    }
    UndoRedoManager.setCurrentScene(scene);
  }

  private record DeletionContext(GameScene copyOfScene, String deletedSceneName,
                                 ObservableList<String> prevLevelOrder, String previousSelected) {}

  private GameScene deepCopyScene(GameScene original) {
    GameScene copy = new GameScene(original.getName());

    for (GameObject obj : original.getAllStoreObjects()) {
      copy.registerObject(obj.clone());
    }
    for (GameObject obj : original.getActiveObjects()) {
      copy.registerObject(obj.clone());
    }

    return copy;
  }


  private void handleAddScene() {
    int newIndex = game.getAllScenes().size() + 1;
    String newSceneName = "Scene" + newIndex;
    while (game.getScene(newSceneName) != null) {
      newIndex++;
      newSceneName = "Scene" + newIndex;
    }

    GameScene newScene = new GameScene(newSceneName);
    GameObject camera = new GameObject("Camera", "camera");
    Transform transform = camera.addComponent(Transform.class);
    transform.setScaleX(GameConfig.getNumber("windowWidth"));
    transform.setScaleY(GameConfig.getNumber("windowHeight"));
    camera.addComponent(Camera.class);
    newScene.registerObject(camera);
    game.addScene(newScene);

    final String sceneNameFinal = newSceneName;
    String previousSelected = selectedSceneName;
    ObservableList<String> prevLevelOrder = FXCollections.observableArrayList(game.getLevelOrder());
    final GameScene copyOfNewScene = deepCopyScene(newScene);

    selectedSceneName = newSceneName;
    if (onSceneClicked != null) {
      onSceneClicked.accept(newScene);
    }
    UndoRedoManager.setCurrentScene(newScene);
    refreshSceneList();

    UndoRedoManager.addAction(new BuilderPanelEditAction<>(
        () -> {
          game.removeScene(sceneNameFinal);
          game.setLevelOrder(prevLevelOrder);
          selectedSceneName = previousSelected;
          if (onSceneClicked != null) {
            onSceneClicked.accept(game.getScene(previousSelected));
          }
          UndoRedoManager.setCurrentScene(game.getScene(previousSelected));
          refreshSceneList();
        },
        () -> {
          game.addScene(copyOfNewScene);
          selectedSceneName = sceneNameFinal;
          if (onSceneClicked != null) {
            onSceneClicked.accept(copyOfNewScene);
          }
          UndoRedoManager.setCurrentScene(copyOfNewScene);
          refreshSceneList();
        }
    ));
  }

}

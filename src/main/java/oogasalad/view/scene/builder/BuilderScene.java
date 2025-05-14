package oogasalad.view.scene.builder;

import static oogasalad.model.config.GameConfig.getText;

import java.io.File;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import oogasalad.controller.LevelController;
import oogasalad.database.DatabaseException;
import oogasalad.model.builder.Builder;
import oogasalad.model.builder.UndoRedoManager;
import oogasalad.model.builder.actions.BuilderPanelEditAction;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.Game;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.engine.component.Transform;
import oogasalad.model.profile.LevelData;
import oogasalad.model.profile.SessionException;
import oogasalad.model.profile.SessionManagement;
import oogasalad.model.resource.ResourcePath;
import oogasalad.view.config.StyleConfig;
import oogasalad.view.gui.docking.Docker;
import oogasalad.view.gui.docking.Docker.DockPosition;
import oogasalad.view.gui.error.PopUpError;
import oogasalad.view.gui.panel.ComponentPanel;
import oogasalad.view.gui.panel.CoordinatePanel;
import oogasalad.view.gui.panel.ObjectPanel;
import oogasalad.view.gui.panel.PrefabPanel;
import oogasalad.view.gui.panel.ScenePanel;
import oogasalad.view.renderer.sceneRenderer.BuilderSceneRenderer;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.ViewScene;
import oogasalad.view.scene.display.GameDisplayScene;

import static oogasalad.model.config.GameConfig.LOGGER;


/**
 * BuilderView is the main view for the level editor
 *
 * @author Hsuan-Kai Liao, Reyan Shariff, Calvin Chen
 */
public class BuilderScene extends ViewScene {

  // Static Constants
  private static final String GAME_PREVIEW = "GamePreviewScene";
  private static final String SAVE_PROFILE_BUTTON_ID = "saveProfileButton";
  private static final String SAVE_LOCAL_BUTTON_ID = "saveLocalButton";
  private static final String MAIN_MENU_BUTTON_ID = "mainMenuButton";
  private static final String PREVIEW_BUTTON_ID = "previewButton";
  private static final String LEVEL_NAME_ID = "levelName";
  private static final String LEVEL_DESCRIPTION_ID = "levelDescription";
  private static final String NAME_FIELD_ID = "nameField";
  private static final String DESCRIPTION_FIELD_ID = "descriptionField";
  private static final String SAVE_TITLE_ID = "saveTitle";
  private static final String HEADER_TEXT_ID = "headerText";
  private static final String DEFAULT_NAME = "defaultName";
  private static final String DEFAULT_DESCRIPTION = "defaultDescription";
  private static final String UPDATE_BUTTON_ID = "updateButton";
  private static final String TEMP_DATA_STORAGE_PREFIX = "data/GameJsons/TEMP/temp_";

  // final builder data
  private final Builder builder;
  private final ObjectDragger objectDragger;
  private final Docker myDocker;
  private final Pane myGameCanvas;
  private final BuilderSceneRenderer mySceneRenderer;
  private final Map<GameObject, Map<String, Boolean>> componentUIExpandState = new HashMap<>();
  private boolean editMode = false;
  private LevelData currentLevelData;
  private String prevLevelName = "";

  // Render Loop
  private AnimationTimer renderLoop;

  // Panels
  private CoordinatePanel myGameViewPane;
  private ComponentPanel componentPanel;
  private ScenePanel scenePanel;
  private ObjectPanel objectPanel;
  private PrefabPanel prefabPanel;

  // GUIs
  private Button previewButton;
  private Button mainMenuButton;
  private Button saveProfileButton;
  private Button saveLocalButton;
  private TextField nameField;
  private TextField descField;

  private BuilderScene(Stage stage) {
    super(stage);
    myDocker = new Docker(stage);
    builder = new Builder();
    myGameCanvas = new Pane();
    mySceneRenderer = new BuilderSceneRenderer(myGameCanvas);
    objectDragger = new ObjectDragger(builder, myGameCanvas);

    myDocker.clearStyleSheets();
    myDocker.addStyleSheet(StyleConfig.getBaseStyleSheet());
    StyleConfig.registerOnSceneChange(sheet -> {
      myDocker.clearStyleSheets();
      myDocker.addStyleSheet(StyleConfig.getBaseStyleSheet());
      myDocker.addStyleSheet(sheet);
    });

    setUpGUI();
    setUpRenderLoop();
    setUpListeners();
    setUpKeyInputs();
  }

  @Override
  public void onActivate() {
    previewButton.setDisable(false);
    renderLoop.start();
  }

  @Override
  public void onDeactivate() {
    builder.deselect();
    myGameViewPane.setDragLocked(false);
    renderLoop.stop();
  }

  /**
   * Set up the builder for the given game filepath. This should be called when the new game file is
   * to be edited.
   *
   * @param gameFilepath the given game filepath
   */
  public void reload(String gameFilepath) {
    try {
      String parentDirectory = Paths.get(gameFilepath).getParent().toString();
      ResourcePath.setFromContext(parentDirectory);

      builder.loadGame(gameFilepath);
      componentUIExpandState.clear();
      scenePanel.setGame(builder.getGame());
      objectPanel.setScene(builder.getCurrentScene());
      prefabPanel.setPrefabScene(scenePanel.getPrefabScene());
      myGameViewPane.setDragLocked(false);
      myDocker.reformat();

      UndoRedoManager.setCurrentScene(builder.getCurrentScene());
      Platform.runLater(() -> myGameViewPane.resetTransform());
    } catch (IllegalStateException e) {
      String message = MessageFormat.format(getText("noGameFileSelected"),
          e.getMessage());
      throw new IllegalStateException(message, e);
    }
  }

  /* LISTENERS SET UP */

  private void setUpListeners() {
    setUpStageListener();
    setUpBuilderListener();
    setUpObjectDraggerListener();
    setUpTopBarListeners();
    setUpPanelsListeners();
  }

  private void setUpStageListener() {
    getStage().setOnCloseRequest(event -> {
      if (showAlert()) {
        Platform.exit();
      } else {
        event.consume();
      }
    });
  }

  private void setUpBuilderListener() {
    builder.setOnChange((previous, current) -> {
      objectPanel.setObjectSelection(current);
      if (previous != null) {
        componentUIExpandState.put(previous, componentPanel.getExpandState());
      }

      componentPanel.setGameObject(current);

      if (current != null) {
        componentPanel.setExpandState(
            componentUIExpandState.getOrDefault(current, new HashMap<>()));
        myGameViewPane.setDragLocked(true);
      } else {
        myGameViewPane.setDragLocked(false);
      }
    });
  }

  private void setUpObjectDraggerListener() {
    objectDragger.setOnComplete(componentPanel::requestSync);
  }

  private void setUpTopBarListeners() {
    previewButton.setOnAction(e -> handlePreviewButton());
    mainMenuButton.setOnAction(e -> handleMainMenuButton());
    saveProfileButton.setOnAction(e -> handleSaveProfileButton());
    saveLocalButton.setOnAction(e -> handleSaveLocalButton());
  }

  private void handlePreviewButton() {
    GameDisplayScene previewScene = (GameDisplayScene) MainViewManager.getInstance()
        .getViewScene(GAME_PREVIEW);
    Game clone = builder.getGame().clone();
    clone.changeScene(builder.getCurrentScene().getName());
    previewScene.play(clone);
    MainViewManager.getInstance().switchTo(GAME_PREVIEW);
  }

  private void handleMainMenuButton() {
    if (showAlert()) {
      MainViewManager.getInstance().switchToMainMenu();
    }
  }

  private void handleSaveProfileButton() {
    Optional<LevelData> levelDataResult = showLevelDetailsDialog();

    if (levelDataResult.isPresent()) {
      LevelData levelData = levelDataResult.get();
      handleNullValues(levelData);

      try {
        createTempAndDeleteFolder(levelData);

        LOGGER.info(GameConfig.getText("levelSavedAndUploaded"));
      } catch (DatabaseException e) {
        PopUpError.showError("Unexpected error occurred: " + e.getMessage());
        LOGGER.error("Unexpected error: {}", e.getMessage(), e);
        PopUpError.showError(
            String.format(GameConfig.getText("failedToSaveOrUploadLevel"), e.getMessage()));
        LOGGER.error(String.format(GameConfig.getText("failedToSaveOrUploadLevel"), e.getMessage()),
            e);
      }
    }
  }

  private void createTempAndDeleteFolder(LevelData levelData) throws DatabaseException {
    String sanitizedLevelName = levelData.getLevelName().replaceAll("\\s+", "_");

    String baseFolder = "data/GameJsons/" + sanitizedLevelName;
    File baseFolderFile = new File(baseFolder);
    baseFolderFile.mkdirs();

    String jsonFilePath = baseFolder + "/" + sanitizedLevelName + ".json";

    builder.saveGameAs(jsonFilePath);

    LevelController levelController = new LevelController();
    handleEditMode(levelController, levelData, baseFolderFile);

    deleteDirectoryRecursively(baseFolderFile);
  }

  private void handleSaveLocalButton() {
    javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
    fileChooser.setTitle(GameConfig.getText("uploadFileTitle"));
    fileChooser.setInitialDirectory(new File("data/GameJsons/"));
    fileChooser.getExtensionFilters().add(
        new javafx.stage.FileChooser.ExtensionFilter("JSON Files", "*.json")
    );
    File selectedFile = fileChooser.showSaveDialog(myDocker.getMainStage().getScene().getWindow());
    if (selectedFile != null) {
      builder.saveGameAs(selectedFile.getPath());
    }
  }

  private void handleEditMode(LevelController levelController, LevelData levelData, File tempFile)
      throws DatabaseException {
    if (!editMode) {
      levelController.handleUploadUserLevel(levelData, tempFile);
    } else {
      levelController.handleUpdatingLevel(levelData, tempFile, prevLevelName);
    }
  }

  private void deleteDirectoryRecursively(File dir) {
    if (dir.isDirectory()) {
      File[] children = dir.listFiles();
      if (children != null) {
        Arrays.stream(children).forEach(this::deleteDirectoryRecursively);
      }
    }
    if (!dir.delete()) {
      LOGGER.warn(GameConfig.getText("temporaryFileDeleteFail", dir));
    }
  }

  private static void handleNullValues(LevelData levelData) {
    if (levelData.getLevelName().isBlank()) {
      levelData.setLevelName(DEFAULT_NAME + System.currentTimeMillis() / 1000);
    }

    if (levelData.getLevelDescription().isBlank()) {
      levelData.setLevelDescription(DEFAULT_DESCRIPTION);
    }
  }

  private void setUpPanelsListeners() {
    setUpObjectPanelListeners();
    setUpScenePanelListeners();
    setUpPrefabPanelListeners();
  }

  private void setUpObjectPanelListeners() {
    objectPanel.setStyle("-fx-border-color: Gray;");
    setUpObjectClickListener();
    setUpObjectDeleteListener();
    setUpObjectSaveAsPrefabListener();
  }

  private void setUpObjectClickListener() {
    objectPanel.setOnObjectClicked(obj -> {
      builder.selectExistingObject(obj);
      prefabPanel.prefabsSync();
    });
  }

  private void setUpObjectDeleteListener() {
    objectPanel.setOnObjectDeleted(obj -> {
      if (Objects.equals(obj, builder.getSelectedObject())) {
        builder.deselect();
      }
      prefabPanel.prefabsSync();
    });
  }

  private void setUpObjectSaveAsPrefabListener() {
    objectPanel.setOnObjectSavedAsPrefab(obj -> {
      GameScene prefabGameScene = scenePanel.getPrefabScene();
      GameObject oldPrefab = unregisterExistingPrefab(prefabGameScene, obj.getName());

      registerPrefab(prefabGameScene, obj);
      prefabPanel.prefabsSync();

      addPrefabUndoRedo(prefabGameScene, obj, oldPrefab);
    });
  }

  private GameObject unregisterExistingPrefab(GameScene prefabScene, String prefabName) {
    GameObject oldPrefab = prefabScene.getObject(prefabName);
    if (oldPrefab != null) {
      prefabScene.unregisterObject(oldPrefab);
    }
    prefabScene = scenePanel.getPrefabScene(); // refresh after unregister
    GameObject duplicate = prefabScene.getObject(prefabName);
    if (duplicate != null) {
      prefabScene.unregisterObject(duplicate);
    }
    return oldPrefab;
  }

  private void registerPrefab(GameScene prefabScene, GameObject obj) {
    prefabScene.registerObject(obj);
  }

  private void addPrefabUndoRedo(GameScene prefabScene, GameObject newPrefab,
      GameObject oldPrefab) {
    UndoRedoManager.addAction(new BuilderPanelEditAction<>(
        () -> {
          prefabScene.unregisterObject(newPrefab);
          if (oldPrefab != null) {
            prefabScene.registerObject(oldPrefab);
          }
          prefabPanel.prefabsSync();
        },
        () -> {
          if (oldPrefab != null) {
            prefabScene.unregisterObject(oldPrefab);
          }
          prefabScene.registerObject(newPrefab);
          prefabPanel.prefabsSync();
        }
    ));
  }

  private void setUpScenePanelListeners() {
    scenePanel.setOnSceneClicked(scene -> {
      if (scene.getName().equals(GameScene.PREFAB_SCENE_NAME)) {
        previewButton.setDisable(true);
        myGameViewPane.setStyle(
            "-fx-border-color: #555555; -fx-border-width: 2; -fx-background-color: #fff3e1;");
      } else {
        previewButton.setDisable(false);
        myGameViewPane.setStyle(
            "-fx-border-color: #555555; -fx-border-width: 2; -fx-background-color: #efefef;");
      }

      builder.deselect();
      builder.setCurrentScene(scene);
      objectPanel.setScene(scene);
    });
  }

  private void setUpPrefabPanelListeners() {
    prefabPanel.setOnInstantiatePrefab(obj -> {
      Transform t = obj.getComponent(Transform.class);
      double centerX = myGameViewPane.getWidth() / 2;
      double centerY = myGameViewPane.getHeight() / 2;
      double offsetX = t.getScaleX() / 2;
      double offsetY = t.getScaleY() / 2;

      Point2D centerInCanvas = myGameCanvas.sceneToLocal(
          myGameViewPane.localToScene(centerX, centerY));

      t.setX(centerInCanvas.getX() - offsetX);
      t.setY(centerInCanvas.getY() - offsetY);

      int index = 0;
      String originalName = obj.getName();
      while (builder.getCurrentScene().getObject(obj.getName()) != null) {
        obj.setName(originalName + " " + index);
        index++;
      }
      builder.getCurrentScene().registerObject(obj);
      objectPanel.objectsSync();

      UndoRedoManager.popLastAction();
      UndoRedoManager.addAction(new BuilderPanelEditAction<>(
          () -> {
            GameObject existing = builder.getCurrentScene().getObject(obj.getName());
            if (existing != null) {
              builder.getCurrentScene().unregisterObject(existing);
            }
            objectPanel.objectsSync();
          },
          () -> {
            builder.getCurrentScene().registerObject(obj);
            objectPanel.objectsSync();
          }
      ));
    });
  }

  private void setUpKeyInputs() {
    getStage().getScene().getAccelerators().put(
        new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN),
        () -> UndoRedoManager.undo(builder.getCurrentScene())
    );
    getStage().getScene().getAccelerators().put(
        new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN),
        () -> UndoRedoManager.redo(builder.getCurrentScene())
    );

    getStage().getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
    });

  }

  /* GUI INITIALIZATION */

  private void setUpGUI() {
    // TopBar
    HBox operations = createTopBar();
    myDocker.setTopBar(operations);

    // Prefabs
    prefabPanel = new PrefabPanel();
    myDocker.createDWindow(new SimpleStringProperty(getText("prefabTitle")),
        createPanel(prefabPanel),
        DockPosition.BOTTOM);

    // Scenes
    scenePanel = new ScenePanel();
    myDocker.createDWindow(new SimpleStringProperty(getText("sceneTitle")), createPanel(scenePanel),
        DockPosition.RIGHT);

    // Game Preview
    myGameViewPane = createGameEditorView();
    myDocker.createDWindow(new SimpleStringProperty(getText("viewTitle")),
        myGameViewPane, DockPosition.TOP);

    // Objects
    objectPanel = new ObjectPanel();
    myDocker.createDWindow(new SimpleStringProperty(getText("objectTitle")),
        createPanel(objectPanel), DockPosition.RIGHT);

    // Component
    componentPanel = new ComponentPanel();
    myDocker.createDWindow(new SimpleStringProperty(getText("componentTitle")),
        createPanel(componentPanel), DockPosition.RIGHT);
  }

  private void setUpRenderLoop() {
    renderLoop = new AnimationTimer() {
      @Override
      public void handle(long now) {
        if (builder.getCurrentScene() != null) {
          // Game Canvas
          mySceneRenderer.renderWithoutCamera(builder.getCurrentScene());
          mySceneRenderer.renderSelectionOverlay(builder.getSelectedObject());

          // Prefab Preview
          prefabPanel.prefabsViewSync();
        }
      }
    };
  }

  private HBox createTopBar() {
    // Top bar
    HBox topBar = new HBox();
    mainMenuButton = new Button(getText(MAIN_MENU_BUTTON_ID));
    previewButton = new Button(getText(PREVIEW_BUTTON_ID));
    saveProfileButton = new Button(getText(SAVE_PROFILE_BUTTON_ID));
    saveLocalButton = new Button(getText(SAVE_LOCAL_BUTTON_ID));

    topBar.getChildren().addAll(mainMenuButton, previewButton, saveProfileButton, saveLocalButton);
    return topBar;
  }

  private CoordinatePanel createGameEditorView() {
    CoordinatePanel container = new CoordinatePanel(myGameCanvas);
    container.setStyle(
        "-fx-border-color: #555555; -fx-border-width: 2; -fx-background-color: #efefef;");
    return container;
  }

  private ScrollPane createPanel(Node root) {
    ScrollPane scrollPane = new ScrollPane(root);
    scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
    scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
    scrollPane.setFitToHeight(true);
    scrollPane.setFitToWidth(true);
    return scrollPane;
  }

  private boolean showAlert() {
    if (!builder.isSaved()) {
      Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
      confirmAlert.setTitle(GameConfig.getText("confirmExit"));
      confirmAlert.setHeaderText(GameConfig.getText("unsavedChangesMayBeLost"));
      confirmAlert.setContentText(GameConfig.getText("areYouSureQuit"));

      Optional<ButtonType> result = confirmAlert.showAndWait();

      if (result.isPresent() && result.get() == ButtonType.OK) {
        GameConfig.LOGGER.info(GameConfig.getText("confirmedExit"));
        return true;
      } else {
        GameConfig.LOGGER.info(GameConfig.getText("canceledExit"));
        return false;
      }
    }

    // Note: It's safe to exit if the builder is already saved
    return true;
  }

  private Optional<LevelData> showLevelDetailsDialog() {
    Dialog<LevelData> dialog = new Dialog<>();
    dialog.setTitle(getText(SAVE_TITLE_ID));
    dialog.setHeaderText(getText(HEADER_TEXT_ID));

    ButtonType saveButtonType = new ButtonType(getText(SAVE_TITLE_ID),
        ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

    createAndInitializeFields();

    VBox content = new VBox(10, new Label(getText(NAME_FIELD_ID)), nameField,
        new Label(getText(DESCRIPTION_FIELD_ID)), descField);
    content.setPadding(new Insets(20));
    dialog.getDialogPane().setContent(content);

    convertResultToLevelData(dialog, saveButtonType);

    return dialog.showAndWait();
  }

  private void createAndInitializeFields() {
    nameField = new TextField();
    nameField.setPromptText(getText(LEVEL_NAME_ID));
    descField = new TextField();
    descField.setPromptText(getText(LEVEL_DESCRIPTION_ID));

    if (editMode && currentLevelData != null) {
      nameField.setText(currentLevelData.getLevelName());
      descField.setText(currentLevelData.getLevelDescription());
    }
  }

  private void convertResultToLevelData(Dialog<LevelData> dialog, ButtonType saveButtonType) {
    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == saveButtonType) {
        if (editMode && currentLevelData != null) {
          return getExistingLevelData();
        } else {
          return createNewLevelData();
        }
      }
      return null;
    });
  }

  private LevelData getExistingLevelData() {
    currentLevelData.setLevelName(nameField.getText().trim());
    currentLevelData.setLevelDescription(descField.getText().trim());
    return currentLevelData;
  }

  private LevelData createNewLevelData() {
    LevelData data = new LevelData();
    data.setLevelName(nameField.getText().trim());
    data.setLevelDescription(descField.getText().trim());
    try {
      data.setCreatorUsername(SessionManagement.getInstance().getCurrentUser().getUsername());
    } catch (SessionException e) {
      LOGGER.error(GameConfig.getText("notLoggedIn"));
    }
    return data;
  }

  /**
   * Method to allow the builder to know whether it's editing or creating a new level
   *
   * @param levelData - the previously created level
   */
  public void setEditMode(LevelData levelData) {
    currentLevelData = levelData;
    prevLevelName = levelData.getLevelName();
    saveProfileButton.setText(getText(UPDATE_BUTTON_ID));
    editMode = true;
  }
}

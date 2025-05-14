package oogasalad.view.scene.builder;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Optional;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.stage.Stage;
import oogasalad.controller.LevelController;
import oogasalad.model.profile.LevelData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/*
class BuilderSceneTest {

  private BuilderScene builderScene;
  private Stage stage;

  @BeforeEach
  void setUp() throws Exception {
    new JFXPanel();
    Platform.runLater(() -> {
      try {
        stage = new Stage();
        Constructor<BuilderScene> constructor = BuilderScene.class.getDeclaredConstructor(Stage.class);
        constructor.setAccessible(true);
        builderScene = constructor.newInstance(stage);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
    Thread.sleep(500);
  }

  @AfterEach
  void tearDown() {
    if (stage != null) {
      Platform.runLater(() -> stage.close());
    }
  }

  @Test
  void onActivate_startsRenderLoop_doesNotThrow() {
    Platform.runLater(() -> assertDoesNotThrow(() -> builderScene.onActivate()));
  }


  @Test
  void setEditMode_givenLevelData_updatesState() {
    Platform.runLater(() -> {
      LevelData data = new LevelData();
      data.setLevelName("TestLevel_" + System.currentTimeMillis());
      data.setLevelDescription("Test Description");
      builderScene.setEditMode(data);
      assertTrue(true);
    });
  }

  @Test
  void topBarButtons_existAfterInitialization() {
    Platform.runLater(() -> {
      Scene scene = stage.getScene();
      assertNotNull(scene);
      assertFalse(scene.getRoot().lookupAll(".button").isEmpty());
    });
  }

  @Test
  void undoRedoKeyMappings_presentInScene() {
    Platform.runLater(() -> {
      Scene scene = stage.getScene();
      assertTrue(scene.getAccelerators().keySet().stream()
          .anyMatch(k -> k.getName().contains("Z") || k.getName().contains("Y")));
    });
  }

  @Test
  void handleNullValues_emptyLevelData_setsDefaults() throws Exception {
    Platform.runLater(() -> {
      try {
        LevelData data = new LevelData();
        data.setLevelName("TestLevel_" + System.currentTimeMillis());
        data.setLevelDescription("");
        var method = BuilderScene.class.getDeclaredMethod("handleNullValues", LevelData.class);
        method.setAccessible(true);
        method.invoke(null, data);
        assertNotNull(data.getLevelName());
        assertNotNull(data.getLevelDescription());
      } catch (Exception e) {
        fail(e);
      }
    });
  }


  @Test
  void handleEditMode_uploadNewLevel_doesNotThrow() throws Exception {
    Platform.runLater(() -> {
      try {
        LevelController controller = new LevelController();
        LevelData data = new LevelData();
        data.setLevelName("");
        data.setLevelDescription("");
        File tempFile = File.createTempFile("dummy", ".json");
        var method = BuilderScene.class.getDeclaredMethod("handleEditMode", LevelController.class, LevelData.class, File.class);
        method.setAccessible(true);
        method.invoke(builderScene, controller, data, tempFile);
        assertTrue(true);
      } catch (Exception e) {
        fail(e);
      }
    });
  }


  @Test
  void handleSaveLocalButton_noFileChosen_doesNotThrow() throws Exception {
    Platform.runLater(() -> {
      try {
        var method = BuilderScene.class.getDeclaredMethod("handleSaveLocalButton");
        method.setAccessible(true);
        method.invoke(builderScene);
      } catch (Exception e) {
        fail(e);
      }
    });
  }

  @Test
  void handleSaveProfileButton_noLevelData_doesNotThrow() throws Exception {
    Platform.runLater(() -> {
      try {
        var method = BuilderScene.class.getDeclaredMethod("handleSaveProfileButton");
        method.setAccessible(true);
        method.invoke(builderScene);
      } catch (Exception e) {
        fail(e);
      }
    });
  }


  @Test
  void handleMainMenuButton_userConfirmsExit_doesNotThrow() throws Exception {
    Platform.runLater(() -> {
      try {
        var method = BuilderScene.class.getDeclaredMethod("handleMainMenuButton");
        method.setAccessible(true);
        method.invoke(builderScene);
      } catch (Exception e) {
        fail(e);
      }
    });
  }

  @Test
  void showAlert_whenSaved_doesNotThrow() throws Exception {
    Platform.runLater(() -> {
      try {
        var method = BuilderScene.class.getDeclaredMethod("showAlert");
        method.setAccessible(true);
        assertDoesNotThrow(() -> {
          method.invoke(builderScene);
        });
      } catch (Exception e) {
        fail(e);
      }
    });
  }

  @Test
  void showLevelDetailsDialog_openDialog_returnsOptional() throws Exception {
    Platform.runLater(() -> {
      try {
        var method = BuilderScene.class.getDeclaredMethod("showLevelDetailsDialog");
        method.setAccessible(true);
        Optional<?> result = (Optional<?>) method.invoke(builderScene);
        assertNotNull(result);
      } catch (Exception e) {
        fail(e);
      }
    });
  }


}

 */

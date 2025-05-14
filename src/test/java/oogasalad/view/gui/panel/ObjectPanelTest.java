package oogasalad.view.gui.panel;

import static org.junit.jupiter.api.Assertions.*;

import javafx.scene.Scene;
import javafx.stage.Stage;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.engine.component.Transform;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class ObjectPanelTest extends ApplicationTest {

  private ObjectPanel objectPanel;
  private GameScene mockScene;

  @Override
  public void start(Stage stage) {
    objectPanel = new ObjectPanel();
    Scene scene = new Scene(objectPanel, 600, 400);
    stage.setScene(scene);
    stage.show();

    mockScene = new GameScene("test");
    objectPanel.setScene(mockScene);
  }

  @Test
  void addObject_WhenClicked_AddsNewObjectToSceneAndPanel() {
    clickOn("Add GameObject");
    assertEquals(1, mockScene.getActiveObjects().size());
  }

  @Test
  void clickObjectBox_WhenNotSelected_SelectsObjectAndTriggersCallback() {
    GameObject obj = new GameObject("TestObject", "");
    obj.addComponent(Transform.class);
    mockScene.registerObject(obj);

    final boolean[] wasClicked = {false};
    objectPanel.setOnObjectClicked(o -> wasClicked[0] = true);
    interact(objectPanel::objectsSync);

    clickOn("TestObject");
    assertTrue(wasClicked[0]);
  }
}

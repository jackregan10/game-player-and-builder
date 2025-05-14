package oogasalad.view.gui.panel;

import java.util.Map;
import javafx.stage.Stage;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.Transform;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

class ComponentPanelTest extends DukeApplicationTest {

  private ComponentPanel componentPanel;
  private GameObject testObject;

  @Override
  public void start(Stage stage) {
    componentPanel = new ComponentPanel();
    testObject = new GameObject("test");
    testObject.addComponent(Transform.class);
  }

  @Test
  void setGameObject_ShouldUpdatePanel() {
    componentPanel.setGameObject(testObject);
    assertFalse(componentPanel.getChildren().isEmpty(), "ComponentPanel should have children after setting a GameObject.");
  }

  @Test
  void getExpandState_ShouldReturnEmptyWhenNoExpandStateSet() {
    assertTrue(componentPanel.getExpandState().isEmpty(), "Expand state should be empty initially.");
  }

  @Test
  void setExpandState_ShouldUpdateExpandState() {
    componentPanel.setExpandState(Map.of("Component1", true));
    assertTrue(componentPanel.getExpandState().get("Component1"), "Expand state for Component1 should be true.");
  }
}

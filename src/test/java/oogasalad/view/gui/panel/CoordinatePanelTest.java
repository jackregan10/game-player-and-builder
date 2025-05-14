package oogasalad.view.gui.panel;

import javafx.geometry.HorizontalDirection;
import javafx.geometry.VerticalDirection;
import javafx.scene.Scene;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;


public class CoordinatePanelTest extends DukeApplicationTest {

  private CoordinatePanel coordinatePanel;
  private Pane container;

  @Override
  public void start(Stage stage) {
    container = new Pane(); // Create a Pane container
    coordinatePanel = new CoordinatePanel(container);
    Scene scene = new Scene(coordinatePanel);
    stage.setScene(scene);
    stage.show();
  }

  @Test
  public void setDragLocked_WhenTrue_DisablesDragging() {
    coordinatePanel.setDragLocked(true);
    coordinatePanel.setOnMouseDragged(event -> {
      assert false : "Scroll event should not be triggered when drag is locked";
    });
    runAsJFXAction(() -> {
      clickOn(coordinatePanel);
      drag(100, 100);
    });
  }

  @Test
  public void setDragLocked_WhenFalse_AllowsDragging() {
    coordinatePanel.setDragLocked(false);
    coordinatePanel.setOnScroll(event -> {
      assert true : "Scroll event should be triggered when drag is not locked";
    });
    runAsJFXAction(() -> {
      coordinatePanel.requestFocus();
      drag(100, 100);
    });
  }
}

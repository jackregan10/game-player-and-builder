package oogasalad.view.gui.docking;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import oogasalad.view.gui.docking.Docker.DockPosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

public class DockingTest extends DukeApplicationTest {
  private static final String DUMMY_WINDOW_NAME = "DUMMY DWindow";
  private static final Rectangle DUMMY_RECTANGLE = new Rectangle();

  private Stage mainStage;
  private Docker docker;

  @Override
  public void start(Stage primaryStage) {
    mainStage = primaryStage;
    docker = new Docker(mainStage);
  }

  @Test
  public void createDWindow_WithNullDocking_DoesNotThrowException() {
    runAsJFXAction(() -> {
      docker.createDWindow(
          new SimpleStringProperty(DUMMY_WINDOW_NAME),
          DUMMY_RECTANGLE,
          DockPosition.NONE
      );
    });
  }

  @Test
  public void createDWindow_WithTopDocking_DocksAtTop() {
    runAsJFXAction(() -> {
      docker.createDWindow(
          new SimpleStringProperty(DUMMY_WINDOW_NAME),
          DUMMY_RECTANGLE,
          DockPosition.TOP
      );
    });
  }

  @Test
  public void createDWindow_WithBottomDocking_DocksAtBottom() {
    runAsJFXAction(() -> {
      docker.createDWindow(
          new SimpleStringProperty(DUMMY_WINDOW_NAME),
          DUMMY_RECTANGLE,
          DockPosition.BOTTOM
      );
    });
  }

  @Test
  public void createDWindow_WithLeftDocking_DocksAtLeft() {
    runAsJFXAction(() -> {
      docker.createDWindow(
          new SimpleStringProperty(DUMMY_WINDOW_NAME),
          DUMMY_RECTANGLE,
          DockPosition.LEFT
      );
    });
  }

  @Test
  public void createDWindow_WithRightDocking_DocksAtRight() {
    runAsJFXAction(() -> {
      docker.createDWindow(
          new SimpleStringProperty(DUMMY_WINDOW_NAME),
          DUMMY_RECTANGLE,
          DockPosition.RIGHT
      );
    });
  }

  @Test
  public void reformat_WhenCalled_ReformatsAllDWindows() {
    runAsJFXAction(() -> {
      docker.reformat();
    });
  }

  @Test
  public void addAndClearStyleSheet_ShouldApplyStylesheetCorrectly() {
    runAsJFXAction(() -> {
      docker.clearStyleSheets();
      docker.addStyleSheet("test.css");
    });
  }

  @Test
  public void dockerSettings_AdjustMainStageAndDraggingOpacity_SuccessfullyUpdates() {
    runAsJFXAction(() -> {
      Assertions.assertEquals(mainStage, docker.getMainStage());
      docker.setWindowOpaqueOnDragging(true);
    });
  }

}

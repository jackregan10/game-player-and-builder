package oogasalad.view.gui.label;

import static org.junit.jupiter.api.Assertions.*;

import javafx.scene.Scene;
import javafx.stage.Stage;
import oogasalad.view.gui.textField.StringTextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.atomic.AtomicBoolean;
import util.DukeApplicationTest;

public class EditableLabelTest extends DukeApplicationTest {

  private EditableLabel editableLabel;
  private Stage stage;

  @Override
  public void start(Stage stage) {
    editableLabel = new EditableLabel("Click me");
    Scene scene = new Scene(editableLabel, 300, 100);
    this.stage = stage;
    stage.setScene(scene);
    stage.show();
  }

  @BeforeEach
  public void setUp() {
    // Ensure clean state before each test
    interact(() -> {
      editableLabel.setText("Click me");
      editableLabel.setEditable(true);
    });
  }

  @Test
  public void createNewTextField_WhenDoubleClicked_ReplacesLabelWithTextField() {
    doubleClickOn(editableLabel);
    assertNull(editableLabel.getText(), "Label text should be null after editing starts");
    assertTrue(editableLabel.getGraphic() instanceof StringTextField, "Graphic should be a StringTextField");
  }

  @Test
  public void setChangeListener_WhenInputChanges_TriggersValidationLogic() {
    AtomicBoolean wasCalled = new AtomicBoolean(false);

    interact(() -> editableLabel.setChangeListener(input -> {
      wasCalled.set(true);
      return true;
    }));

    doubleClickOn(editableLabel);
    StringTextField field = (StringTextField) editableLabel.getGraphic();
    clickOn(field);
    write("newText");
    assertTrue(wasCalled.get(), "ChangeListener should have been called");
  }

  @Test
  public void setCompleteListener_WhenFocusLost_TriggersCompletionCallback() {
    AtomicBoolean wasCalled = new AtomicBoolean(false);
    interact(() -> editableLabel.setCompleteListener(() -> wasCalled.set(true)));
    doubleClickOn(editableLabel);
    StringTextField field = (StringTextField) editableLabel.getGraphic();
    assertNotNull(field);
    runAsJFXAction(() -> {
      stage.getScene().getRoot().requestFocus();
    });
    assertTrue(wasCalled.get(), "CompleteListener should be triggered on focus loss");
  }
}

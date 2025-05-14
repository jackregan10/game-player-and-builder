package oogasalad.view.gui.deserializedfieldinput;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import oogasalad.model.serialization.serializable.SerializedField;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

class StringFieldInputTest extends ApplicationTest {

  private SerializedField testField;

  @Override
  public void start(Stage stage) throws Exception {
    DummyTarget target = new DummyTarget();
    Field stringField = DummyTarget.class.getDeclaredField("username");
    Method getter = DummyTarget.class.getDeclaredMethod("getUsername");
    Method setter = DummyTarget.class.getDeclaredMethod("setUsername", String.class);

    testField = new SerializedField(target, stringField, null, getter, setter);

    StringFieldInput fieldInput = new StringFieldInput();
    VBox root = new VBox(fieldInput.showGUI(testField));
    stage.setScene(new Scene(root, 400, 200));
    stage.show();
  }

  @Test
  void showGUI_WithValidField_ShouldShowTextField() {
    TextField tf = lookup(".text-field").query();

    assertEquals("default", tf.getText());
  }

  @Test
  void typeInTextField_NewValue_ShouldUpdateSerializedField() {
    TextField tf = lookup(".text-field").query();
    clickOn(tf).eraseText(7).write("newuser");
    waitForFxEvents();

    assertEquals("newuser", testField.getValue());
  }
  public static class DummyTarget {
    private String username = "default";

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }
  }
}
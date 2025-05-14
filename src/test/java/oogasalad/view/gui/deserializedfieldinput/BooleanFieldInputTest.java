package oogasalad.view.gui.deserializedfieldinput;

import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import oogasalad.model.serialization.serializable.SerializedField;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class BooleanFieldInputTest extends ApplicationTest {

  @Override
  public void start(Stage stage) throws Exception {
    DummyTarget target = new DummyTarget();
    Field boolField = DummyTarget.class.getDeclaredField("enabled");
    Method getter = DummyTarget.class.getDeclaredMethod("isEnabled");
    Method setter = DummyTarget.class.getDeclaredMethod("setEnabled", Boolean.class);

    SerializedField testField = new SerializedField(target, boolField, null, getter, setter);

    BooleanFieldInput fieldInput = new BooleanFieldInput();
    VBox root = new VBox(fieldInput.showGUI(testField));
    stage.setScene(new Scene(root, 300, 100));
    stage.show();
  }

  @Test
  void showGUI_FieldTrue_CheckBoxShouldBeSelected() {
    CheckBox cb = lookup(".check-box").query();
    assertTrue(cb.isSelected());
  }

//  @Test
//  void toggleCheckBox_CheckedToUnchecked_FieldShouldUpdateToFalse() {
//    CheckBox cb = lookup(".check-box").query();
//    clickOn(cb);
//    assertEquals(false, testField.getValue());
//  }

  public static class DummyTarget {
    private Boolean enabled = true;

    public Boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
    }
  }
}
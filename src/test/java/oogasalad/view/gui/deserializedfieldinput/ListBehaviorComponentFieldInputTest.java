package oogasalad.view.gui.deserializedfieldinput;

import java.lang.reflect.Type;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import oogasalad.model.engine.subComponent.behavior.Behavior;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.model.engine.subComponent.behavior.BehaviorComponent;
import oogasalad.model.serialization.serializable.SerializedField;
import oogasalad.model.serialization.serializable.TypeRef;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

//ChatGPT assisted
class ListBehaviorComponentFieldInputTest extends ApplicationTest {

  private static final Type LIST_BEHAVIOR_COMPONENT_TYPE = new TypeRef<List<BehaviorComponent<?>>>() {}.getType();
  private static final Class<?> ACTION_CLASS = BehaviorAction.class;
  private static final String ACTION_PACKAGE = "oogasalad.model.engine.action";
  private SerializedField testField;

  @Override
  public void start(Stage stage) throws Exception {
    Behavior behavior = new Behavior("DUMMY");
    testField = behavior.getSerializedField("actions");
    System.out.println(testField.getFieldType());

    ListBehaviorComponentFieldInput fieldInput = (ListBehaviorComponentFieldInput) DeserializedFieldInput.createFieldUI(
        testField, LIST_BEHAVIOR_COMPONENT_TYPE);
    fieldInput.setBehaviorComponentType(ACTION_CLASS, ACTION_PACKAGE);

    VBox root = new VBox(fieldInput.showGUI(testField));
    stage.setScene(new Scene(root, 600, 400));
    stage.show();
  }

  @Test
  void clickOnAddButton_NoDropdownSelection_ShouldHaveEmptySelectionShown_NotCount() {
    clickOn("+");
    waitForFxEvents();
    assertEquals(0, ((List<?>) testField.getValue()).size());
  }

}
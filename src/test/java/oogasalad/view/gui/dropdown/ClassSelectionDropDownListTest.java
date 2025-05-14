package oogasalad.view.gui.dropdown;

import javafx.scene.Scene;
import javafx.stage.Stage;
import oogasalad.view.gui.dropDown.ClassSelectionDropDownList;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ClassSelectionDropDownListTest extends ApplicationTest {

  private ClassSelectionDropDownList dropDownList;

  // Dummy classes for testing
  public static abstract class Animal {}
  @SuppressWarnings("unused")
  public static class Dog extends Animal {}
  @SuppressWarnings("unused")
  public static class Cat extends Animal {}

  @Override
  public void start(Stage stage) {
    // Use a custom package that includes the above classes
    dropDownList = new ClassSelectionDropDownList("Choose Animal", this.getClass().getPackageName(), Animal.class);
    stage.setScene(new Scene(dropDownList));
    stage.show();
  }

  @Test
  void setOnCheckValid_allValid_ShouldContainSubclassesOfAnimal() {
    dropDownList.setOnCheckValid(clazz -> true);
    clickOn(dropDownList);
    Set<String> items = dropDownList.getItems().stream().collect(Collectors.toSet());
    assertTrue(items.contains("Dog"));
    assertTrue(items.contains("Cat"));
    assertFalse(items.contains("Animal")); // Base class should not be included
  }

  @Test
  void setOnCheckValid_allInvalid_ShouldNotContainAnySubclassesOfAnimal() {
    dropDownList.setOnCheckValid(clazz -> false);
    clickOn(dropDownList);
    Set<String> items = dropDownList.getItems().stream().collect(Collectors.toSet());
    assertFalse(items.contains("Dog"));
    assertFalse(items.contains("Cat"));
    assertFalse(items.contains("Animal"));
  }

  @Test
  void dropDown_ShouldSetPromptText() {
    assertEquals("Choose Animal", dropDownList.getPromptText());
  }
}

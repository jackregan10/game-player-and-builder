package oogasalad.view.gui.dropdown;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import oogasalad.view.gui.dropDown.ClassSelectionDropDownMenu;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.testfx.service.query.PointQuery;

import static org.junit.jupiter.api.Assertions.*;

public class ClassSelectionDropDownMenuTest extends ApplicationTest {

  private ClassSelectionDropDownMenu dropDownMenu;

  // Dummy superclass and subclasses
  public static abstract class Animal {}
  @SuppressWarnings("unused")
  public static class Dog extends Animal {}
  @SuppressWarnings("unused")
  public static class Cat extends Animal {}

  @Override
  public void start(Stage stage) {
    dropDownMenu = new ClassSelectionDropDownMenu("Select Animal", this.getClass().getPackageName(), Animal.class);
    stage.setScene(new Scene(dropDownMenu));
    stage.show();
  }

  @Test
  void menu_ShouldContainSubclassesOfAnimal() {
    dropDownMenu.setOnCheckValid(clazz -> true);
    clickOn(dropDownMenu);
    var itemLabels = dropDownMenu.getItems().stream()
        .map(MenuItem::getText)
        .collect(Collectors.toSet());

    assertTrue(itemLabels.contains("Dog"));
    assertTrue(itemLabels.contains("Cat"));
    assertFalse(itemLabels.contains("Animal"));
  }

  @Test
  void menu_ShouldSetTitleCorrectly() {
    assertEquals("Select Animal", dropDownMenu.getText());
  }

  @Test
  void menu_ShouldTriggerCallbackOnSelect() {
    AtomicReference<String> selectedClass = new AtomicReference<>(null);
    dropDownMenu.setOnCheckValid(clazz -> true);
    dropDownMenu.setOnClassSelected(clazz -> selectedClass.set(clazz.getSimpleName()));

    // Simulate selection of "Dog"
    clickOn(dropDownMenu);
    clickOn("Dog");

    assertEquals("Dog", selectedClass.get());
  }
}

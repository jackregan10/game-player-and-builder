package oogasalad.view.gui.dropDown;

import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import org.reflections.Reflections;

/**
 * A drop-down menu for selecting classes from a specified package. This class is used to create a
 * GUI component.
 */
public class ClassSelectionDropDownMenu extends MenuButton {

  private final List<Class<?>> allClasses;
  private Predicate<Class<?>> onCheckValid = clazz -> true;
  private Consumer<Class<?>> selectionCallback;

  /**
   * Constructor for ClassSelectionDropDownMenu.
   *
   * @param packageName the package name to search for classes
   * @param superClass  the superclass to filter classes by
   */
  public ClassSelectionDropDownMenu(String title, String packageName, Class<?> superClass) {
    super(title);

    Reflections reflections = new Reflections(packageName);
    allClasses = reflections.getSubTypesOf(superClass).stream()
        .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()) && !clazz.isInterface())
        .collect(Collectors.toList());

    showingProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal) {
        populateMenuItems();
      }
    });
  }

  private void populateMenuItems() {
    getItems().clear();
    allClasses.stream()
        .filter(onCheckValid)
        .sorted(Comparator.comparing(Class::getSimpleName))
        .forEach(clazz -> {
          MenuItem menuItem = new MenuItem(clazz.getSimpleName());
          menuItem.setOnAction(event -> {
            if (selectionCallback != null) {
              selectionCallback.accept(clazz);
            }
          });
          getItems().add(menuItem);
        });
  }


  /**
   * Sets the predicate to determine if a class is valid for selection.
   *
   * @param checker the predicate that returns true if the class should be shown
   */
  public void setOnCheckValid(Predicate<Class<?>> checker) {
    this.onCheckValid = checker;
  }

  /**
   * Sets the action to be performed when a class is selected.
   *
   * @param callback the callback to be invoked with the selected class
   */
  public void setOnClassSelected(Consumer<Class<?>> callback) {
    this.selectionCallback = callback;
  }
}

package oogasalad.view.gui.dropDown;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import org.reflections.Reflections;

/**
 * A drop-down combo box for selecting classes from a specified package. This class is used to
 * create a GUI component.
 */
public class ClassSelectionDropDownList extends ComboBox<String> {

  private List<Class<?>> allClasses;
  private Predicate<Class<?>> onCheckValid = clazz -> true;

  /**
   * Set the predicate to determine if a class is valid for selection.
   *
   * @param checker a predicate that returns true if the class should be shown
   */
  public void setOnCheckValid(Predicate<Class<?>> checker) {
    this.onCheckValid = checker;
  }

  /**
   * Constructor for ClassSelectionComboBox.
   *
   * @param packageName the package name to search for classes
   * @param superClass  the superclass to filter classes by
   */
  public ClassSelectionDropDownList(String prompt, String packageName, Class<?> superClass) {
    super();
    setPromptText(prompt);

    Reflections reflections = new Reflections(packageName);
    allClasses = reflections.getSubTypesOf(superClass).stream()
        .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()) && !clazz.isInterface())
        .collect(Collectors.toList());

    setupSelectionRestoration();
  }
  

  private void setupSelectionRestoration() {
    String[] previouslySelected = {null};
    EventHandler<ActionEvent>[] actionEventHandler = new EventHandler[1];
    boolean[] isRestoringSelection = {false};

    setupShowingListener(previouslySelected, actionEventHandler, isRestoringSelection);
    setupActionEventHandler(actionEventHandler, isRestoringSelection);
  }

  private void setupShowingListener(String[] previouslySelected,
      EventHandler<ActionEvent>[] actionEventHandler,
      boolean[] isRestoringSelection) {
    showingProperty().addListener((obs, oldVal, isShowing) -> {
      if (isShowing) {
        handleDropdownShowing(previouslySelected, actionEventHandler);
      } else {
        handleDropdownHiding(previouslySelected, actionEventHandler, isRestoringSelection);
      }
    });
  }

  private void handleDropdownShowing(String[] previouslySelected,
      EventHandler<ActionEvent>[] actionEventHandler) {
    previouslySelected[0] = getSelectionModel().getSelectedItem();
    actionEventHandler[0] = getOnAction();
    setOnAction(null);
    refreshDropdownItems();
  }

  private void refreshDropdownItems() {
    getItems().clear();
    allClasses.stream()
        .filter(onCheckValid)
        .map(Class::getSimpleName)
        .sorted()
        .forEach(getItems()::add);
  }

  private void handleDropdownHiding(String[] previouslySelected,
      EventHandler<ActionEvent>[] actionEventHandler,
      boolean[] isRestoringSelection) {
    isRestoringSelection[0] = true;
    restorePreviousSelection(previouslySelected);
    setOnAction(actionEventHandler[0]);
    isRestoringSelection[0] = false;
  }

  private void restorePreviousSelection(String[] previouslySelected) {
    if (getSelectionModel().getSelectedItem() == null) {
      if (previouslySelected[0] != null) {
        getSelectionModel().select(previouslySelected[0]);
      } else {
        setPromptText(getPromptText());
      }
    }
  }

  private void setupActionEventHandler(EventHandler<ActionEvent>[] actionEventHandler,
      boolean[] isRestoringSelection) {
    addEventHandler(ActionEvent.ACTION, event -> {
      if (!isRestoringSelection[0] && actionEventHandler[0] != null) {
        actionEventHandler[0].handle(event);
      }
    });
  }

}

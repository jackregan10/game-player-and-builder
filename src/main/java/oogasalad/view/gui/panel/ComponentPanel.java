package oogasalad.view.gui.panel;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import oogasalad.model.builder.actions.BuilderPanelEditAction;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.Transform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import oogasalad.model.builder.UndoRedoManager;
import oogasalad.model.serialization.serializable.SerializedField;
import oogasalad.view.gui.deserializedfieldinput.DeserializedFieldInput;
import oogasalad.view.gui.dropDown.ClassSelectionDropDownMenu;
import oogasalad.view.gui.hoverInfo.HoverInfo;
import oogasalad.view.gui.error.PopUpError;

/**
 * ComponentPanel is a VBox container that displays and manages UI panels for GameComponents inside
 * a GameObject, allowing expand/collapse per component.
 *
 * @author Hsuan-Kai Liao
 */
public class ComponentPanel extends VBox {

  private static final String COLLAPSED_ARROW = "▶";
  private static final String EXPANDED_ARROW = "▼";
  private static final String DELETE_BUTTON = "-";
  private static final String COMPONENT_PACKAGE_NAME = "oogasalad.model.engine.component";

  private final Map<String, Boolean> expandState;
  private final ClassSelectionDropDownMenu addComponentMenuButton;
  private GameObject currentObject;

  /**
   * Constructor for creating a component panel to hold the current objects' components Constructor
   * for ComponentPanel.
   */
  public ComponentPanel() {
    super(10);
    expandState = new HashMap<>();
    addComponentMenuButton = new ClassSelectionDropDownMenu(
        GameConfig.getText("addComponentButton"), COMPONENT_PACKAGE_NAME, GameComponent.class);
    addComponentMenuButton.setOnCheckValid(clazz -> {
      if (currentObject != null) {
        return !currentObject.hasComponent((Class<? extends GameComponent>) clazz);
      }
      return false;
    });

    setPadding(new Insets(10));
    // TODO: Set the style in the style sheet
    setStyle("-fx-background-color: #f4f4f4; -fx-padding: 10px;");
    setAlignment(Pos.TOP_CENTER);
    configureAddComponentMenu();
  }

  /**
   * Set the GameObject and rebuild all component panels.
   *
   * @param object GameObject to visualize
   */
  public void setGameObject(GameObject object) {
    currentObject = object;
    getChildren().clear();

    if (object != null) {
      Set<String> componentNames = object.getAllComponents().values().stream()
          .map(c -> c.getClass().getSimpleName())
          .collect(Collectors.toSet());
      expandState.keySet().retainAll(componentNames);

      List<GameComponent> sortedComponents = new ArrayList<>(object.getAllComponents().values());
      sortedComponents.sort(Comparator.comparing(c -> c.componentTag().ordinal()));

      for (GameComponent component : sortedComponents) {
        getChildren().add(createComponentBox(component));
      }
    }

    requestSync();

    getChildren().add(addComponentMenuButton);
  }

  /**
   * Update the UI of all DeserializedFieldInput elements to match model state.
   */
  public void requestSync() {
    for (Node node : getChildren()) {
      syncNodeRecursively(node);
    }
  }

  private void syncNodeRecursively(Node node) {
    if (node instanceof DeserializedFieldInput<?> input) {
      input.onSync();
    } else if (node instanceof Parent parent) {
      for (Node child : parent.getChildrenUnmodifiable()) {
        syncNodeRecursively(child);
      }
    }
  }

  /**
   * Retrieve the current expand/collapse state of all components.
   *
   * @return Map of component names to expansion booleans.
   */
  public Map<String, Boolean> getExpandState() {
    return new HashMap<>(expandState);
  }

  /**
   * Update the expand/collapse state of UI panels without rebuilding.
   *
   * @param state Map of component name to expand state
   */
  public void setExpandState(Map<String, Boolean> state) {
    expandState.clear();
    expandState.putAll(state);

    for (Node node : getChildren()) {
      updateComponentExpandState(node);
    }
  }

  private void configureAddComponentMenu() {
    addComponentMenuButton.setOnClassSelected(clazz -> handleAddComponent(clazz));
  }

  @SuppressWarnings("unchecked")
  private void handleAddComponent(Class<?> clazz) {
    try {
      Class<? extends GameComponent> componentClass = (Class<? extends GameComponent>) clazz;
      if (currentObject != null && !currentObject.hasComponent(componentClass)) {
        addComponentToCurrentObject(componentClass);
      }
    } catch (ClassCastException e) {
      String message = MessageFormat.format("Invalid component class: {0}", e.getMessage());
      GameConfig.LOGGER.error(message);
      PopUpError.showError(message);
    }
  }


  private void addComponentToCurrentObject(Class<? extends GameComponent> componentClass) {
    currentObject.addComponent(componentClass);
    setGameObject(currentObject);

    UndoRedoManager.addAction(new BuilderPanelEditAction<>(
        () -> {
          currentObject.removeComponent(componentClass);
          setGameObject(currentObject);
        },
        () -> redoAddComponent(componentClass)
    ));
  }

  private void redoAddComponent(Class<? extends GameComponent> componentClass) {
    try {
      GameComponent redoComponent = componentClass.getDeclaredConstructor().newInstance();
      currentObject.addComponent(redoComponent);
      setGameObject(currentObject);
    } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
             NoSuchMethodException e) {
      throw new ComponentCreationException("Error getting constructor for class: " + componentClass,
          e);
    }
  }

  private VBox createComponentBox(GameComponent component) {
    String compName = component.getClass().getSimpleName();
    List<SerializedField> fields = component.getSerializedFields();
    boolean isExpandable = !fields.isEmpty();

    VBox container = new VBox(5);
    container.setPadding(new Insets(5));
    container.setAlignment(Pos.TOP_CENTER);
    container.setStyle("-fx-background-color: #e4e4e4;");

    HBox header = createHeader(compName, component, isExpandable);
    VBox contentBox = createContentBox(fields);

    boolean shouldExpand = expandState.getOrDefault(compName, false);
    contentBox.setVisible(isExpandable && shouldExpand);
    contentBox.setManaged(isExpandable && shouldExpand);

    if (isExpandable) {
      header.setOnMouseClicked(e -> toggleContent(contentBox, compName, header));
    }

    container.getChildren().addAll(header, contentBox);
    return container;
  }

  private HBox createHeader(String compName, GameComponent component, boolean isExpandable) {
    Label arrow = new Label(isExpandable
        ? (expandState.getOrDefault(compName, false) ? EXPANDED_ARROW : COLLAPSED_ARROW)
        : "");
    arrow.setStyle("-fx-font-size: 14px;");

    Label title = new Label(compName);
    title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
    HBox spacer = new HBox();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    HBox header;

    if (component.getClass().equals(Transform.class)) {
      header = new HBox(5, arrow, title, spacer);
    } else {
      Button deleteButton = new Button(DELETE_BUTTON);
      deleteButton.setStyle(
          "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: transparent;");
      deleteButton.setOnAction(e -> {

        GameComponent componentCopy = deepCopyComponent(component);
        Class<? extends GameComponent> compClass = component.getClass();
        currentObject.removeComponent(component.getClass());
        setGameObject(currentObject);

        UndoRedoManager.addAction(new BuilderPanelEditAction<>(
            () -> {
              currentObject.addComponent(componentCopy);
              setGameObject(currentObject);
            },
            () -> {
              currentObject.removeComponent(compClass);
              setGameObject(currentObject);
            }
        ));
      });
      header = new HBox(5, arrow, title, spacer, deleteButton);
    }
    header.setAlignment(Pos.CENTER_LEFT);
    header.setCursor(isExpandable ? Cursor.HAND : Cursor.DEFAULT);

    HoverInfo.installHoverTooltip(header, component);

    return header;
  }

  private GameComponent deepCopyComponent(GameComponent component) {
    try {
      GameComponent copy = component.getClass().getDeclaredConstructor().newInstance();

      for (SerializedField field : component.getSerializedFields()) {
        Object value = field.getValue();

        SerializedField copyField = findSerializedField(copy, field.getFieldName());

        if (copyField != null) {
          copyField.setValue(value);
        }
      }
      return copy;
    } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
             NoSuchMethodException e) {
      throw new ComponentCreationException("Error getting constructor for component: " + component,
          e);
    }
  }

  private SerializedField findSerializedField(GameComponent component, String fieldName) {
    for (SerializedField field : component.getSerializedFields()) {
      if (field.getFieldName().equals(fieldName)) {
        return field;
      }
    }
    return null;
  }

  private VBox createContentBox(List<SerializedField> fields) {
    VBox contentBox = new VBox(5);
    for (SerializedField field : fields) {
      DeserializedFieldInput<?> fieldInput = DeserializedFieldInput.createFieldUI(field);
      fieldInput.setBindInformation(() -> currentObject);
      contentBox.getChildren().add(fieldInput);
    }
    return contentBox;
  }

  private void toggleContent(VBox contentBox, String compName, HBox header) {
    boolean nowExpanded = !contentBox.isVisible();
    expandState.put(compName, nowExpanded);
    contentBox.setVisible(nowExpanded);
    contentBox.setManaged(nowExpanded);

    header.getChildren().forEach(node -> {
      if (node instanceof Label label &&
          (COLLAPSED_ARROW.equals(label.getText()) || EXPANDED_ARROW.equals(label.getText()))) {
        label.setText(nowExpanded ? EXPANDED_ARROW : COLLAPSED_ARROW);
      }
    });
  }

  private void updateComponentExpandState(Node node) {
    VBox compBox = asVBox(node);
    int two = 2;
    if (compBox == null || compBox.getChildren().size() < two) {
      return;
    }

    HBox header = asHBox(compBox.getChildren().get(0));
    VBox contentBox = asVBox(compBox.getChildren().get(1));
    if (header == null || contentBox == null) {
      return;
    }

    Label arrow = findArrowLabel(header);
    String componentName = findComponentName(header);

    if (arrow == null || componentName == null) {
      return;
    }

    applyExpandState(arrow, contentBox, componentName);
  }

  private VBox asVBox(Node node) {
    return (node instanceof VBox vbox) ? vbox : null;
  }

  private HBox asHBox(Node node) {
    return (node instanceof HBox hbox) ? hbox : null;
  }

  private Label findArrowLabel(HBox header) {
    for (Node node : header.getChildren()) {
      if (node instanceof Label label) {
        String text = label.getText();
        if (COLLAPSED_ARROW.equals(text) || EXPANDED_ARROW.equals(text)) {
          return label;
        }
      }
    }
    return null;
  }

  private String findComponentName(HBox header) {
    for (Node node : header.getChildren()) {
      if (node instanceof Label label) {
        String text = label.getText();
        if (!COLLAPSED_ARROW.equals(text) && !EXPANDED_ARROW.equals(text)) {
          return text;
        }
      }
    }
    return null;
  }

  private void applyExpandState(Label arrow, VBox contentBox, String componentName) {
    boolean expand = expandState.getOrDefault(componentName, false);
    contentBox.setVisible(expand);
    contentBox.setManaged(expand);
    arrow.setText(expand ? EXPANDED_ARROW : COLLAPSED_ARROW);
  }
}

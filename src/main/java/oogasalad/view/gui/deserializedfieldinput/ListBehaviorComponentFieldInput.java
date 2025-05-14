package oogasalad.view.gui.deserializedfieldinput;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.subComponent.behavior.BehaviorComponent;
import oogasalad.model.serialization.serializable.SerializedField;
import oogasalad.model.serialization.serializable.TypeRef;
import oogasalad.model.serialization.serializer.Serializer;
import oogasalad.model.serialization.serializer.SerializerException;
import oogasalad.view.gui.box.ListBox;
import oogasalad.view.gui.dropDown.ClassSelectionDropDownList;
import oogasalad.view.gui.hoverInfo.HoverInfo;
import oogasalad.view.gui.textField.StringTextField;

import java.lang.reflect.*;
import java.util.*;

/**
 * Generic GUI component for deserializing a List of BehaviorComponent elements.
 *
 * @author Hsuan-Kai Liao
 */
final class ListBehaviorComponentFieldInput extends
    DeserializedFieldInput<List<BehaviorComponent<?>>> {

  private static final Type VOID_TYPE = new TypeRef<Void>() {
  }.getType();

  private final Map<HBox, BehaviorComponent<?>> containerBehaviorComponentMap = new HashMap<>();

  private String componentPackage;
  private Class<?> componentClass;
  private ListBox<BehaviorComponent<?>> listBox;
  private SerializedField field;

  @Override
  protected LabelPosition defaultLabelPosition() {
    return LabelPosition.NONE;
  }

  @Override
  protected Node showGUI(SerializedField field) {
    this.field = field;

    // NOTE: I have to do this way (not lambda) to get through pipelines
    this.listBox = new ListBox<>(bc -> generateItemField(bc), hBox -> decomposeRow(hBox),
        () -> null);
    this.listBox.setOnListUpdated(
        updatedList -> field.setValue(updatedList.stream().filter(Objects::nonNull).toList()));

    Label label = new Label(formatFieldName(field.getFieldName()));
    VBox root = new VBox(5, label, listBox);
    root.setAlignment(Pos.TOP_LEFT);
    return root;
  }

  @Override
  public void onSync() {
    List<BehaviorComponent<?>> values =
        field.getValue() != null ? (List<BehaviorComponent<?>>) field.getValue()
            : new ArrayList<>();
    listBox.setValues(values);

    for (HBox hBox : containerBehaviorComponentMap.keySet()) {
      validateComponentRequirements(hBox);
    }
  }

  /**
   * Validates a specific BehaviorComponent and updates the corresponding row's color.
   */
  // TODO: Use CSS
  private void validateComponentRequirements(HBox row) {
    BehaviorComponent<?> component = containerBehaviorComponentMap.get(row);

    if (getBindInformation() == null) {
      return;
    }

    Set<Class<? extends GameComponent>> currentComponentTypes =
        ((GameObject) getBindInformation()).getAllComponents().keySet();

    String color =
        currentComponentTypes.containsAll(component.requiredComponents()) ? "transparent" : "red";
    row.getChildren().getFirst()
        .setStyle("-fx-border-color: " + color + "; -fx-border-width: 2px;");
  }

  /**
   * Set the local Behavior Component type, this should be either BehaviorAction or
   * BehaviorConstraint
   *
   * @param componentClass   the component Class
   * @param componentPackage the package that contains all the concrete subclasses of the component
   *                         class specified above
   */
  public void setBehaviorComponentType(Class<?> componentClass, String componentPackage) {
    this.componentClass = componentClass;
    this.componentPackage = componentPackage;
  }

  private HBox generateItemField(BehaviorComponent<?> initialComponent) {
    ClassSelectionDropDownList dropDown = getClassSelectionDropDownList();

    StringTextField paramField = new StringTextField("", GameConfig.getText("paramPrompt"));

    HBox row = new HBox(5, dropDown, paramField);
    row.setAlignment(Pos.CENTER_LEFT);
    HBox.setHgrow(paramField, Priority.ALWAYS);

    if (initialComponent != null) {
      initializeComponentRow(initialComponent, dropDown, paramField);
    }
    setDropDownAction(dropDown, row, paramField);
    setDropDownCheck(dropDown);
    paramField.setChangeListener(e -> updateParamField(row, paramField));

    containerBehaviorComponentMap.put(row, initialComponent);
    return row;
  }

  /**
   * Create each dropdown list item and add hover tooltip
   */
  private ClassSelectionDropDownList getClassSelectionDropDownList() {
    ClassSelectionDropDownList dropDown = new ClassSelectionDropDownList(
        GameConfig.getText("selectPrompt"), componentPackage, componentClass);

    // give each popup‐list cell a tooltip
    dropDown.setCellFactory(listView -> new ListCell<>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
          setTooltip(null);
        } else {
          setText(item);
          // use HoverInfo helper class
          HoverInfo.installHoverTooltip(this, item);
        }
      }
    });
    return dropDown;
  }

  private BehaviorComponent<?> decomposeRow(HBox row) {
    return containerBehaviorComponentMap.get(row);
  }

  private void initializeComponentRow(BehaviorComponent<?> component,
      ClassSelectionDropDownList dropDown, StringTextField paramField) {
    dropDown.setValue(component.getClass().getSimpleName());

    if (TypeRef.findGenericTypeArgument(component.getClass()).equals(VOID_TYPE)) {
      hide(paramField);
    } else {
      SerializedField param = component.getSerializedFields().getFirst();
      paramField.setText(Optional.ofNullable(param.getValue()).map(Object::toString).orElse(""));
    }
  }

  private void setDropDownAction(ClassSelectionDropDownList dropDown, HBox row,
      StringTextField paramField) {
    dropDown.setOnAction(e -> {
      String className = dropDown.getValue();
      BehaviorComponent<?> newComponent = instantiateComponent(className);
      if (newComponent == null) {
        return;
      }

      containerBehaviorComponentMap.put(row, newComponent);
      validateComponentRequirements(row);

      SerializedField param = newComponent.getSerializedFields().getFirst();
      if (TypeRef.findGenericTypeArgument(newComponent.getClass()).equals(VOID_TYPE)) {
        hide(paramField);
      } else {
        show(paramField);
        paramField.setText(Optional.ofNullable(param.getValue()).map(Object::toString).orElse(""));
      }

      field.setValue(listBox.getValues());
    });
  }

  private void setDropDownCheck(ClassSelectionDropDownList dropDown) {
    dropDown.setOnCheckValid(clazz -> {
      try {
        BehaviorComponent<?> instance = (BehaviorComponent<?>) clazz.getDeclaredConstructor()
            .newInstance();
        return ((GameObject) getBindInformation()).getAllComponents().keySet()
            .containsAll(instance.requiredComponents());
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
               NoSuchMethodException e) {
        return false;
      }
    });
  }

  private boolean updateParamField(HBox row, StringTextField paramField) {
    return Optional.ofNullable(containerBehaviorComponentMap.get(row)).map(component -> {
      SerializedField param = component.getSerializedFields().getFirst();
      return updateParameter(param, paramField.getText());
    }).orElse(false);
  }

  private BehaviorComponent<?> instantiateComponent(String className) {
    try {
      Class<?> clazz = Class.forName(componentPackage + "." + className);
      return (BehaviorComponent<?>) componentClass.cast(
          clazz.getDeclaredConstructor().newInstance());
    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
             | IllegalAccessException | InvocationTargetException e) {
      return null;
    }
  }

  private boolean updateParameter(SerializedField param, String newVal) {
    try {
      Serializer.deserialize(param, newVal);
      return true;
    } catch (SerializerException e) {
      return false;
    }
  }

  private void hide(StringTextField field) {
    field.setVisible(false);
    field.setManaged(false);
  }

  private void show(StringTextField field) {
    field.setVisible(true);
    field.setManaged(true);
  }

  private String formatFieldName(String fieldName) {
    return Character.toUpperCase(fieldName.charAt(0)) +
        fieldName.substring(1).replaceAll("([a-z])([A-Z])", "$1 $2");
  }
}

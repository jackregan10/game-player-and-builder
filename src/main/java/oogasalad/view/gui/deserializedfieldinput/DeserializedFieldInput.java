package oogasalad.view.gui.deserializedfieldinput;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import oogasalad.model.config.GameConfig;
import oogasalad.model.serialization.serializable.SerializedField;
import oogasalad.model.serialization.serializable.TypeRef;
import oogasalad.model.serialization.serializable.TypeReferenceException;
import oogasalad.view.gui.label.FieldLabel;
import org.reflections.Reflections;

/**
 * Abstract class for creating a GUI element for a SerializedField.
 *
 * @param <T> the type of the input
 * @author Hsuan-Kai Liao
 */
public abstract class DeserializedFieldInput<T> extends HBox {

  private static final String SERIALIZER_PACKAGE = DeserializedFieldInput.class.getPackage().getName();
  private static final int DEFAULT_SPACING = 10;
  private static final Map<Type, Supplier<DeserializedFieldInput<?>>> initializerPool = new HashMap<>();

  private Label label;
  private Node guiNode;
  private Supplier<Object> info = () -> null;

  static {
    Reflections reflections = new Reflections(SERIALIZER_PACKAGE);
    for (Class<? extends DeserializedFieldInput> clazz : reflections.getSubTypesOf(DeserializedFieldInput.class)) {
      if (!Modifier.isAbstract(clazz.getModifiers())) {
        Type type = TypeRef.findGenericTypeArgument(clazz);
        initializerPool.put(type, () -> {
          try {
            return clazz.getDeclaredConstructor().newInstance();
          } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new TypeReferenceException(GameConfig.getText("errorCreateDeserializedUI", clazz.getName()), e);
          }
        });
      }
    }
  }

  /**
   * The label position indicator enum.
   */
  public enum LabelPosition {
    NONE,
    TOP,
    LEFT,
  }

  /**
   * Constructor for DeserializedFieldUI.
   */
  protected DeserializedFieldInput() {
    super(DEFAULT_SPACING);
    HBox.setHgrow(this, Priority.ALWAYS);
    setStyle("-fx-background-color: #dadada; -fx-padding: 5;");
  }

  void initGUI(SerializedField field) {
    this.guiNode = showGUI(field);
    HBox.setHgrow(guiNode, Priority.ALWAYS);
    this.label = new FieldLabel(field.getFieldName());
    setLabelPosition(defaultLabelPosition());
  }

  /**
   * Create and initialize the appropriate input UI component for a field.
   */
  public static DeserializedFieldInput<?> createFieldUI(SerializedField field) {
    Supplier<DeserializedFieldInput<?>> initializer = initializerPool.get(field.getFieldType());

    if (initializer == null) {
      GameConfig.LOGGER.error(GameConfig.getText("errorCreateDeserializedUI"), field.getFieldType());
      return new DeserializedFieldInput<Void>() {
        @Override
        protected LabelPosition defaultLabelPosition() {
          return null;
        }

        @Override
        protected Node showGUI(SerializedField field) {
          return new Label("ERROR" + field.getFieldName());
        }

        @Override
        public void onSync() {
          // DO NOTHING
        }
      };
    }

    DeserializedFieldInput<?> fieldUI = initializer.get();
    fieldUI.initGUI(field);
    return fieldUI;
  }

  /**
   * Create and initialize the appropriate input UI component for a field with a forced input type.
   */
  public static DeserializedFieldInput<?> createFieldUI(SerializedField field, Type type) {
    DeserializedFieldInput<?> fieldUI = initializerPool.get(type).get();
    fieldUI.initGUI(field);
    return fieldUI;
  }

  /**
   * Bind an object information to this UI.
   */
  public void setBindInformation(Supplier<Object> info) {
    this.info = info;
  }

  /**
   * Get thr object information bound to this UI.
   */
  public Object getBindInformation() {
    return info.get();
  }

  /**
   * Sets the label position for this input.
   *
   * @param position the label position (NONE, TOP, LEFT)
   */
  public void setLabelPosition(LabelPosition position) {
    getChildren().clear();
    switch (position) {
      case NONE -> getChildren().add(guiNode);
      case TOP -> {
        VBox vBox = new VBox(DEFAULT_SPACING, label, guiNode);
        HBox.setHgrow(vBox, Priority.ALWAYS);
        getChildren().add(vBox);
      }
      case LEFT -> getChildren().addAll(label, guiNode);
    }
  }

  /**
   * Set the default label position of this UI widget.
   */
  protected abstract LabelPosition defaultLabelPosition();

  /**
   * Create a GUI element for the given SerializedField.
   *
   * @param field the SerializedField to create a GUI element for
   * @return a Node representing the GUI element
   */
  protected abstract Node showGUI(SerializedField field);

  /**
   * This is called when the outside requires UI to synchronize the actual value of the
   * SerializedField.
   */
  public abstract void onSync();
}

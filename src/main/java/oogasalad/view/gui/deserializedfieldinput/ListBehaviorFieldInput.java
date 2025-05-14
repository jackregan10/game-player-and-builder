package oogasalad.view.gui.deserializedfieldinput;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.subComponent.behavior.Behavior;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.model.engine.subComponent.behavior.BehaviorComponent;
import oogasalad.model.engine.subComponent.behavior.BehaviorConstraint;
import oogasalad.model.serialization.serializable.SerializedField;

import java.util.ArrayList;
import java.util.List;
import oogasalad.model.serialization.serializable.TypeRef;
import oogasalad.view.gui.box.ListBox;
import oogasalad.view.gui.label.EditableLabel;

/**
 * GUI component for editing a List<Behavior> field. Each Behavior can be dynamically added,
 * removed, and edited.
 *
 * @author Hsuan-Kai Liao
 */
final class ListBehaviorFieldInput extends DeserializedFieldInput<List<Behavior>> {

  private static final Type LIST_BEHAVIOR_COMPONENT_TYPE = new TypeRef<List<BehaviorComponent<?>>>() {
  }.getType();
  private static final Class<?> CONSTRAINT_CLASS = BehaviorConstraint.class;
  private static final Class<?> ACTION_CLASS = BehaviorAction.class;
  private static final String CONSTRAINT_PACKAGE = "oogasalad.model.engine.subComponent.behavior.constraint";
  private static final String ACTION_PACKAGE = "oogasalad.model.engine.subComponent.behavior.action";

  // TODO: make this outside in the css
  private static final String BEHAVIOR_ROW_STYLE = """
      -fx-background-color: #AcAcAc;
      -fx-background-radius: 6;
      -fx-padding: 8;
      """;

  private static final Map<HBox, Behavior> containerBehaviorMap = new HashMap<>();

  private ListBox<Behavior> listBox;
  private SerializedField field;

  @Override
  protected LabelPosition defaultLabelPosition() {
    return LabelPosition.TOP;
  }

  @Override
  protected Node showGUI(SerializedField field) {
    this.field = field;

    // NOTE: I have to do this way (not lambda) to get through pipelines
    this.listBox = new ListBox<>(behavior -> generateItemField(behavior),
        hBox -> decomposeRow(hBox), () -> new Behavior(
        GameConfig.getText("newBehavior")));
    this.listBox.setOnListUpdated(field::setValue);

    VBox root = new VBox(5, listBox);
    root.setAlignment(Pos.TOP_LEFT);
    return root;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void onSync() {
    List<Behavior> values =
        field.getValue() != null ? (List<Behavior>) field.getValue() : new ArrayList<>();
    listBox.setValues(values);
  }

  private HBox generateItemField(Behavior behavior) {
    List<SerializedField> paramList = behavior.getSerializedFields();
    EditableLabel nameLabel = new EditableLabel(behavior.getName());
    nameLabel.setCursor(Cursor.HAND);
    nameLabel.setCompleteListener(this::onSync);
    nameLabel.setChangeListener(newName -> {
      paramList.getFirst().setValue(newName);
      return true;
    });

    ListBehaviorComponentFieldInput constraintList = (ListBehaviorComponentFieldInput) DeserializedFieldInput.createFieldUI(
        paramList.get(1),
        LIST_BEHAVIOR_COMPONENT_TYPE);
    constraintList.setBehaviorComponentType(CONSTRAINT_CLASS, CONSTRAINT_PACKAGE);
    constraintList.setBindInformation(this::getBindInformation);
    constraintList.onSync();
    ListBehaviorComponentFieldInput actionList = (ListBehaviorComponentFieldInput) DeserializedFieldInput.createFieldUI(
        paramList.get(2),
        LIST_BEHAVIOR_COMPONENT_TYPE);
    actionList.setBehaviorComponentType(ACTION_CLASS, ACTION_PACKAGE);
    actionList.setBindInformation(this::getBindInformation);
    actionList.onSync();

    VBox container = new VBox(5, nameLabel, constraintList, actionList);
    HBox row = new HBox(5, container);

    row.setAlignment(Pos.CENTER_LEFT);
    HBox.setHgrow(container, Priority.ALWAYS);
    row.setStyle(BEHAVIOR_ROW_STYLE);

    containerBehaviorMap.put(row, behavior);
    return row;
  }

  private Behavior decomposeRow(HBox row) {
    return containerBehaviorMap.get(row);
  }
}

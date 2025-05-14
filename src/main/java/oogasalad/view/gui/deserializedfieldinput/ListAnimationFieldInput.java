package oogasalad.view.gui.deserializedfieldinput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import oogasalad.model.engine.subComponent.animation.Animation;
import oogasalad.model.serialization.serializable.SerializedField;
import oogasalad.view.gui.box.ListBox;
import oogasalad.view.gui.label.EditableLabel;

/**
 * A GUI component for editing a serialized List<String> field.
 *
 * @author Hsuan-Kai Liao
 */
final class ListAnimationFieldInput extends DeserializedFieldInput<List<Animation>> {

  private static final String NAME_FIELD = "name";
  // TODO: make this outside in the css
  private static final String ANIMATION_ROW_STYLE = """
      -fx-background-color: #AcAcAc;
      -fx-background-radius: 6;
      -fx-padding: 8;
      """;

  private final Map<HBox, Animation> containerAnimationMap = new HashMap<>();

  private ListBox<Animation> listBox;
  private SerializedField field;

  @Override
  protected LabelPosition defaultLabelPosition() {
    return LabelPosition.TOP;
  }

  @Override
  protected Node showGUI(SerializedField field) {
    this.field = field;

    // NOTE: I have to do this way (not lambda) to get through pipelines
    this.listBox = new ListBox<>(anim -> generateRow(anim), hBox -> decomposeRow(hBox), () -> new Animation());
    this.listBox.setOnListUpdated(field::setValue);

    VBox root = new VBox(5, listBox);
    HBox.setHgrow(root, Priority.ALWAYS);
    root.setAlignment(Pos.TOP_LEFT);
    return root;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void onSync() {
    List<Animation> values = field.getValue() != null ? (List<Animation>) field.getValue() : new ArrayList<>();
    listBox.setValues(values);
  }

  private HBox generateRow(Animation animation) {
    EditableLabel nameLabel = new EditableLabel(animation.getName());
    nameLabel.setCursor(Cursor.HAND);
    nameLabel.setCompleteListener(this::onSync);
    nameLabel.setChangeListener(newName -> {
      animation.getSerializedField(NAME_FIELD).setValue(newName);
      return true;
    });

    VBox container = new VBox(5, nameLabel);
    for (SerializedField field : animation.getSerializedFields()) {
      if (field.getFieldName().equals(NAME_FIELD)) { continue; }
      DeserializedFieldInput<?> fieldInput = DeserializedFieldInput.createFieldUI(field);
      fieldInput.onSync();
      container.getChildren().add(fieldInput);
    }

    HBox row = new HBox(5, container);
    row.setAlignment(Pos.CENTER_LEFT);
    HBox.setHgrow(container, Priority.ALWAYS);
    row.setStyle(ANIMATION_ROW_STYLE);

    containerAnimationMap.put(row, animation);
    return row;
  }

  private Animation decomposeRow(HBox row) {
    return containerAnimationMap.get(row);
  }

}

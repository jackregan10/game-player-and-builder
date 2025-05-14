package oogasalad.view.gui.deserializedfieldinput;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import oogasalad.model.config.GameConfig;
import oogasalad.model.serialization.serializable.SerializedField;
import oogasalad.view.gui.box.ListBox;
import oogasalad.view.gui.textField.StringTextField;

import java.util.ArrayList;
import java.util.List;

/**
 * A GUI component for editing a serialized List<String> field.
 */
final class ListStringFieldInput extends DeserializedFieldInput<List<String>> {

  private SerializedField field;
  private ListBox<String> listBox;

  @Override
  protected LabelPosition defaultLabelPosition() {
    return LabelPosition.TOP;
  }

  @Override
  protected Node showGUI(SerializedField field) {
    this.field = field;

    // NOTE: I have to do this way (not lambda) to get through pipelines
    this.listBox = new ListBox<>(s -> generateItemField(s), hBox -> decomposeRow(hBox), () -> "");
    this.listBox.setOnListUpdated(field::setValue);

    VBox root = new VBox(5, listBox);
    HBox.setHgrow(root, Priority.ALWAYS);
    return root;
  }

  @Override
  public void onSync() {
    List<String> values = field.getValue() != null ? (List<String>) field.getValue() : new ArrayList<>();
    listBox.setValues(values);
  }

  private HBox generateItemField(String initialValue) {
    StringTextField textField = new StringTextField(initialValue, GameConfig.getText("ellipsesText"));
    textField.setChangeListener( e -> updateField());

    HBox itemBox = new HBox(5, textField);
    itemBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
    HBox.setHgrow(textField, Priority.ALWAYS);
    return itemBox;
  }

  private String decomposeRow(HBox row) {
    return ((StringTextField) row.getChildren().getFirst()).getText();
  }

  private boolean updateField() {
    field.setValue(listBox.getValues());
    return true;
  }
}

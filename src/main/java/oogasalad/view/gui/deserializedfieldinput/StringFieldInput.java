package oogasalad.view.gui.deserializedfieldinput;

import javafx.scene.Node;
import oogasalad.model.config.GameConfig;
import oogasalad.model.serialization.serializable.SerializedField;
import oogasalad.view.gui.textField.StringTextField;

/**
 * A class that provides a JavaFX UI component for editing a string field in a serialized object. It
 * extends the DeserializedFieldUI class and implements the showGUI method to create the UI. The UI
 * consists of a label and a text field that only accepts string values.
 *
 * @author Hsuan-Kai Liao
 */
final class StringFieldInput extends DeserializedFieldInput<String> {

  private StringTextField textField;
  private SerializedField field;

  @Override
  protected LabelPosition defaultLabelPosition() {
    return LabelPosition.LEFT;
  }

  @Override
  protected Node showGUI(SerializedField field) {
    this.field = field;
    this.textField = new StringTextField((String) field.getValue(), GameConfig.getText(
        "ellipsesText"));

    textField.setChangeListener(newVal -> {
      field.setValue(newVal);
      return true;
    });

    return textField;
  }

  @Override
  public void onSync() {
    textField.setText((String) field.getValue());
  }
}

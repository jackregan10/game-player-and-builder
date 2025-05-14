package oogasalad.view.gui.deserializedfieldinput;

import javafx.scene.Node;
import oogasalad.model.serialization.serializable.SerializedField;
import oogasalad.view.gui.textField.IntegerTextField;

/**
 * A class that provides a JavaFX UI component for editing an integer field in a serialized object. It
 * extends the DeserializedFieldUI class and implements the showGUI method to create the UI. The UI
 * consists of a label and a text field that only accepts double values.
 *
 * @author Hsuan-Kai Liao
 */
final class IntegerFieldInput extends DeserializedFieldInput<Integer> {

  private IntegerTextField integerText;
  private SerializedField field;

  @Override
  protected LabelPosition defaultLabelPosition() {
    return LabelPosition.LEFT;
  }

  @Override
  protected Node showGUI(SerializedField field) {
    this.field = field;
    this.integerText = new IntegerTextField((Integer) field.getValue(), "Enter a double value");
    integerText.setChangeListener(newVal -> {
    // Make a text field for the field that only accepts double values
      new IntegerTextField((Integer) field.getValue(), "Enter a double value");
      field.setValue(newVal);
      return true;
  });
    return integerText;
  }

  @Override
  public void onSync() {
    integerText.setText(field.getValue().toString());
  }

}


package oogasalad.view.gui.deserializedfieldinput;

import javafx.scene.Node;
import oogasalad.model.config.GameConfig;
import oogasalad.model.serialization.serializable.SerializedField;
import oogasalad.view.gui.textField.DoubleTextField;

/**
 * A class that provides a JavaFX UI component for editing a double field in a serialized object. It
 * extends the DeserializedFieldUI class and implements the showGUI method to create the UI. The UI
 * consists of a label and a text field that only accepts double values.
 */
final class DoubleFieldInput extends DeserializedFieldInput<Double> {

  private DoubleTextField doubleField;
  private SerializedField field;


  @Override
  protected LabelPosition defaultLabelPosition() {
    return LabelPosition.LEFT;
  }

  @Override
  protected Node showGUI(SerializedField field) {
    this.field = field;
    doubleField = new DoubleTextField((Double) field.getValue(), GameConfig.getText("enterDoublePrompt"));
    doubleField.setChangeListener(newVal -> {
      field.setValue(newVal);
      return true;
    });

    return doubleField;
  }

  @Override
  public void onSync() {
    doubleField.setText(field.getValue().toString());
  }

}

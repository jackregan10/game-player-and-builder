package oogasalad.view.gui.deserializedfieldinput;

import javafx.scene.Node;
import oogasalad.model.resource.ResourcePath;
import oogasalad.model.serialization.serializable.SerializedField;
import oogasalad.view.gui.textField.PathTextField;

/**
 * UI Widget for image path row in sprite renderer
 */
public class ResourcePathFieldInput extends DeserializedFieldInput<ResourcePath> {

  private PathTextField textField;
  private SerializedField field;

  @Override
  protected LabelPosition defaultLabelPosition() {
    return LabelPosition.LEFT;
  }

  @Override
  protected Node showGUI(SerializedField field) {
    this.field = field;

    textField = new PathTextField(((ResourcePath) field.getValue()).getPath(), "...");
    textField.setChangeListener(newVal -> {
      ((ResourcePath) field.getValue()).setPath(textField.getText());
      return true;
    });

    return textField;
  }

  @Override
  public void onSync() {
    textField.setText(((ResourcePath) field.getValue()).getPath());
  }
}

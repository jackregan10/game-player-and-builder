package oogasalad.view.gui.deserializedfieldinput;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import oogasalad.model.builder.UndoRedoManager;
import oogasalad.model.builder.actions.BuilderPanelEditAction;
import oogasalad.model.serialization.serializable.SerializedField;

/**
 * A class that provides a JavaFX UI component for editing a boolean field in a serialized object. It
 * extends the DeserializedFieldUI class and implements the showGUI method to create the UI. The UI
 * consists of a label and a text field that only accepts string values.
 *
 * @author Hsuan-Kai Liao
 */
final class BooleanFieldInput extends DeserializedFieldInput<Boolean> {

  private CheckBox checkBox;
  private SerializedField field;

  @Override
  protected LabelPosition defaultLabelPosition() {
    return LabelPosition.LEFT;
  }

  @Override
  protected Node showGUI(SerializedField field) {
    this.field = field;
    checkBox = createCheckBox(field);
    setUpFieldSyncAndUndo(field, checkBox);
    return checkBox;
  }

  private CheckBox createCheckBox(SerializedField field) {
    CheckBox box = new CheckBox();
    box.setSelected(Boolean.TRUE.equals(field.getValue()));
    return box;
  }

  private void setUpFieldSyncAndUndo(SerializedField field, CheckBox box) {
    box.selectedProperty().addListener((obs, oldVal, newVal) -> {
      updateField(field, newVal);
      if (!oldVal.equals(newVal)) {
        addUndoRedo(field, box, oldVal, newVal);
      }
    });
  }

  private void updateField(SerializedField field, Boolean newVal) {
    field.setValue(newVal);
  }

  private void addUndoRedo(SerializedField field, CheckBox box, Boolean oldVal, Boolean newVal) {
    UndoRedoManager.addAction(new BuilderPanelEditAction<>(
        () -> applyValue(field, box, oldVal),
        () -> applyValue(field, box, newVal)
    ));
  }

  private void applyValue(SerializedField field, CheckBox box, Boolean value) {
    field.setValue(value);
    box.setSelected(value);
  }



  @Override
  public void onSync() {
    checkBox.setSelected((Boolean) field.getValue());
  }

}

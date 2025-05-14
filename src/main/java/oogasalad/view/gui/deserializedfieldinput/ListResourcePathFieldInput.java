package oogasalad.view.gui.deserializedfieldinput;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import oogasalad.model.resource.ResourcePath;
import oogasalad.model.serialization.serializable.SerializedField;
import oogasalad.view.gui.box.ListBox;
import oogasalad.view.gui.textField.PathTextField;

/**
 * A GUI component for editing a serialized List<ResourcePath> field.
 *
 * @author Hsuan-Kai Liao
 */
public class ListResourcePathFieldInput extends DeserializedFieldInput<List<ResourcePath>> {

  private SerializedField field;
  private ListBox<ResourcePath> listBox;

  @Override
  protected LabelPosition defaultLabelPosition() {
    return LabelPosition.TOP;
  }

  @Override
  protected Node showGUI(SerializedField field) {
    this.field = field;

    this.listBox = new ListBox<>(this::generateRow, this::decomposeRow, ResourcePath::new);
    this.listBox.setOnListUpdated(field::setValue);

    VBox root = new VBox(5, listBox);
    HBox.setHgrow(root, Priority.ALWAYS);
    return root;
  }

  @Override
  public void onSync() {
    List<ResourcePath> values = field.getValue() != null ? (List<ResourcePath>) field.getValue() : new ArrayList<>();
    listBox.setValues(values);
  }

  @SuppressWarnings("PMD.UnusedPrivateMethod")
  private HBox generateRow(ResourcePath initialValue) {
    PathTextField pathField = new PathTextField(initialValue.getPath(), "...");
    pathField.setChangeListener(e -> updateField());

    return pathField;
  }

  @SuppressWarnings("PMD.UnusedPrivateMethod")
  private ResourcePath decomposeRow(HBox row) {
    ResourcePath resourcePath = new ResourcePath();
    resourcePath.setPath(((PathTextField) row).getText());
    return resourcePath;
  }

  private boolean updateField() {
    field.setValue(listBox.getValues());
    return true;
  }
}

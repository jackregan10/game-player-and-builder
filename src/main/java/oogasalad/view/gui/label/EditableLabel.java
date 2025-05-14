package oogasalad.view.gui.label;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.function.Predicate;
import oogasalad.model.config.GameConfig;
import oogasalad.view.gui.textField.StringTextField;

/**
 * An editable label class that would be easy to "double click" to edit values.
 */
public class EditableLabel extends Label {

  private static final int editClickCount = 2;

  private Predicate<String> changeListener;
  private Runnable completeListener;
  private boolean isEditable = true;

  /**
   * Create an editable Label for easy to rename.
   * @param text initialText
   */
  public EditableLabel(String text) {
    super(text);
    this.setText(text);
    this.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
    this.setMaxWidth(Double.MAX_VALUE);
    this.setPadding(new Insets(0, 10, 0, 10));
    HBox.setHgrow(this, Priority.ALWAYS);

    this.setOnMouseClicked(event -> {
      if (isEditable && event.getClickCount() == editClickCount) {
        createNewTextField();
      }
    });
  }

  /**
   * Set the changeListener for this label to validate input texts.
   * @param listener the change listener.
   */
  public void setChangeListener(Predicate<String> listener) {
    this.changeListener = listener;
  }

  /**
   * Set the completeListener for this label for the completion callback of the input.
   * @param listener the complete listener
   */
  public void setCompleteListener(Runnable listener) {
    this.completeListener = listener;
  }

  /**
   * Set whether the label is editable or not.
   */
  public void setEditable(boolean editable) {
    this.isEditable = editable;
  }

  private void createNewTextField() {
    StringTextField editField = new StringTextField(getText(), GameConfig.getText("ellipsesText"));
    editField.setAlignment(this.getAlignment());
    editField.setChangeListener(this.changeListener);

    editField.focusedProperty().addListener((obs, oldVal, newVal) -> {
      if (!newVal && completeListener != null) {
        completeListener.run();
      }
    });

    this.setText(null);
    this.setGraphic(editField);
    editField.requestFocus();
    editField.selectAll();
  }
}

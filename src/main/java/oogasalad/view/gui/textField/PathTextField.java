package oogasalad.view.gui.textField;

import java.util.function.Predicate;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * PathTextField is a display for the file path
 */

public class PathTextField extends HBox {

  private static final String FILE_CHOOSER_ICON = "📁";
  private StringTextField textField;

  /**
   * Creates a PathTextField with the given initial path and prompt text.
   * It also includes a button for opening a file dialog to select a file path.
   *
   * @param initialPath The initial file path to display in the text field.
   * @param promptText  The prompt text for the text field.
   */
  public PathTextField(String initialPath, String promptText) {
    textField = new StringTextField(initialPath, promptText);

    Button fileChooserButton = new Button(FILE_CHOOSER_ICON);
    fileChooserButton.setOnMouseClicked(this::openFileDialog);

    this.setSpacing(10);
    this.setAlignment(Pos.CENTER_LEFT);
    this.getChildren().addAll(textField, fileChooserButton);
  }

  /**
   * Opens the file dialog to allow the user to select a file.
   * If a file is selected, the path is updated in the text field.
   * If the dialog is canceled, no changes are made.
   *
   * @param event Mouse click event triggered by the user.
   */
  @SuppressWarnings("PMD.UnusedFormalParameter")
  private void openFileDialog(MouseEvent event) {
    Stage stage = (Stage) this.getScene().getWindow();

    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Path To A File", "*.*"));

    File selectedFile = fileChooser.showOpenDialog(stage);

    if (selectedFile != null) {
      String selectedFilePath = selectedFile.getAbsolutePath();
      setText(selectedFilePath);
    }
  }

  /**
   * Sets the text of the text field inside the PathTextField.
   *
   * @param text The text to set in the text field.
   */
  public void setText(String text) {
    textField.setText(text);
  }

  /**
   * Gets the current text from the text field inside the PathTextField.
   *
   * @return The current text in the text field.
   */
  public String getText() {
    return textField.getText();
  }

  /**
   * Set the change listener to this field.
   *
   * @param listener the predicate listener to be subscribed.
   */
  public void setChangeListener(Predicate<String> listener) {
    textField.setChangeListener(listener);
  }
}

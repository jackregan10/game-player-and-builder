package oogasalad.view.gui.error;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import oogasalad.model.config.GameConfig;

/**
 * Provides utility methods for displaying error messages in a pop-up window using JavaFX Alerts.
 * The error messages are displayed inside a scrollable {@link TextArea} to prevent truncation.
 */
public class PopUpError {

  /**
   * Displays an error message in a pop-up alert dialog. Uses a scrollable {@link TextArea} to
   * ensure long error messages are fully visible.
   *
   * @param message the error message to display
   */
  public static void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(GameConfig.getText("error"));
    alert.setHeaderText(GameConfig.getText("anErrorOccurred"));

    // Create a scrollable TextArea to handle long messages
    TextArea textArea = new TextArea(message);
    textArea.setEditable(false);
    textArea.setWrapText(true);

    textArea.setMaxWidth(Double.MAX_VALUE);
    textArea.setMaxHeight(Double.MAX_VALUE);
    GridPane.setVgrow(textArea, Priority.ALWAYS);
    GridPane.setHgrow(textArea, Priority.ALWAYS);

    GridPane gridPane = new GridPane();
    gridPane.add(textArea, 0, 0);

    alert.getDialogPane().setContent(gridPane);
    alert.showAndWait();
  }
}

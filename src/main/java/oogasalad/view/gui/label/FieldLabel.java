package oogasalad.view.gui.label;

import javafx.scene.control.Label;

/**
 * The label that can change any camal-case letter into real words phrase.
 *
 * @author Hsuan-Kai Liao
 */
public class FieldLabel extends Label {

  /**
   * The constructor for create a fieldLabel.
   */
  public FieldLabel(String fieldName) {
    super();
    String formatedName = reformatText(fieldName);
    this.setText(formatedName);
  }

  private String reformatText(String text) {
    String formatedText = text.replaceAll("([a-z])([A-Z])", "$1 $2");
    return formatedText.substring(0, 1).toUpperCase() + formatedText.substring(1);
  }
}

package oogasalad.view.gui.textField;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

/**
 * A TextField that only accepts valid double values.
 *
 * @author Hsuan-Kai Liao
 */
public class IntegerTextField extends TypedTextField<Integer> {

  /**
   * Create a double user interface input that handles add listener easier.
   */
  public IntegerTextField(Integer initialValue, String prompt) {
    super(initialValue, prompt);
  }

  @Override
  protected TextFormatter<Integer> createTextFormatter(Integer initialValue) {
    return new TextFormatter<>(
        new IntegerStringConverter(),
        initialValue,
        change -> {
          String newText = change.getControlNewText();
          boolean textMatch = newText.matches("\\d+");
          if (newText.isEmpty() || textMatch) {
            return change;
          }
          return null;
        }
    );
  }


  @Override
  protected Integer parseText(String text) {
    if (text.isEmpty()) {
      return 0;
    }
    return Integer.parseInt(text);
  }
}

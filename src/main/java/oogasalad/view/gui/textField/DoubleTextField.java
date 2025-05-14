package oogasalad.view.gui.textField;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.DoubleStringConverter;

/**
 * A TextField that only accepts valid double values.
 *
 * @author Hsuan-Kai Liao
 */
public class DoubleTextField extends TypedTextField<Double> {

  private static final String DASH = "-";
  private static final String DOT = ".";

  /**
   * Create a double user interface input that handles add listener easier.
   */
  public DoubleTextField(Double initialValue, String prompt) {
    super(initialValue, prompt);
  }

  @Override
  protected TextFormatter<Double> createTextFormatter(Double initialValue) {
    return new TextFormatter<>(
        new DoubleStringConverter(),
        initialValue,
        change -> {
          String newText = change.getControlNewText();
          if (newText.isEmpty() || newText.equals(DASH) || newText.equals(DOT)) {
            return change;
          }
          try {
            Double.parseDouble(newText);
            return change;
          } catch (NumberFormatException e) {
            return null;
          }
        }
    );
  }

  @Override
  protected Double parseText(String text) {
    if (text.isEmpty() || text.equals(DASH) || text.equals(DOT)) {
      return 0.0;
    }
    return Double.parseDouble(text);
  }
}

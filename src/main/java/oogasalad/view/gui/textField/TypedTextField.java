package oogasalad.view.gui.textField;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.Clipboard;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A generic TextField that supports type-safe input with validation and change listeners.
 *
 * @param <T> The type of the input value (e.g., Double, Integer, String)
 * @author Hsuan-Kai Liao
 */
public abstract class TypedTextField<T> extends TextField {

  private T originalValue;
  private Predicate<T> changeListener;

  /**
   * Create a basic TextField abstractions.
   *
   * @param initialValue the initialValue of the text input
   * @param prompt       the showing prompt
   */
  public TypedTextField(T initialValue, String prompt) {
    super();
    setPromptText(prompt);
    this.originalValue = initialValue;

    setTextFormatter(createTextFormatter(initialValue));
    setOnKeyPressed(this::handleKeyEvents);
    textProperty().addListener((obs, oldVal, newVal) -> handleTextChange(newVal));
    focusedProperty().addListener((obs, oldVal, newVal) -> handleFocusChange(oldVal, newVal));

    // Listen for paste event
    setOnKeyReleased(this::handleKeyReleased);
  }

  /**
   * Make the text formatter to restrict user input.
   *
   * @param initialValue the given initial value
   * @return the textFormatter restriction
   */
  protected abstract TextFormatter<T> createTextFormatter(T initialValue);

  private void handleKeyEvents(KeyEvent e) {
    if (e.getCode() == KeyCode.ESCAPE) {
      onCancel();
    }
    e.consume();
  }

  private void handleTextChange(String newText) {
    T parsed = parseText(newText);
    if (parsed != null && onSubmit(parsed)) {
      setStyle("-fx-text-fill: black;");
    } else {
      setStyle("-fx-text-fill: red;");
    }
  }

  private void handleFocusChange(boolean wasFocused, boolean isNowFocused) {
    if (!isNowFocused) {
      onComplete();
    } else if (!wasFocused) {
      originalValue = parseText(getText());
    }
  }

  private void handleKeyReleased(KeyEvent e) {
    if (e.getCode() == KeyCode.V && e.isControlDown()) {
      Clipboard clipboard = Clipboard.getSystemClipboard();
      if (clipboard.hasString()) {
        String clipboardText = clipboard.getString();

        T parsed;
        try {
          parsed = parseText(clipboardText);
          setText(parsed.toString());
        } catch (IllegalArgumentException ignored) {
          return;
        }
      }
    }
  }


  /**
   * Set the change listener to this field.
   *
   * @param listener the predicate listener to be subscribed.
   */
  public void setChangeListener(Predicate<T> listener) {
    this.changeListener = listener;
  }

  private void onCancel() {
    setText(originalValue == null ? "" : originalValue.toString());
    if (changeListener != null) {
      changeListener.test(originalValue);
    }
  }

  private boolean onSubmit(T newValue) {
    if (changeListener != null) {
      return changeListener.test(newValue);
    }
    return true;
  }

  private void onComplete() {
    T newValue = parseText(getText());
    if (!Objects.equals(originalValue, newValue) && changeListener != null) {
      if (changeListener.test(newValue)) {
        originalValue = newValue;
      } else {
        onCancel();
      }
    }
  }

  /**
   * Parses the current text to the appropriate type T.
   *
   * @param text the string to parse
   * @return the parsed value, or null if invalid
   */
  protected abstract T parseText(String text);
}

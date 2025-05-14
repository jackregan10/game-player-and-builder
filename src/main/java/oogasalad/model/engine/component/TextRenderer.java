package oogasalad.model.engine.component;

import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.serialization.serializable.SerializableField;

/**
 * A component that renders text on screen as part of a GameObject. This component uses JavaFX
 * styling and snapshotting to draw styled text to the canvas. Example uses: - Game over messages -
 * HUD elements like scores or instructions - Button labels or static screen prompts
 *
 * @author Logan Dracos
 */
public class TextRenderer extends GameComponent implements Renderable {

  @SerializableField
  private String text;

  @SerializableField
  private String styleClass = "defaultText";

  @SerializableField
  private boolean isCentered = false;

  @SerializableField
  private int fontSize;

  @Override
  public ComponentTag componentTag() {
    return ComponentTag.TEXT;
  }

  /**
   * Gets the text currently displayed by this component.
   *
   * @return the current display string
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the text that will be displayed on screen.
   *
   * @param newText the updated string
   */
  public void setText(String newText) {
    this.text = newText;
  }

  /**
   * Gets the name of the JavaFX style class applied to this text.
   *
   * @return a string like "title", "buttonLabel", etc.
   */
  public String getStyleClass() {
    return styleClass;
  }

  /**
   * Updates the style class to apply a different CSS rule to this text.
   *
   * @param styleClass a valid JavaFX style class name
   */
  public void setStyleClass(String styleClass) {
    this.styleClass = styleClass;
  }

  /**
   * @return - whether the text object should be centered or not
   */
  public boolean isCentered() {
    return isCentered;
  }

  /**
   * @param centered - Boolean of whether the text should be centered on the page or not
   */
  public void setCentered(boolean centered) {
    this.isCentered = centered;
  }

  /**
   * @return - Returns current font size of text renderer
   */
  public int getFontSize() {
    return fontSize;
  }

  /**
   * @param fontSize - New font size
   */
  public void setFontSize(int fontSize) {
    this.fontSize = fontSize;
  }
}

package oogasalad.model.engine.component;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TextRendererTest {

  private TextRenderer textRenderer;

  @BeforeEach
  void setup() {
    textRenderer = new TextRenderer();
  }

  @Test
  void GetText_DefaultValue_NullReturned() {
    assertNull(textRenderer.getText());
  }

  @Test
  void SetText_ValidString_ValueUpdated() {
    textRenderer.setText("Hello World");
    assertEquals("Hello World", textRenderer.getText());
  }

  @Test
  void SetText_NullString_ValueUpdatedToNull() {
    textRenderer.setText(null);
    assertNull(textRenderer.getText());
  }

  @Test
  void GetStyleClass_DefaultValue_ReturnsDefault() {
    assertEquals("defaultText", textRenderer.getStyleClass());
  }

  @Test
  void SetStyleClass_ValidString_StyleClassUpdated() {
    textRenderer.setStyleClass("titleText");
    assertEquals("titleText", textRenderer.getStyleClass());
  }

  @Test
  void SetStyleClass_NullValue_StyleClassUpdatedToNull() {
    textRenderer.setStyleClass(null);
    assertNull(textRenderer.getStyleClass());
  }

  @Test
  void IsCentered_DefaultValue_ReturnsFalse() {
    assertFalse(textRenderer.isCentered());
  }

  @Test
  void SetCentered_TrueValue_ValueUpdated() {
    textRenderer.setCentered(true);
    assertTrue(textRenderer.isCentered());
  }

  @Test
  void SetCentered_FalseValue_ValueUpdated() {
    textRenderer.setCentered(false);
    assertFalse(textRenderer.isCentered());
  }

  @Test
  void GetFontSize_DefaultValue_ReturnsZero() {
    assertEquals(0, textRenderer.getFontSize());
  }

  @Test
  void SetFontSize_ValidPositiveValue_FontSizeUpdated() {
    textRenderer.setFontSize(18);
    assertEquals(18, textRenderer.getFontSize());
  }

  @Test
  void SetFontSize_ZeroValue_FontSizeUpdatedToZero() {
    textRenderer.setFontSize(0);
    assertEquals(0, textRenderer.getFontSize());
  }

  @Test
  void SetFontSize_NegativeValue_FontSizeUpdatedToNegative() {
    textRenderer.setFontSize(-10);
    assertEquals(-10, textRenderer.getFontSize());
  }
}

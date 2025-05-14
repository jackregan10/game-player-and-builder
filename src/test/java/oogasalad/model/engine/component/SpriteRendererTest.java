package oogasalad.model.engine.component;

import java.util.List;
import oogasalad.model.resource.ResourcePath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpriteRendererTest {

  private SpriteRenderer spriteRenderer;

  @BeforeEach
  void setup() {
    spriteRenderer = new SpriteRenderer();
    ResourcePath path1 = new ResourcePath();
    path1.setPath("img1.png");
    spriteRenderer.setImagePath(path1);
  }

  @Test
  void getOffsetX_DefaultState_ReturnsZero() {
    assertEquals(0.0, spriteRenderer.getOffsetX());
  }

  @Test
  void setOffsetX_ValidValue_UpdatesOffset() {
    spriteRenderer.setOffsetX(25.5);
    assertEquals(25.5, spriteRenderer.getOffsetX());
  }

  @Test
  void getOffsetY_DefaultState_ReturnsZero() {
    assertEquals(0.0, spriteRenderer.getOffsetY());
  }

  @Test
  void setOffsetY_ValidValue_UpdatesOffset() {
    spriteRenderer.setOffsetY(-12.75);
    assertEquals(-12.75, spriteRenderer.getOffsetY());
  }

  @Test
  void getImagePath_InitialState_ReturnsFirstImagePath() {
    assertEquals("img1.png", spriteRenderer.getImagePath().getPath());
  }

  @Test
  void componentTag_Always_ReturnsRenderTag() {
    assertEquals(ComponentTag.SPRITE, spriteRenderer.componentTag());
  }
}

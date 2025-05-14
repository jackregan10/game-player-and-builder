package oogasalad.model.engine.component;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransformTest {

  @Test
  void getX_DefaultState_ReturnsZero() {
    Transform t = new Transform();
    assertEquals(0.0, t.getX());
  }

  @Test
  void setX_ValidValue_ValueIsUpdated() {
    Transform t = new Transform();
    t.setX(42.5);
    assertEquals(42.5, t.getX());
  }

  @Test
  void getY_DefaultState_ReturnsZero() {
    Transform t = new Transform();
    assertEquals(0.0, t.getY());
  }

  @Test
  void setY_ValidValue_ValueIsUpdated() {
    Transform t = new Transform();
    t.setY(-17.3);
    assertEquals(-17.3, t.getY());
  }

  @Test
  void getRotation_DefaultState_ReturnsZero() {
    Transform t = new Transform();
    assertEquals(0.0, t.getRotation());
  }

  @Test
  void setRotation_ValidValue_ValueIsUpdated() {
    Transform t = new Transform();
    t.setRotation(90.0);
    assertEquals(90.0, t.getRotation());
  }

  @Test
  void getScaleX_DefaultState_ReturnsZero() {
    Transform t = new Transform();
    assertEquals(0.0, t.getScaleX());
  }

  @Test
  void setScaleX_ValidValue_ValueIsUpdated() {
    Transform t = new Transform();
    t.setScaleX(2.5);
    assertEquals(2.5, t.getScaleX());
  }

  @Test
  void getScaleY_DefaultState_ReturnsZero() {
    Transform t = new Transform();
    assertEquals(0.0, t.getScaleY());
  }

  @Test
  void setScaleY_ValidValue_ValueIsUpdated() {
    Transform t = new Transform();
    t.setScaleY(3.0);
    assertEquals(3.0, t.getScaleY());
  }

  @Test
  void componentTag_Always_ReturnsTransformTag() {
    Transform t = new Transform();
    assertEquals(ComponentTag.TRANSFORM, t.componentTag());
  }
}

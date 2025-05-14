package oogasalad.model.engine.component;

import oogasalad.model.engine.architecture.GameObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhysicsHandlerTest {

  private PhysicsHandler physics;
  private Transform transform;

  @BeforeEach
  void setup() {
    GameObject player = new GameObject("player");
    physics = new PhysicsHandler();
    player.addComponent(physics);
    transform = new Transform();
    player.addComponent(transform);
    physics.awake();
  }


  @Test
  void applyImpulseX_ValidMass_ChangesVelocityX() {
    physics.setVelocityX(0);
    physics.applyImpulseX(10);
    assertEquals(10.0, physics.getVelocityX());
  }

  @Test
  void applyImpulseY_ValidMass_ChangesVelocityY() {
    physics.setVelocityY(0);
    physics.applyImpulseY(-5);
    assertEquals(-5.0, physics.getVelocityY());
  }

  @Test
  void applyImpulseX_ZeroMass_NoCrashAndInfinity() {
    physics.setVelocityX(0);
    physics.applyImpulseX(10);

    physics.setVelocityX(0);
    physics.setAccelerationX(0);
    physics.setVelocityY(0);
    physics.setAccelerationY(0);

    try {
      var massField = PhysicsHandler.class.getDeclaredField("mass");
      massField.setAccessible(true);
      massField.set(physics, 0.0);

      physics.applyImpulseX(10);
      assertTrue(Double.isInfinite(physics.getVelocityX()) || Double.isNaN(physics.getVelocityX()));
    } catch (Exception e) {
      fail("Reflection error during mass manipulation");
    }
  }

  @Test
  void setGetVelocityX_ValidValue_ReturnsSame() {
    physics.setVelocityX(3.2);
    assertEquals(3.2, physics.getVelocityX());
  }

  @Test
  void setGetVelocityY_ValidValue_ReturnsSame() {
    physics.setVelocityY(-1.5);
    assertEquals(-1.5, physics.getVelocityY());
  }

  @Test
  void setGetAccelerationX_ValidValue_ReturnsSame() {
    physics.setAccelerationX(0.8);
    assertEquals(0.8, physics.getAccelerationX());
  }

  @Test
  void setGetAccelerationY_ValidValue_ReturnsSame() {
    physics.setAccelerationY(-0.9);
    assertEquals(-0.9, physics.getAccelerationY());
  }

  @Test
  void update_WithAcceleration_UpdatesPosition() {
    transform.setX(0);
    transform.setY(0);
    physics.setVelocityX(1);
    physics.setVelocityY(2);
    physics.setAccelerationX(1);
    physics.setAccelerationY(-1);

    physics.update(1.0);

    assertEquals(2.0, transform.getX());
    assertEquals(1.0, transform.getY());
  }

  @Test
  void update_NoAcceleration_VelocityRemainsConstant() {
    transform.setX(5);
    transform.setY(10);
    physics.setVelocityX(3);
    physics.setVelocityY(-2);
    physics.setAccelerationX(0);
    physics.setAccelerationY(0);

    physics.update(2.0);

    assertEquals(11.0, transform.getX());
    assertEquals(6.0, transform.getY());
  }

  @Test
  void componentTag_Always_ReturnsPhysicsTag() {
    assertEquals(ComponentTag.PHYSICS, physics.componentTag());
  }
}

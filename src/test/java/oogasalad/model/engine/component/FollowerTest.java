package oogasalad.model.engine.component;

import java.awt.Dimension;

import oogasalad.model.engine.architecture.Game;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.engine.architecture.GameInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FollowerTest {
  private GameObject targetObj;
  private Follower follower;
  private Transform targetTransform;
  private Transform followerTransform;

  @BeforeEach
  void setup() {
    Game game = new Game();
    GameScene scene = new GameScene("TestScene");
    game.addScene(scene);

    targetObj = new GameObject("Target", "tag");
    targetTransform = targetObj.addComponent(Transform.class);
    scene.registerObject(targetObj);

    GameObject followerObj = new GameObject("Follower", "tag");
    followerTransform = followerObj.addComponent(Transform.class);
    follower = followerObj.addComponent(Follower.class);
    follower.setFollowObjectName("Target");
    scene.registerObject(followerObj);
  }

  @Test
  void Awake_WithValidTarget_OffsetsCorrectlyInitialized() {
    targetTransform.setX(10);
    targetTransform.setY(20);

    followerTransform.setX(30);
    followerTransform.setY(50);

    follower.awake();

    assertEquals(20, follower.getOffsetX(), 0.01);
    assertEquals(30, follower.getOffsetY(), 0.01);
  }

  @Test
  void Awake_WithSmoothMovement_InitializesSmoothFields() {
    targetTransform.setX(10);
    targetTransform.setY(20);
    followerTransform.setX(30);
    followerTransform.setY(50);

    follower.setSmoothMovement(true);
    targetObj.addComponent(PhysicsHandler.class);

    follower.awake();

    assertNotNull(follower.getCurrentPosition());
    assertNotNull(follower.getPreviousPosition());
    assertEquals(0.0, follower.getSpeedLimit(), 0.01);
  }

  @Test
  void Update_TargetMissingTransform_LogsErrorAndSkipsUpdate() {
    GameObject newTarget = new GameObject("Target", "tag");
    assertThrows(IllegalArgumentException.class, () -> follower.setFollowObject(newTarget));
  }

  @Test
  void Update_ValidFollowObject_PositionUpdatedWithOffset() {
    targetTransform.setX(15);
    targetTransform.setY(25);
    followerTransform.setX(20);
    followerTransform.setY(40);

    follower.awake();
    follower.update(0.016);

    assertEquals(15 + follower.getOffsetX(), followerTransform.getX(), 0.01);
    assertEquals(25 + follower.getOffsetY(), followerTransform.getY(), 0.01);
  }

  @Test
  void SmoothMovement_LargeDistance_SnapsTowardTarget() {
    // Setup: Enable smoothing and physics
    follower.setSmoothMovement(true);
    PhysicsHandler handler = targetObj.addComponent(PhysicsHandler.class);
    follower.awake();

    // First update to establish initial previousPosition
    targetTransform.setX(0);
    targetTransform.setY(0);
    followerTransform.setX(0);
    followerTransform.setY(0);
    follower.update(0.016);

    // Move the target far away to trigger smoothing
    targetTransform.setX(100);
    targetTransform.setY(0);
    handler.setVelocityX(10); // Some motion so minSpeed > 0
    handler.setVelocityY(0);
    handler.setAccelerationX(5);
    handler.setAccelerationY(0);

    follower.update(0.5); // Long enough delta to allow visible interpolation

    // Since target jumped to 100, follower should be somewhere between 0 and 100
    double x = followerTransform.getX();
    assertTrue(x > 0 && x < 100);
  }

  @Test
  void setFollowObject_ValidObject_ObjectStored() {
    follower.setFollowObject(targetObj);
    assertEquals(targetObj, follower.getFollowObject());
  }

  @Test
  void getFollowObjectName_NameSetBeforeAwake_ReturnsCorrectName() {
    Follower follower = new Follower();
    follower.setFollowObjectName("Target");
    assertEquals("Target", follower.getFollowObjectName());
  }

  @Test
  void getFollowObjectName_NameNeverSet_ReturnsNull() {
    Follower follower = new Follower();
    assertNull(follower.getFollowObjectName());
  }

  @Test
  void setOffset_ValidValues_OffsetsStored() {
    follower.setOffset(5.0, -3.0);
    assertEquals(5.0, follower.getOffsetX());
    assertEquals(-3.0, follower.getOffsetY());
  }

  @Test
  void update_FollowingTarget_UpdatesPositionCorrectly() {
    follower.setFollowObject(targetObj);
    follower.setOffset(10.0, -5.0);

    targetTransform.setX(100);
    targetTransform.setY(50);

    follower.update(0.016);

    assertEquals(110.0, followerTransform.getX());
    assertEquals(45.0, followerTransform.getY());
  }

  @Test
  void awake_ValidFollowObjectName_SetsFollowObjectWithoutError() {
    Game game = new Game();
    GameScene scene = new GameScene("FollowerScene");
    game.addScene(scene);

    GameObject target = new GameObject("Target", "tag");
    target.addComponent(Transform.class);
    scene.registerObject(target);

    GameObject followerObj = new GameObject("Follower", "tag");
    followerObj.addComponent(Transform.class);
    Follower follower = followerObj.addComponent(Follower.class);
    follower.setFollowObjectName("Target");

    assertDoesNotThrow(() -> scene.registerObject(followerObj));
  }

  @Test
  void awake_InvalidFollowObjectName_ThrowsRuntimeException() {
    Game game = new Game();
    game.setGameInfo(new GameInfo("", "", "", new Dimension(1000, 1000)));
    GameScene scene = new GameScene("FollowerScene");
    game.addScene(scene);

    GameObject camera = new GameObject("Camera", "tag");
    camera.addComponent(Transform.class);
    camera.addComponent(Camera.class);
    scene.registerObject(camera);
    camera.getComponent(Camera.class).awake();

    GameObject followerObj = new GameObject("Follower", "tag");
    Transform followTransform = followerObj.addComponent(Transform.class);
    followTransform.setX(10);
    followTransform.setY(10);
    Follower follower = followerObj.addComponent(Follower.class);
    follower.setFollowObjectName("NonExistent");
    scene.registerObject(followerObj);

    assertThrows(RuntimeException.class, () -> scene.step(1));
  }


  @Test
  void componentTag_Always_ReturnsSmoothMovementTag() {
    assertEquals(ComponentTag.FOLLOWER, follower.componentTag());
  }
}

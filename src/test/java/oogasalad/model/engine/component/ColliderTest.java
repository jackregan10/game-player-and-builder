package oogasalad.model.engine.component;

import oogasalad.model.engine.architecture.Game;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.engine.component.Collider.CollisionDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColliderTest {

  private GameObject player;
  private GameObject platform;
  private Collider playerCollider;
  private Transform playerTransform;
  private Transform platformTransform;

  @BeforeEach
  void setup() {
    Game game = new Game();
    GameScene scene = new GameScene("testScene");
    game.addScene(scene);

    player = new GameObject("Player", "player");
    platform = new GameObject("Platform", "ground");

    playerTransform = player.addComponent(Transform.class);
    platformTransform = platform.addComponent(Transform.class);

    playerCollider = player.addComponent(Collider.class);
    Collider platformCollider = platform.addComponent(Collider.class);

    playerTransform.setX(50);
    playerTransform.setY(50);
    playerTransform.setScaleX(10);
    playerTransform.setScaleY(10);

    platformTransform.setX(50);
    platformTransform.setY(60);
    platformTransform.setScaleX(10);
    platformTransform.setScaleY(10);

    scene.registerObject(player);
    scene.registerObject(platform);

    playerCollider.awake();
    platformCollider.awake();
  }

  @Test
  void componentTag_Always_ReturnsCollisionTag() {
    assertEquals(ComponentTag.COLLISION, playerCollider.componentTag());
  }

  @Test
  void Update_ColliderDoesNotOverlap_CollidedCollidersEmpty() {
    playerTransform.setY(100);
    playerCollider.update(0.016);
    assertTrue(playerCollider.getCollidedColliders().isEmpty());
  }

  @Test
  void Update_SelfCollision_Ignored() {
    playerCollider.getCollidableTags().add("player");
    playerCollider.update(0.016);
    assertTrue(playerCollider.getCollidedColliders().isEmpty());
  }

  @Test
  void Update_OverlappingWithValidTag_ColliderAdded() {
    playerCollider.getCollidableTags().add("ground");
    playerCollider.update(0.016);
    assertEquals(1, playerCollider.getCollidedColliders().size());
  }

  @Test
  void Update_OverlappingColliderWithPhysics_YPositionSnappedAndVelocityZeroed() {
    playerTransform.setY(52);
    player.addComponent(PhysicsHandler.class).setVelocityY(5);
    playerCollider.getCollidableTags().add("ground");
    playerCollider.update(0.016);
    assertEquals(60 - playerTransform.getScaleY(), playerTransform.getY());
    assertEquals(0, player.getComponent(PhysicsHandler.class).getVelocityY());
  }


  @Test
  void Update_OverlapXPosition_PlayerPushedLeft() {
    platformTransform.setY(50);
    platformTransform.setX(59);
    playerCollider.getCollidableTags().add("ground");
    playerCollider.update(0.016);
    assertTrue(playerTransform.getX() < 59);
  }

  @Test
  void Update_NoPhysicsHandler_YStillResolvedButVelocityUnaffected() {
    playerCollider.getCollidableTags().add("ground");
    playerCollider.update(0.016);
    assertEquals(60 - playerTransform.getScaleY(), playerTransform.getY());
  }


  @Test
  void update_NoPhysics_DoesNotResolveCollision() {
    double originalY = playerTransform.getY();
    playerCollider.update(1.0);
    assertEquals(originalY, playerTransform.getY());
  }

  @Test
  void collidesWith_NonCollidedTag_ReturnsFalse() {
    assertFalse(playerCollider.collidesWith("enemy"));
  }

  @Test
  void TouchingFromAbove_AlignedWithinTolerance_ReturnsTrue() {
    boolean result = playerCollider.touchingFromDirection("ground", CollisionDirection.ABOVE, 0.5);
    assertTrue(result);
  }

  @Test
  void TouchingFromBelow_ExactReverse_ReturnsTrue() {
    platformTransform.setY(40);
    boolean result = playerCollider.touchingFromDirection("ground", CollisionDirection.BELOW, 0.5);
    assertTrue(result);
  }

  @Test
  void Exact_TouchingFromLeft_ReturnsTrue() {
    platformTransform.setX(60);
    platformTransform.setY(50);
    boolean result = playerCollider.touchingFromDirection("ground", CollisionDirection.LEFT, 0.5);
    assertTrue(result);
  }

  @Test
  void Exact_TouchingFromRight_ReturnsTrue() {
    platformTransform.setX(40);
    platformTransform.setY(50);
    boolean result = playerCollider.touchingFromDirection("ground", CollisionDirection.RIGHT, 0.5);
    assertTrue(result);
  }

  @Test
  void TouchingFromAbove_OutsideTolerance_ReturnsFalse() {
    platformTransform.setY(61);
    boolean result = playerCollider.touchingFromDirection("ground", CollisionDirection.ABOVE, 0.5);
    assertFalse(result);
  }

  @Test
  void TouchingFromAbove_WrongTag_ReturnsFalse() {
    platform.setTag("obstacle");
    boolean result = playerCollider.touchingFromDirection("ground", CollisionDirection.ABOVE, 1.0);
    assertFalse(result);
  }

  @Test
  void TouchingFromAbove_ObjectHasNoCollider_ReturnsFalse() {
    platform.removeComponent(Collider.class);
    boolean result = playerCollider.touchingFromDirection("ground", CollisionDirection.ABOVE, 1.0);
    assertFalse(result);
  }

  @Test
  void resolveCollisionY_FromAbove_ResolvesToTopOfPlatform() {
    playerTransform.setY(52); // bottom = 62, platform top = 60 => Y overlap = 2
    playerTransform.setX(50); // overlap fully in X

    platformTransform.setX(50);
    platformTransform.setY(60);

    player.addComponent(PhysicsHandler.class).setVelocityY(-5);
    playerCollider.getCollidableTags().add("ground");
    platform.setTag("ground");

    playerCollider.update(0.016);

    // Expect snapped on top of platform: 60 - height(10) = 50
    assertEquals(50, playerTransform.getY(), 0.01);
    assertEquals(0, player.getComponent(PhysicsHandler.class).getVelocityY(), 0.01);
  }



  @Test
  void resolveCollisionX_FromLeft_ResolvesLeftward() {
    playerTransform.setX(59); // Player: [59,69]
    playerTransform.setY(50); // Full vertical overlap

    platformTransform.setX(60); // Platform: [60,70] → 1 unit overlap
    platformTransform.setY(50);

    player.addComponent(PhysicsHandler.class);
    playerCollider.getCollidableTags().add("ground");
    platform.setTag("ground");

    playerCollider.update(0.016);

    // Expect player to be pushed left of platform
    assertTrue(playerTransform.getX() < 59);
  }

  @Test
  void resolveCollisionX_FromRight_ResolvesRightward() {
    playerTransform.setX(41); // Player: [41,51]
    playerTransform.setY(50);

    platformTransform.setX(40); // Platform: [40,50] → 9 to 10 overlap
    platformTransform.setY(50);

    player.addComponent(PhysicsHandler.class);
    playerCollider.getCollidableTags().add("ground");
    platform.setTag("ground");

    playerCollider.update(0.016);

    // Expect player to be pushed right of platform
    assertTrue(playerTransform.getX() > 41);
  }

  @Test
  void resolveCollision_IsPermeable_NoResolution() {
    // Same positioning as above test, which moved the player
    playerTransform.setX(41);
    playerTransform.setY(50);
    platformTransform.setX(40);
    platformTransform.setY(50);

    player.addComponent(PhysicsHandler.class);
    playerCollider.getCollidableTags().add("ground");
    platform.setTag("ground");
    Collider platformCollider = platform.getComponent(Collider.class);
    platformCollider.getSerializedFields().stream().filter(f -> f.getFieldName().equals("isPermeable"))
        .findFirst()
        .orElseThrow()
        .setValue(true);

    System.out.println(playerTransform.getX());
    playerCollider.update(0.016);

    // Player should not move because platform has been set to permeable
    assertEquals(41, playerTransform.getX());
  }

  @Test
  void collidesWith_TagMatchesInCollidedColliders_ReturnsTrue() {
    playerCollider.getCollidableTags().add("ground");
    platform.setTag("ground");
    platformTransform.setX(52); // slight X overlap
    platformTransform.setY(50); // full Y overlap
    playerCollider.update(0.016);

    assertTrue(playerCollider.collidesWith("ground"));
  }

  @Test
  void collidesWith_NoMatchingTagInCollidedColliders_ReturnsFalse() {
    playerCollider.getCollidableTags().add("ground");
    platform.setTag("wrongTag");
    platformTransform.setX(52); // overlapping but wrong tag
    platformTransform.setY(50);
    playerCollider.update(0.016);

    assertFalse(playerCollider.collidesWith("ground"));
  }


  @Test
  void processCollision_SelfCollision_Ignored() {
    GameObject solo = new GameObject("Solo", "tag");
    Collider soloCollider = solo.addComponent(Collider.class);
    solo.addComponent(Transform.class).setScaleX(10);
    solo.getComponent(Transform.class).setScaleY(10);
    soloCollider.awake();

    GameScene tempScene = new GameScene("temp");
    tempScene.registerObject(solo);
    new Game().addScene(tempScene);

    soloCollider.update(1.0);

    assertFalse(soloCollider.collidesWith("tag"));
  }
}

package oogasalad.model.engine.component;

import java.util.List;
import java.util.Set;
import oogasalad.model.engine.architecture.Game;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.engine.subComponent.behavior.Behavior;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.model.engine.subComponent.behavior.BehaviorConstraint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BehaviorControllerTest {

  private BehaviorController controller;

  static class TestConstraint extends BehaviorConstraint<Boolean> {
    private final boolean value;

    public TestConstraint(boolean value) {
      this.value = value;
    }

    @Override
    protected Boolean defaultParameter() {
      return null;
    }

    @Override
    public Set<Class<? extends GameComponent>> requiredComponents() {
      return Set.of();
    }

    /**
     * Check if the constraint is met.
     *
     * @param parameter the parameter to check against
     * @return true if the constraint is met, false otherwise
     */
    @Override
    protected boolean check(Boolean parameter) {
      return value;
    }
  }

  static class FlagAction extends BehaviorAction<Boolean> {
    private boolean executed = false;

    public boolean wasExecuted() {
      return executed;
    }

    @Override
    protected Boolean defaultParameter() {
      return null;
    }

    @Override
    public Set<Class<? extends GameComponent>> requiredComponents() {
      return Set.of();
    }

    /**
     * Performs the action
     *
     * @param parameter the parameter for the action
     */
    @Override
    protected void perform(Boolean parameter) {
      executed = true;
    }
  }

  @BeforeEach
  void setup() {
    Game game = new Game();
    GameScene scene = new GameScene("BehaviorTestScene");
    game.addScene(scene);

    GameObject obj = new GameObject("TestObj", "tag");
    controller = obj.addComponent(BehaviorController.class);
    scene.registerObject(obj);
  }

  @Test
  void componentTag_Always_ReturnsBehaviorTag() {
    assertEquals(ComponentTag.BEHAVIOR, controller.componentTag());
  }

  @Test
  void addBehavior_WithDefaultConstructor_RegistersBehavior() {
    Behavior behavior = new Behavior("dummy");
    addBehavior(controller, behavior);
    assertNotNull(behavior);
  }

  @Test
  void addBehavior_WithExistingBehavior_AddsAndTriggersCorrectly() {
    FlagAction action = new FlagAction();

    Behavior behavior = new Behavior("custom");
    behavior.setBehaviorController(controller);
    List<BehaviorAction<?>> actions = (List<BehaviorAction<?>>) behavior.getSerializedField("actions").getValue();
    actions.add(action);
    List<BehaviorConstraint<?>> constraints = (List<BehaviorConstraint<?>>) behavior.getSerializedField("constraints").getValue();
    constraints.add(new TestConstraint(true));

    addBehavior(controller, behavior);
    controller.update(1.0);

    assertTrue(action.wasExecuted());
  }

  @Test
  void update_BehaviorFailsConstraint_DoesNotTriggerAction() {
    FlagAction action = new FlagAction();

    Behavior behavior = new Behavior("custom");
    addBehavior(controller, behavior);
    addAction(behavior, action);
    addConstraint(behavior, new TestConstraint(false));

    controller.update(1.0);

    assertFalse(action.wasExecuted());
  }

  @Test
  void update_NoConstraints_AlwaysExecutesAction() {
    FlagAction action = new FlagAction();

    Behavior behavior = new Behavior("custom");
    addBehavior(controller, behavior);
    addAction(behavior, action);

    controller.update(1.0);

    assertTrue(action.wasExecuted());
  }

  @Test
  void addSameBehaviorMultipleTimes_OnlyAddedOnce() {
    Behavior behavior = new Behavior("shared");
    behavior.setBehaviorController(controller);
    addBehavior(controller, behavior);
    addBehavior(controller, behavior);

    FlagAction action = new FlagAction();
    addAction(behavior, action);

    controller.update(1.0);

    assertTrue(action.wasExecuted());
  }

  @Test
  void awake_WithPreAttachedBehavior_InitializesBehaviorControllerAndAwakens() {
    // Setup flag to track if awake was called
    class AwakeTrackingBehavior extends Behavior {
      boolean awakened = false;

      public AwakeTrackingBehavior() {
        super("AwakeTrack");
      }

      @Override
      public void awake() {
        awakened = true;
      }
    }

    AwakeTrackingBehavior behavior = new AwakeTrackingBehavior();
    addBehavior(controller, behavior);

    // Now manually call awake
    controller.awake();

    assertTrue(behavior.awakened, "Behavior.awake() should have been called");
    assertEquals(controller, behavior.getController(), "BehaviorController should be set");
  }

  private void addAction(Behavior behavior, BehaviorAction<?> action) {
    getActions(behavior).add(action);
  }

  private void addConstraint(Behavior behavior, BehaviorConstraint<?> constraint) {
    getConstraints(behavior).add(constraint);
  }

  private void addBehavior(BehaviorController controller, Behavior behavior) {
    getBehaviors(controller).add(behavior);
  }
  
  private List<Behavior> getBehaviors(BehaviorController controller) {
    return (List<Behavior>) controller.getSerializedField("behaviors").getValue();
  }


  private List<BehaviorAction<?>> getActions(Behavior behavior) {
    return (List<BehaviorAction<?>>) behavior.getSerializedField("actions").getValue();
  }

  private List<BehaviorConstraint<?>> getConstraints(Behavior behavior) {
    return (List<BehaviorConstraint<?>>) behavior.getSerializedField("constraints").getValue();
  }
}

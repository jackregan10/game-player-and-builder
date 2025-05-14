package oogasalad.model.engine.subComponent.behavior;

import oogasalad.model.engine.subComponent.behavior.action.ChangeGameSceneAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChangeGameSceneActionTest extends BehaviorBaseTest {

  private ChangeGameSceneAction action;

  @Override
  public void customSetUp() {
    getGame().setLevelOrder(
        java.util.List.of(getScene1().getName(), getScene2().getName())
    );
    action = new ChangeGameSceneAction();
    addAction(getBehavior1(), action);
  }

  @Test
  public void onPerform_parameterMinusOne_advancesToNextLevel() {
    assertEquals(getScene1().getName(), getGame().getCurrentScene().getName());

    action.onPerform(-1);
    assertEquals(getScene2().getName(), getGame().getCurrentScene().getName());
  }

  @Test
  public void onPerform_parameterOne_goesToLevelOne() {
    action.onPerform(1);
    assertEquals(getScene2().getName(), getGame().getCurrentScene().getName());
  }

  @Test
  public void onPerform_invalidIndex_throwsException() {
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      action.onPerform(99);
    });

    assertTrue(e.getMessage().contains("Invalid level index"));
  }
}

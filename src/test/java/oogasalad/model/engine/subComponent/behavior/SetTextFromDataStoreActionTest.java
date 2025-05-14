package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import oogasalad.model.engine.subComponent.behavior.action.SetTextFromDataStoreAction;
import oogasalad.model.engine.component.TextRenderer;

public class SetTextFromDataStoreActionTest extends BehaviorBaseTest {

  private SetTextFromDataStoreAction action;

  @Override
  public void customSetUp() {
    // Add TextRenderer to object
    TextRenderer textRenderer = new TextRenderer();
    textRenderer.setText("Initial: ");
    getObj1().addComponent(textRenderer);

    // Add action to behavior
    action = new SetTextFromDataStoreAction();
    addAction(getBehavior1(), action);
  }

  @Test
  public void onPerform_validKey_setsTextCorrectly() {
    action.onPerform("Object1.scaleX");
    TextRenderer renderer = getObj1().getComponent(TextRenderer.class);
    assertEquals("Initial: 100", renderer.getText());
  }

  @Test
  public void onPerform_invalidKey_throwsException() {
    Exception exception = assertThrows(RuntimeException.class, () -> action.onPerform("Object1.key"));
    System.out.println(exception.getMessage());
    assertTrue(exception.getMessage().contains("Failed to retrieve or set value from key"));
  }
}
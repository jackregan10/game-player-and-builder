package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;

import oogasalad.model.engine.subComponent.behavior.action.SetTextFromSavedStoreAction;
import oogasalad.model.engine.component.TextRenderer;
import oogasalad.model.engine.datastore.ScriptableDataStore;
import org.junit.jupiter.api.Test;

public class SetTextFromSavedStoreActionTest extends BehaviorBaseTest {

  private SetTextFromSavedStoreAction action;

  @Override
  public void customSetUp() {
    // Add TextRenderer to the object
    TextRenderer textRenderer = new TextRenderer();
    textRenderer.setText("Saved: ");
    getObj1().addComponent(textRenderer);


    ScriptableDataStore dataStore = getGame().getDataStore();
    dataStore.setScene(getGame().getCurrentScene());
    dataStore.set("score.total", 150);

    // Create and attach the action
    action = new SetTextFromSavedStoreAction();
    addAction(getBehavior1(), action);
  }

  @Test
  public void onPerform_savedKey_setsTextCorrectly() {
    action.onPerform("score.total");
    TextRenderer renderer = getObj1().getComponent(TextRenderer.class);
    assertEquals("Saved: 150", renderer.getText());
  }

  @Test
  public void onPerform_missingKey_throwsException() {
    Exception exception = assertThrows(RuntimeException.class, () -> {
      action.onPerform("missing.key");
    });
    assertTrue(exception.getMessage().contains("Failed to retrieve or set value from key: 'missing.key'"));
  }
}

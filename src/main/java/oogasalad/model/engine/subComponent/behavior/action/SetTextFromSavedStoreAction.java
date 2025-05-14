package oogasalad.model.engine.subComponent.behavior.action;

import java.util.function.BiConsumer;
import oogasalad.model.engine.component.TextRenderer;
import oogasalad.model.engine.datastore.MissingKeyException;
import oogasalad.model.engine.datastore.ScriptableDataStore;

/**
 * An action that sets the text of a {@link TextRenderer} component based on a *manually saved*
 * value in the {@link ScriptableDataStore}'s long-term `data` map. Unlike
 * SetTextFromDataStoreAction, this action will not fetch live scene fields or apply ops. It reads
 * the value exactly as it was stored using `set(...)` into the store. Example: { "name":
 * "SetSavedScoreText", "actions": [ { "name": "SetTextFromSavedStoreAction", "parameter":
 * "player.score" } ] }
 *
 * @author Logan Dracos
 */
public class SetTextFromSavedStoreAction extends SetTextFromAction {
  @Override
  protected BiConsumer<TextRenderer, String> provideSetter() {
    return (textRenderer, rawKey) -> {
      try {
        if (getPrefix() == null) {
          setPrefix(textRenderer.getText());
        }

        Object value = getDataStore().get(rawKey); // Direct access to saved value

        textRenderer.setText(value != null ? getPrefix() + value : "null");

      } catch (MissingKeyException | ClassCastException e) {
        throw new IllegalArgumentException(
            "Failed to retrieve or set value from key: '" + rawKey + "'", e);
      }
    };
  }
}

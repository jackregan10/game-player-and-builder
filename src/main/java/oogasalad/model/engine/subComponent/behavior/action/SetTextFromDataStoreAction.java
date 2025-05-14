package oogasalad.model.engine.subComponent.behavior.action;

import java.util.function.BiConsumer;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.datastore.MissingKeyException;
import oogasalad.model.engine.component.TextRenderer;
import oogasalad.model.engine.datastore.ScriptableDataStore;


/**
 * An action that sets the text of a {@link TextRenderer} component based on a value retrieved from
 * the {@link ScriptableDataStore}.
 * <p>
 * This action expects its input parameter to be a {@code String} key path (e.g., "player.score")
 * used to fetch a live value from the data store during execution. The {@link TextRenderer} will
 * keep its initial text as a prefix and the fetched value will then be appended
 * <p>
 * The /save tag will ensure that the value is saved to long-term storage in the
 * {@link ScriptableDataStore} and if future scenes also have the /save tag, then they will fetch
 * and add in that saved value. Optionally user may include an alias for the save (ex. /save,score),
 * which will result in the value being saved and fetched as the alias.
 * <p>
 * The /op: tag takes in a three-character string (add, sub, mul, div), and then a comma, and then
 * either a literal value or another object.serializablefield after. An example can be seen below.
 * <p>
 * This class is typically used in behaviors for HUD elements, like scoreboards or health displays,
 * where text needs to reflect dynamic game state stored in a shared context. Example usage in a
 * behavior JSON:
 * <pre>
 * {
 *   "name": "SetScoreText",
 *   "actions": [
 *     {
 *       "name": "SetTextFromDataStoreAction",
 *       "parameter": "player.score/save,score/op:add,100"
 *     }
 *   ]
 * }
 * </pre>
 *
 * @author - Logan Dracos
 */
public class SetTextFromDataStoreAction extends SetTextFromAction {

  @Override
  protected BiConsumer<TextRenderer, String> provideSetter() {
    return (textRenderer, keyPath) -> {
      try {
        if (getPrefix() == null) {
          setPrefix(textRenderer.getText());
        }

        Object getValue = getDataStore().getValue(keyPath);

        textRenderer.setText(getValue != null ? getPrefix() + getValue : "null");

      } catch (MissingKeyException | IllegalStateException | ClassCastException e) {
        throw new IllegalArgumentException(GameConfig.getText("failedToRetrieveKey", keyPath), e);
      }
    };
  }
}
package oogasalad.model.engine.subComponent.behavior.action;

import java.util.function.BiConsumer;
import oogasalad.model.engine.component.TextRenderer;
import oogasalad.model.engine.datastore.ScriptableDataStore;

/**
 * Abstract Parent class for actions that set the text of a {@link TextRenderer} component based on a
 * value retrieved from the {@link ScriptableDataStore}. This class provides a common structure for
 * actions that set text based on data store values, allowing for different implementations to
 * customize the behavior of how the text is set.
 * 
 * @author Christian Bepler, Logan Dracos
 */

public abstract class SetTextFromAction extends SetTextAction<String> {
    private ScriptableDataStore dataStore;
    private String prefix = null;

    @Override
    protected void awake() {
    super.awake();
    dataStore = getBehavior().getController().getParent().getScene()
        .getGame().getDataStore();
    }

    @Override
    protected abstract BiConsumer<TextRenderer, String> provideSetter();

    @Override
    protected String toText(String value) {
    return String.valueOf(value);
    }

    @Override
    protected String defaultParameter() {
    return "";
    }

    /**
     * Gets the prefix for the text renderer. This prefix is used to prepend the text that is set
     * based on the data store value.
     *
     * @return the prefix string
     */
    protected String getPrefix() {
    return prefix;
    }

    /**
     * Sets the prefix for the text renderer. This prefix is used to prepend the text that is set
     * based on the data store value.
     *
     * @param prefix the prefix string
     */
    protected void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Gets the data store associated with this action. This data store is used to retrieve values
     * for setting the text of the {@link TextRenderer} component.
     *
     * @return the data store
     */
    protected ScriptableDataStore getDataStore() {
        return dataStore;
    }

    /**
     * Sets the data store for this action. This data store is used to retrieve values for setting
     * the text of the {@link TextRenderer} component.
     *
     * @param dataStore the data store
     */
    protected void setDataStore(ScriptableDataStore dataStore) {
        this.dataStore = dataStore;
    }
}
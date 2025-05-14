package oogasalad.model.engine.subComponent.behavior.constraint;

import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.subComponent.behavior.BehaviorConstraint;
import oogasalad.model.engine.architecture.KeyCode;
import oogasalad.model.engine.component.InputHandler;

/**
 * KeyNotPressConstraint is a class that extends BehaviorConstraint and is used to check if a key is
 * not ever pressed.
 *
 * @author Christian Bepler
 */
public class KeyNotPressConstraint extends BehaviorConstraint<KeyCode> {

    private InputHandler inputHandler;

    @Override
    protected KeyCode defaultParameter() {
        return KeyCode.NONE;
    }

    @Override
    protected void awake() {
        inputHandler = getComponent(InputHandler.class);
    }

    @Override
    public boolean check(KeyCode parameter) {
        return !(inputHandler != null && inputHandler.isKeyPressed(parameter));
    }

    @Override
    public Set<Class<? extends GameComponent>> requiredComponents() {
        return Set.of(InputHandler.class);
    }
}

package oogasalad.model.engine.subComponent.behavior.action;

import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.component.Transform;

/**
 * The RotationSetAction class is an action that sets the rotation of a Transform component.
 * 
 * 
 * @author Christian Bepler
 */

public class RotationSetAction extends SetComponentValueAction<Double, Transform> {

    @Override
    protected Transform supplyComponent() {
        return getComponent(Transform.class);
    }


    @Override
    protected java.util.function.BiConsumer<Transform, Double> provideSetter() {
        return Transform::setRotation;
    }

    @Override
    protected Double defaultParameter() {
        return 0.0;
    }

    @Override
    public Set<Class<? extends GameComponent>> requiredComponents() {
        return Set.of(
            Transform.class
        );
    }
}

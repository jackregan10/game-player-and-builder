package oogasalad.model.engine.subComponent.behavior.action;

import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.component.Transform;

import java.util.function.BiConsumer;

/**
 * ScaleYSetAction is a class that extends SetComponentValueAction and is used to set the Y scale of
 * the object.
 *
 * @author Christian Bepler
 */

public class ScaleYSetAction extends SetComponentValueAction<Double, Transform> {

    @Override
    protected Transform supplyComponent() {
        return getComponent(Transform.class);
    }

    @Override
    protected BiConsumer<Transform, Double> provideSetter() {
        return Transform::setScaleY;
    }

    @Override
    protected Double defaultParameter() {
        return 0.0;
    }

    @Override
    public Set<Class<? extends GameComponent>> requiredComponents() {
        return Set.of(Transform.class);
    }
}

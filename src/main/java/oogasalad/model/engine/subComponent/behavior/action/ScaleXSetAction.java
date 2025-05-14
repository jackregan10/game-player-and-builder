package oogasalad.model.engine.subComponent.behavior.action;

import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.component.Transform;

/**
 * ScaleXSetAction is a class that extends SetComponentValueAction and is used to set the X scale of
 * the object.
 *
 * @author Christian Bepler
 */

public class ScaleXSetAction extends SetComponentValueAction<Double, Transform> {

    @Override
    protected Transform supplyComponent() {
        return getComponent(Transform.class);
    }

    @Override
    protected java.util.function.BiConsumer<Transform, Double> provideSetter() {
        return Transform::setScaleX;
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

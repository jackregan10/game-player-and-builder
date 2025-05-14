package oogasalad.model.engine.subComponent.behavior.action;

import oogasalad.model.engine.subComponent.behavior.BehaviorAction;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * SetComponentValueAction is an abstract class that extends BehaviorAction and is used to set a
 * value of a component.
 *
 * @param <T> the type of the parameter to be set
 * @param <C> the type of the component
 * 
 * @author Christian Bepler
 */

public abstract class SetComponentValueAction<T, C> extends BehaviorAction<T> {

    private Supplier<C> componentSupplier;
    private BiConsumer<C, T> setter;

    @Override
    protected void awake() {
        componentSupplier = this::supplyComponent;
        setter = provideSetter();
    }

    protected abstract C supplyComponent();

    protected abstract BiConsumer<C, T> provideSetter();

    @Override
    protected void perform(T parameter) {
        C component = componentSupplier.get();
        setter.accept(component, parameter);
    }
}

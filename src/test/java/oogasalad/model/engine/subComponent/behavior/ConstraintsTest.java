package oogasalad.model.engine.subComponent.behavior;

import org.junit.jupiter.api.Test;


public abstract class ConstraintsTest<T extends BehaviorConstraint<?>> extends BehaviorBaseTest {
    private T constraint;

    public abstract void customSetUp();

    protected T getConstraint() {
        return constraint;
    }

    protected void setConstraint(T constraint) {
        this.constraint = constraint;
    }

    @Test
    public abstract void check_checkPositive_returnsTrue();

    @Test
    public abstract void check_checkNegative_returnsFalse();

}

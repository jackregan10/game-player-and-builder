package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

public abstract class ActionsTest<T extends BehaviorAction<?>> extends BehaviorBaseTest {

    private T action;

    public abstract void customSetUp();

    protected T getAction() {
        return action;
    }

    protected void setAction(T action){
        this.action = action;
        try {
            Method awakeMethod = BehaviorComponent.class.getDeclaredMethod("awake");
            awakeMethod.setAccessible(true);
            awakeMethod.invoke(action);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
          assert false;
        }
    }


    @Test
    public abstract void onPerform_performAction_performAction();
}

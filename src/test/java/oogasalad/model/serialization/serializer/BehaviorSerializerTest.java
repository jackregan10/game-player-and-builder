package oogasalad.model.serialization.serializer;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import oogasalad.model.engine.subComponent.behavior.Behavior;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.model.engine.subComponent.behavior.BehaviorConstraint;
import oogasalad.model.engine.subComponent.behavior.action.SwapAction;
import oogasalad.model.serialization.nodes.BehaviorNode;
import oogasalad.model.engine.subComponent.behavior.constraint.KeyPressConstraint;
import oogasalad.model.engine.architecture.KeyCode;

public class BehaviorSerializerTest
        extends CustomSerializerTest<BehaviorSerializer, Behavior, BehaviorNode> {

    public BehaviorSerializerTest() {
        super(new BehaviorSerializer());
    }

    @Override
    @BeforeEach
    public void setup() {
        input = new Behavior("name");
        SwapAction swapAction = new SwapAction();
        swapAction.setParameter("testParameter");
        addAction(input, swapAction);
        KeyPressConstraint keyPressConstraint = new KeyPressConstraint();
        keyPressConstraint.setParameter(KeyCode.A);
        addConstraint(input, keyPressConstraint);
    }

    @Override
    protected Boolean compareValues(Behavior a, Behavior b) {
        return a.getName().equals(b.getName()) && sameActions(getActions(a), getActions(b))
                && sameConstraints(getConstraints(a), getConstraints(b));
    }

    @Override
    protected Behavior convertNodeToObject(BehaviorNode node) {
        return BehaviorNode.toBehavior(node);
    }

    @Override
    protected BehaviorNode createNodeFromInput(Behavior input) {
        return new BehaviorNode(input);
    }

    private boolean sameActions(List<BehaviorAction<?>> a, List<BehaviorAction<?>> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (!sameAction(a.get(i), b.get(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean sameAction(BehaviorAction a, BehaviorAction b) {
        return a.getClass().equals(b.getClass()) && a.getParameter().equals(b.getParameter());
    }

    private boolean sameConstraints(List<BehaviorConstraint<?>> a, List<BehaviorConstraint<?>> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (!sameConstraint(a.get(i), b.get(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean sameConstraint(BehaviorConstraint a, BehaviorConstraint b) {
        return a.getClass().equals(b.getClass()) && a.getParameter().equals(b.getParameter());
    }

    public List<BehaviorAction<?>> getActions(Behavior behavior) {
        return (List<BehaviorAction<?>>) behavior.getSerializedField("actions").getValue();
    }

    public List<BehaviorConstraint<?>> getConstraints(Behavior behavior) {
        return (List<BehaviorConstraint<?>>) behavior.getSerializedField("constraints").getValue();
    }

    public void addAction(Behavior behavior, BehaviorAction<?> action) {
        getActions(behavior).add(action);
        action.setBehavior(behavior);
    }

    public void addConstraint(Behavior behavior, BehaviorConstraint<?> constraint) {
        getConstraints(behavior).add(constraint);
        constraint.setBehavior(behavior);
    }

}

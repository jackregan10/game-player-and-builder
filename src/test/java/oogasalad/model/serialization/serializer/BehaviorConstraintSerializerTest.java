package oogasalad.model.serialization.serializer;

import org.junit.jupiter.api.BeforeEach;

import oogasalad.model.engine.subComponent.behavior.BehaviorConstraint;
import oogasalad.model.engine.subComponent.behavior.constraint.KeyPressConstraint;
import oogasalad.model.serialization.nodes.BehaviorConstraintNode;
import oogasalad.model.engine.architecture.KeyCode;

public class BehaviorConstraintSerializerTest extends
        CustomSerializerTest<BehaviorConstraintSerializer, BehaviorConstraint, BehaviorConstraintNode> {

    public BehaviorConstraintSerializerTest() {
        super(new BehaviorConstraintSerializer());
    }

    @Override
    @BeforeEach
    public void setup() {
        input = new KeyPressConstraint();
        input.setParameter(KeyCode.A);
    }

    @Override
    protected BehaviorConstraint convertNodeToObject(BehaviorConstraintNode node) {
        return BehaviorConstraintNode.toBehaviorConstraint(node);
    }

    @Override
    protected BehaviorConstraintNode createNodeFromInput(BehaviorConstraint input) {
        return new BehaviorConstraintNode(input);
    }

    @Override
    protected Boolean compareValues(BehaviorConstraint a, BehaviorConstraint b) {
        return a.getClass().equals(b.getClass()) && a.getParameter().equals(b.getParameter());
    }
}

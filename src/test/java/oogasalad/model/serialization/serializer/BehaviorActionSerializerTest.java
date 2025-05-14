package oogasalad.model.serialization.serializer;

import org.junit.jupiter.api.BeforeEach;

import oogasalad.model.engine.subComponent.behavior.BehaviorAction;
import oogasalad.model.engine.subComponent.behavior.action.SwapAction;
import oogasalad.model.serialization.nodes.BehaviorActionNode;


final class BehaviorActionSerializerTest
        extends CustomSerializerTest<BehaviorActionSerializer, BehaviorAction, BehaviorActionNode> {

    protected BehaviorActionSerializerTest() {
        super(new BehaviorActionSerializer());
    }

    @Override
    @BeforeEach
    public void setup() {
        input = new SwapAction();
        input.setParameter("testParameter");
    }

    @Override
    protected Boolean compareValues(BehaviorAction a, BehaviorAction b) {
        return a.getClass().equals(b.getClass()) && a.getParameter().equals(b.getParameter());
    }

    @Override
    protected BehaviorAction convertNodeToObject(BehaviorActionNode node) {
        return BehaviorActionNode.toBehaviorAction(node);
    }

    @Override
    protected BehaviorActionNode createNodeFromInput(BehaviorAction input) {
        return new BehaviorActionNode(input);
    }

}

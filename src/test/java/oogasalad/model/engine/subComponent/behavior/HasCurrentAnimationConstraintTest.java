package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;

import oogasalad.model.resource.ResourcePath;
import org.junit.jupiter.api.Test;
import java.util.List;

import oogasalad.model.engine.component.AnimationController;
import oogasalad.model.engine.component.SpriteRenderer;
import oogasalad.model.engine.subComponent.behavior.constraint.HasCurrentAnimationConstraint;
import oogasalad.model.engine.subComponent.animation.Animation;


public class HasCurrentAnimationConstraintTest
        extends ConstraintsTest<HasCurrentAnimationConstraint> {

    private AnimationController animationController;

    @Override
    public void customSetUp() {
        HasCurrentAnimationConstraint constraint = new HasCurrentAnimationConstraint();
        addConstraint(getBehavior1(), constraint);
        setConstraint(constraint);
        getObj1().addComponent(SpriteRenderer.class);
        animationController = getObj1().addComponent(AnimationController.class);
        Animation animation = new Animation();
        animation.setName("testAnimation");
        ResourcePath path1 = new ResourcePath();
        path1.setPath("testPath1");
        ResourcePath path2 = new ResourcePath();
        path1.setPath("testPath2");
        animation.setFilePaths(List.of(path1, path2));
        animation.setAnimationLength(1.0);
        animation.setLoop(true);
        addAnimation(animationController, animation);
        animationController.awake();
        animationController.setCurrentAnimation(animation.getName());
    }

    @Override
    @Test
    public void check_checkPositive_returnsTrue() {
        assertTrue(getConstraint().onCheck("testAnimation"));
    }

    @Override
    @Test
    public void check_checkNegative_returnsFalse() {
        assertFalse(getConstraint().onCheck("testAnimation2"));
    }

}

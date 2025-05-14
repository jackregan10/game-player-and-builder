package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;

import oogasalad.model.resource.ResourcePath;
import org.junit.jupiter.api.Test;
import java.util.List;

import oogasalad.model.engine.subComponent.behavior.action.LaunchAnimationAction;
import oogasalad.model.engine.component.AnimationController;
import oogasalad.model.engine.component.SpriteRenderer;
import oogasalad.model.engine.subComponent.animation.Animation;

public class LaunchAnimationActionTest extends ActionsTest<LaunchAnimationAction> {

    @Override
    public void customSetUp() {
        LaunchAnimationAction action = new LaunchAnimationAction();
        addAction(getBehavior1(), action);
        setAction(action);
        getObj1().addComponent(SpriteRenderer.class);
        AnimationController controller = getObj1().addComponent(AnimationController.class);
        Animation animation = new Animation();
        animation.setName("testAnimation");
        ResourcePath path1 = new ResourcePath();
        path1.setPath("img1.png");
        ResourcePath path2 = new ResourcePath();
        path1.setPath("img2.png");
        animation.setFilePaths(List.of(path1, path2));
        animation.setAnimationLength(1.0);
        animation.setLoop(true);
        addAnimation(controller, animation);
        controller.awake();
    }

    @Override
    @Test
    public void onPerform_performAction_performAction() {
        String animationName = "testAnimation";
        getAction().onPerform(animationName);
        assertEquals(animationName, getObj1().getComponent(AnimationController.class).getCurrentAnimation());
    }
}

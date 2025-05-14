package oogasalad.model.engine.component;

import oogasalad.model.resource.ResourcePath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.subComponent.animation.Animation;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class AnimationControllerTest {

    private AnimationController animationController;
    private SpriteRenderer spriteRenderer;
    
    @BeforeEach
    public void setUp() {
        animationController = new AnimationController();
        Animation animation = new Animation();
        animation.setName("testAnimation");
        ResourcePath path1 = new ResourcePath();
        path1.setPath("img1.png");
        ResourcePath path2 = new ResourcePath();
        path2.setPath("img2.png");
        animation.setFilePaths(List.of(path1, path2));
        animation.setAnimationLength(1.0);
        animation.setLoop(true);
        addAnimation(animationController, animation);
        Animation animation2 = new Animation();
        animation2.setName("testAnimation2");
        ResourcePath path3 = new ResourcePath();
        path3.setPath("img3.png");
        ResourcePath path4 = new ResourcePath();
        path4.setPath("img4.png");
        animation2.setFilePaths(List.of(path3, path4));
        animation2.setAnimationLength(2.0);
        animation2.setLoop(false);
        addAnimation(animationController, animation2);
        GameObject obj = new GameObject("TestObject", "tag");
        obj.addComponent(animationController);
        spriteRenderer = obj.addComponent(SpriteRenderer.class);
        animationController.awake();
    }

    private void addAnimation(AnimationController controller, Animation animation) {
        getAnimations(controller).add(animation);
    }
    
    private List<Animation> getAnimations(AnimationController controller) {
        return (List<Animation>) controller.getSerializedField("animations").getValue();
    }

    @Test
    public void setCurrentAnimation_validAnimationName_setsCurrentAnimation() {
        animationController.setCurrentAnimation("testAnimation");
        assertEquals("testAnimation", animationController.getCurrentAnimation());
    }

    @Test
    public void setCurrentAnimation_invalidAnimationName_doesNotChangeCurrentAnimation() {
        assertThrows(IllegalArgumentException.class, () -> {
            animationController.setCurrentAnimation("invalidAnimation");
        });
    }

    @Test
    public void update_switchesFilePath() {
        animationController.setCurrentAnimation("testAnimation");
        assertEquals("img1.png", spriteRenderer.getImagePath().getPath());
        animationController.update(0.6);
        assertEquals("img2.png", spriteRenderer.getImagePath().getPath());
    }
}

package oogasalad.model.serialization.nodes;

import java.util.stream.IntStream;
import oogasalad.model.resource.ResourcePath;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

import oogasalad.model.engine.subComponent.animation.Animation;

public class AnimationNodeTest {
    @Test
    public void toAnimation_ValidNode_ReturnsAnimation() {
        Animation animation = new Animation();
        animation.setName("testAnimation");
        ResourcePath path1 = new ResourcePath();
        path1.setPath("img1.png");
        ResourcePath path2 = new ResourcePath();
        path2.setPath("img2.png");
        animation.setFilePaths(List.of(path1, path2));
        animation.setAnimationLength(1.0);
        animation.setLoop(true);
        AnimationNode node = new AnimationNode(animation);
        Animation testAnimation = AnimationNode.toAnimation(node);
        compareAnimations(animation, testAnimation);
    }

    protected Boolean compareAnimations(Animation a, Animation b) {
        return a.getName().equals(b.getName()) &&
            a.getAnimationLength().equals(b.getAnimationLength()) &&
            a.getLoop() == b.getLoop() &&
            a.getFilePaths().size() == b.getFilePaths().size() &&
            IntStream.range(0, a.getFilePaths().size())
                .allMatch(i -> a.getFilePaths().get(i).getPath().equals(b.getFilePaths().get(i).getPath()));
    }
}

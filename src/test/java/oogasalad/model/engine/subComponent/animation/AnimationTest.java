package oogasalad.model.engine.subComponent.animation;

import static org.junit.jupiter.api.Assertions.*;

import oogasalad.model.resource.ResourcePath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;


public class AnimationTest {

    private Animation animation;

    @BeforeEach
    public void setUp() {
        animation = new Animation();
        animation.setName("testAnimation");
        ResourcePath path1 = new ResourcePath();
        path1.setPath("img1.png");
        ResourcePath path2 = new ResourcePath();
        path2.setPath("img2.png");
        animation.setFilePaths(List.of(path1, path2));
        animation.setAnimationLength(1.0);
        animation.setLoop(true);
        animation.reset();
    }

    @Test
    public void getNextPath_simpleIncrement_returnsPath(){
        assertEquals("img1.png", animation.getNextPath().getPath());
        assertEquals("img2.png", animation.getNextPath().getPath());
    }

    @Test
    public void getNextPath_noLoop_resetsBackToLast(){
        animation.setLoop(false);
        assertEquals("img1.png", animation.getNextPath().getPath());
        assertEquals("img2.png", animation.getNextPath().getPath());
        assertEquals("img2.png", animation.getNextPath().getPath());
    }

    @Test
    public void getNextPath_loop_resetsBackToFirst(){
        animation.setLoop(true);
        assertEquals("img1.png", animation.getNextPath().getPath());
        assertEquals("img2.png", animation.getNextPath().getPath());
        assertEquals("img1.png", animation.getNextPath().getPath());
    }
}

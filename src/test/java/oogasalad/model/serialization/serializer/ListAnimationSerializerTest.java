package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.stream.IntStream;
import oogasalad.model.resource.ResourcePath;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;

import oogasalad.model.engine.subComponent.animation.Animation;

final class ListAnimationSerializerTest {

  private final ListAnimationSerializer serializer = new ListAnimationSerializer();
  
  private List<Animation> animations;

  @BeforeEach
   public void setup() {
        Animation animation = new Animation();
        animation.setName("testAnimation");
        ResourcePath path1 = new ResourcePath();
        path1.setPath("img1.png");
        ResourcePath path2 = new ResourcePath();
        path2.setPath("img2.png");
        animation.setFilePaths(List.of(path1, path2));
        animation.setAnimationLength(1.0);
        animation.setLoop(true);
        animation.reset();
        Animation animation2 = new Animation();
        animation2.setName("testAnimation2");
        ResourcePath path3 = new ResourcePath();
        path3.setPath("img3.png");
        ResourcePath path4 = new ResourcePath();
        path4.setPath("img4.png");
        animation2.setFilePaths(List.of(path3, path4));
        animation2.setAnimationLength(2.0);
        animation2.setLoop(false);
        animation2.reset();
        animations = List.of(animation, animation2);
    }

  @Test
  void serialize_ValidAnimation_ReturnsAnimationNode() {
    JsonNode result = serializer.serialize(animations);
    assertInstanceOf(ArrayNode.class, result);
    ArrayNode animationNode = (ArrayNode) result;
    assertNotNull(animationNode);
  }

  @Test
  void deserialize_ValidDoubleNode_ReturnsDoubleValue() {
    JsonNode result = serializer.serialize(animations);
    List<Animation> resultAnimation = serializer.deserialize(result);
    assertTrue(compareAnimationLists(resultAnimation, animations));
  }

  private Boolean compareAnimationLists(List<Animation> a, List<Animation> b) {
    if (a.size() != b.size()) {
      return false;
    }
    for (int i = 0; i < a.size(); i++) {
      if (!compareAnimations(a.get(i), b.get(i))) {
        return false;
      }
    }
    return true;
  }

  private Boolean compareAnimations(Animation a, Animation b) {
    boolean pathsEqual = a.getFilePaths().size() == b.getFilePaths().size() &&
        IntStream.range(0, a.getFilePaths().size())
            .allMatch(i -> a.getFilePaths().get(i).getPath().equals(b.getFilePaths().get(i).getPath()));
    return a.getName().equals(b.getName()) &&
        a.getAnimationLength().equals(b.getAnimationLength()) &&
        a.getLoop() == b.getLoop() && pathsEqual;
  }
}

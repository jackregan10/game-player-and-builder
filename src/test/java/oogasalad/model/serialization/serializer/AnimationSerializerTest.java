package oogasalad.model.serialization.serializer;

import java.util.stream.IntStream;
import oogasalad.model.resource.ResourcePath;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;

import oogasalad.model.engine.subComponent.animation.Animation;
import oogasalad.model.serialization.nodes.AnimationNode;

final class AnimationSerializerTest extends CustomSerializerTest<AnimationSerializer, Animation, AnimationNode> {

  protected AnimationSerializerTest() {
    super(new AnimationSerializer());
  }

  @Override
  @BeforeEach
  public void setup() {
      input = new Animation();
      input.setName("testAnimation");
      ResourcePath path1 = new ResourcePath();
      path1.setPath("img1.png");
      ResourcePath path2 = new ResourcePath();
      path2.setPath("img2.png");
      input.setFilePaths(List.of(path1, path2));
      input.setAnimationLength(1.0);
      input.setLoop(true);
      input.reset();
  }

  @Override
  protected Boolean compareValues(Animation a, Animation b) {
    boolean pathsEqual = a.getFilePaths().size() == b.getFilePaths().size() &&
        IntStream.range(0, a.getFilePaths().size())
            .allMatch(i -> a.getFilePaths().get(i).getPath().equals(b.getFilePaths().get(i).getPath()));
    return a.getName().equals(b.getName()) &&
            a.getAnimationLength().equals(b.getAnimationLength()) &&
            a.getLoop() == b.getLoop() && pathsEqual;
  }

  @Override
  protected Animation convertNodeToObject(AnimationNode node) {
    return AnimationNode.toAnimation(node);
  }

  @Override
  protected AnimationNode createNodeFromInput(Animation input) {
    return new AnimationNode(input);
  }
}

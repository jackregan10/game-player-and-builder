package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public abstract class CustomSerializerTest<T extends Serializer, U, V extends JsonNode> {

  private final T serializer;
  protected U input;

  protected CustomSerializerTest(T serializerInstance) {
    this.serializer = serializerInstance;
  }

  @BeforeEach
  public abstract void setup();

  @Test
  void serialize_Valid_ReturnsNode() {
    JsonNode result = serializer.serialize(input);
    assertTrue(isInstanceOfV(result));
    V node = (V) result;
    U output = convertNodeToObject(node);
    assertTrue(compareValues(output, input));
  }

  @Test
  void deserialize_Valid_ValidOutput() {
    V node = createNodeFromInput(input);
    U result = (U) serializer.deserialize(node);
    assertTrue(compareValues(result, input));
  }

  /**
   * Converts a node to the corresponding object using the appropriate static method.
   * 
   * @param node the node to convert
   * @return the converted object
   */
  protected abstract U convertNodeToObject(V node);

  /**
   * Creates a node from the input object.
   * 
   * @param input the input object
   * @return the corresponding node
   */
  protected abstract V createNodeFromInput(U input);

  /**
   * Compares two objects of type U for equality.
   * 
   * @param a the first object
   * @param b the second object
   * @return true if the objects are equal, false otherwise
   */
  protected abstract Boolean compareValues(U a, U b);

  @SuppressWarnings("unchecked")
  private boolean isInstanceOfV(JsonNode node) {
    try {
      V castedNode = (V) node;
      return castedNode != null;
    } catch (ClassCastException e) {
      return false;
    }
  }
}

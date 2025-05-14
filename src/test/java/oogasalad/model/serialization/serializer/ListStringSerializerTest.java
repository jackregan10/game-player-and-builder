package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class ListStringSerializerTest {

  private final ListStringSerializer serializer = new ListStringSerializer();
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void serialize_NonNullList_ReturnsArrayNode() {
    // Prepare the test data
    List<String> input = List.of("value1", "value2", "value3");

    // Serialize the list
    JsonNode result = serializer.serialize(input);

    // Check if the result is an ArrayNode
    assertTrue(result instanceof ArrayNode);

    // Verify the values in the ArrayNode
    ArrayNode arrayNode = (ArrayNode) result;
    assertEquals(3, arrayNode.size());
    assertEquals("value1", arrayNode.get(0).asText());
    assertEquals("value2", arrayNode.get(1).asText());
    assertEquals("value3", arrayNode.get(2).asText());
  }

  @Test
  void serialize_NullList_ReturnsNullNode() {
    // Prepare the test data
    List<String> input = null;

    // Serialize the null list
    JsonNode result = serializer.serialize(input);

    // Check if the result is a NullNode
    assertTrue(result instanceof NullNode);
  }

  @Test
  void deserialize_ValidArrayNode_ReturnsList() {
    // Prepare the test data
    JsonNode node = objectMapper.createArrayNode()
        .add("value1")
        .add("value2")
        .add("value3");

    // Deserialize the array node
    List<String> result = serializer.deserialize(node);

    // Verify the deserialized values
    assertEquals(3, result.size());
    assertEquals("value1", result.get(0));
    assertEquals("value2", result.get(1));
    assertEquals("value3", result.get(2));
  }

  @Test
  void deserialize_NullNode_ReturnsEmptyList() {
    // Deserialize the NullNode
    List<String> result = serializer.deserialize(NullNode.getInstance());

    // Verify that the result is an empty list
    assertTrue(result.isEmpty());
  }

  @Test
  void deserialize_NullArrayNode_ReturnsEmptyList() {
    // Deserialize the null array
    List<String> result = serializer.deserialize(NullNode.getInstance());

    // Verify that the result is an empty list
    assertTrue(result.isEmpty());
  }

  @Test
  void deserialize_InvalidNode_ReturnsEmptyList() {
    // Prepare the test data (a node that is not an array)
    JsonNode node = new TextNode("invalid");

    // Deserialize the invalid node
    List<String> result = serializer.deserialize(node);

    // Verify that the result is an empty list
    assertTrue(result.isEmpty());
  }

  @Test
  void deserialize_ListWithNullElements_ReturnsListWithEmptyStrings() {
    // Prepare the test data (array with null elements)
    JsonNode node = objectMapper.createArrayNode()
        .add("value1")
        .addNull()
        .add("value3");

    // Deserialize the array node
    List<String> result = serializer.deserialize(node);

    // Verify the deserialized values
    assertEquals(3, result.size());
    assertEquals("value1", result.get(0));
    assertEquals("", result.get(1));  // Null node is deserialized as empty string
    assertEquals("value3", result.get(2));
  }
}

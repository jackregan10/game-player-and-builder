package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import oogasalad.model.engine.architecture.KeyCode;
import oogasalad.model.serialization.serializable.Serializable;
import oogasalad.model.serialization.serializable.SerializableField;
import oogasalad.model.serialization.serializable.SerializedField;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SerializerTest {

  public static class DummySerializableFieldHolder implements Serializable {
    // SUPPORTED
    @SerializableField
    private String stringField = "";
    @SerializableField
    private Double doubleField = 0.0;
    @SerializableField
    private Integer integerField = 0;
    @SerializableField
    private Boolean booleanField = false;
    @SerializableField
    private List<String> listStringField = new ArrayList<>();
    @SerializableField
    private KeyCode keyCodeField = KeyCode.NONE;

    // UNSUPPORTED
    @SerializableField
    private Test testField;
  }

  @Test
  void serialize_NoSerializerAvailable_ThrowsException() throws NoSuchFieldException, NoSuchMethodException {
    DummySerializableFieldHolder holder = new DummySerializableFieldHolder();
    SerializedField field = holder.getSerializedField("testField");

    Exception e = assertThrows(SerializerException.class, () ->
        Serializer.serialize(field));
  }

  @Test
  void deserialize_AcceptableNullValue_NoExceptionThrown() throws NoSuchFieldException, NoSuchMethodException {
    DummySerializableFieldHolder holder = new DummySerializableFieldHolder();
    SerializedField field = holder.getSerializedField("stringField");

    JsonNode node = new ObjectNode(null);

    // Verify that the appropriate exception is thrown
    assertDoesNotThrow(() -> Serializer.deserialize(field, node));
  }

  @Test
  void deserialize_InvalidJson_ThrowsException() throws NoSuchFieldException, NoSuchMethodException {
    DummySerializableFieldHolder holder = new DummySerializableFieldHolder();
    SerializedField field = holder.getSerializedField("keyCodeField");

    // Invalid JSON value as string
    String value = "InvalidJson";

    // Verify that the appropriate exception is thrown
    Exception e = assertThrows(SerializerException.class, () ->
        Serializer.deserialize(field, value));
  }
}

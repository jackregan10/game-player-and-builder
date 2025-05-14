package oogasalad.model.serialization.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.rpc.InvalidArgumentException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import oogasalad.model.config.GameConfig;
import oogasalad.model.serialization.serializable.SerializedField;
import oogasalad.model.serialization.serializable.TypeRef;
import oogasalad.model.serialization.serializable.TypeReferenceException;
import org.reflections.Reflections;

/**
 * This is the class handle all the serialization process of the serializable fields.
 * @param <T> the class specified.
 */
public abstract class Serializer<T> {

  private static final String SERIALIZER_PACKAGE = Serializer.class.getPackage().getName();
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final Map<Type, Serializer<?>> instancePool = new HashMap<>();

  static {
    Reflections reflections = new Reflections(SERIALIZER_PACKAGE);
    Set<Class<? extends Serializer>> subTypes = reflections.getSubTypesOf(Serializer.class);
    for (Class<? extends Serializer> clazz : subTypes) {
      try {
        if (!Modifier.isAbstract(clazz.getModifiers())) {
          Serializer<?> instance = clazz.getDeclaredConstructor().newInstance();
          Type actualType = TypeRef.findGenericTypeArgument(clazz);
          instancePool.put(actualType, instance);
        }
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
        throw new TypeReferenceException(GameConfig.getText("errorCreateSerializer", clazz.getName()), e);
      }
    }
  }

  /**
   * Finds the appropriate serializer from the pool and delegates the serialization.
   * @param field the field containing type and value
   * @return serialized JsonNode
   */
  public static JsonNode serialize(SerializedField field) throws SerializerException {
    try {
      Serializer<Object> serializer = getSerializer(field.getFieldType());
      assert serializer != null;
      return serializer.serialize(field.getValue());
    } catch (AssertionError | IndexOutOfBoundsException e) {
      throw new SerializerException(GameConfig.getText("errorSerializing", field.getFieldType()), e);
    }
  }

  /**
   * Finds the appropriate serializer from the pool and delegates the deserialization,
   * modifying the value in the provided field.
   * @param field the field containing type and value information
   * @param node the JsonNode to deserialize
   */
  public static void deserialize(SerializedField field, JsonNode node) throws SerializerException {
    try {
      Serializer<Object> serializer = getSerializer(field.getFieldType());
      assert serializer != null;
      Object deserializedValue = serializer.deserialize(node);
      field.setValue(deserializedValue);
    } catch (AssertionError | InvalidArgumentException | SerializerException e) {
      throw new SerializerException(GameConfig.getText("errorDeserializing", field.getFieldType()), e);
    }
  }

  /**
   * Finds the appropriate serializer from the pool and delegates the deserialization,
   * modifying the value in the provided field.
   *
   * @param field the field containing type and value information
   * @param value the value to deserialize
   *
   * @apiNote This first transforms the value into a JsonNode-like format {"fieldName": value}, then parses it.
   */
  public static void deserialize(SerializedField field, String value) throws SerializerException {
    try {
      JsonNode valueNode;
      try {
        valueNode = mapper.readTree(value);
      } catch (JsonProcessingException e) {
        valueNode = mapper.getNodeFactory().textNode(value);
      }
      deserialize(field, valueNode);
    } catch (SerializerException | IndexOutOfBoundsException | IllegalArgumentException e) {
      throw new SerializerException(GameConfig.getText("errorDeserializing", value), e);
    }
  }

  /**
   * Get the serializer based on the input Type.
   * @param type the given Type
   */
  @SuppressWarnings("unchecked")
  protected static Serializer<Object> getSerializer(Type type) {
    return (Serializer<Object>) instancePool.get(type);
  }

  /**
   * Serializes the given object of type T into a JsonNode.
   */
  protected abstract JsonNode serialize(T obj);

  /**
   * Deserializes the given JsonNode into an object of type T.
   */
  protected abstract T deserialize(JsonNode node);
}

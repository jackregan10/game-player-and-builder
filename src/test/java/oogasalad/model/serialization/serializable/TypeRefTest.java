package oogasalad.model.serialization.serializable;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import org.junit.jupiter.api.Test;

class TypeRefTest {

  static class StringListHolder extends GenericHolder<List<String>> {}
  static class IntegerHolder extends GenericHolder<Integer> {}
  static class NoGenericType {}

  static abstract class GenericHolder<T> {
    // A class with a generic type argument for testing
  }

  @Test
  void findGenericTypeArgument_ClassWithGenericSuperclass_ReturnsCorrectType() {
    Type type = TypeRef.findGenericTypeArgument(StringListHolder.class);
    assertTrue(type instanceof ParameterizedType);
    ParameterizedType paramType = (ParameterizedType) type;
    assertEquals(List.class, paramType.getRawType());
    assertEquals(String.class, paramType.getActualTypeArguments()[0]);
  }

  @Test
  void findGenericTypeArgument_ClassWithSimpleGeneric_ReturnsCorrectType() {
    Type type = TypeRef.findGenericTypeArgument(IntegerHolder.class);
    assertEquals(Integer.class, type);
  }

  @Test
  void findGenericTypeArgument_NoGenericType_ThrowsException() {
    Exception e = assertThrows(TypeReferenceException.class, () ->
        TypeRef.findGenericTypeArgument(NoGenericType.class));
  }
}

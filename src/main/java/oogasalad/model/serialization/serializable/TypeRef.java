package oogasalad.model.serialization.serializable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import oogasalad.model.config.GameConfig;

/**
 * This is the class that hold a reference for the actual type of class.
 * @param <T> the class specified
 * @author Hsuan-Kai Liao
 */
public abstract class TypeRef<T> {
  private static final Map<Type, Type> PRIMITIVE_TO_WRAPPER = Map.of(
      int.class, Integer.class,
      boolean.class, Boolean.class,
      char.class, Character.class,
      byte.class, Byte.class,
      short.class, Short.class,
      long.class, Long.class,
      float.class, Float.class,
      double.class, Double.class
  );

  private final Type type;

  protected TypeRef() {
    Type superClass = getClass().getGenericSuperclass();
    if (superClass instanceof ParameterizedType) {
      this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    } else {
      throw new TypeReferenceException(GameConfig.getText("invalidTypReference"));
    }
  }

  /**
   * Get the true type of T.
   */
  public Type getType() {
    return type;
  }

  /**
   * Change the primitive type into its wrapper type.
   * @param primitiveType the input primitive type
   * @return the wrapper type if input is primitive; else return itself.
   */
  public static Type wrapperForPrimitive(Type primitiveType) {
    return PRIMITIVE_TO_WRAPPER.getOrDefault(primitiveType, primitiveType);
  }

  /**
   * Finds the first generic type argument used in the direct superclass or interface
   * of the given class. It walks up the hierarchy and returns the first match found.
   * For example, if {@code class A extends B<List<String>>}, calling this with {@code A.class}
   * will return {@code List<String>}.
   *
   * @param clazz the class to analyze
   * @return the generic type argument (first one found), etc {@code A<T, U>}, this will return T.
   * @throws TypeReferenceException if no generic type argument is found
   */
  public static Type findGenericTypeArgument(Class<?> clazz) {
    String name = clazz.getName();
    Class<?> current = clazz;

    while (current != null && current != Object.class) {
      Type type = getGenericSuperclassType(current);
      if (type != null) {
        return type;
      }

      type = getGenericInterfaceType(current);
      if (type != null) {
        return type;
      }

      current = current.getSuperclass();
    }

    throw new TypeReferenceException(GameConfig.getText("errorFindingGenericType", name));
  }

  private static Type getGenericSuperclassType(Class<?> clazz) {
    Type genericSuperclass = clazz.getGenericSuperclass();
    if (genericSuperclass instanceof ParameterizedType paramType) {
      return paramType.getActualTypeArguments()[0];
    }
    return null;
  }

  private static Type getGenericInterfaceType(Class<?> clazz) {
    for (Type iface : clazz.getGenericInterfaces()) {
      if (iface instanceof ParameterizedType paramType) {
        return paramType.getActualTypeArguments()[0];
      }
    }
    return null;
  }

}

package oogasalad.model.serialization.serializable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import oogasalad.model.config.GameConfig;

/**
 * This is the interface indicating that its field can be annotated as @SerializableField. And all
 * the fields annotated @SerializableField will be added to this SerializedField list.
 *
 * @author Hsuan-Kai Liao
 */
public interface Serializable {

  /**
   * Get all the field annotated @SerializableField, including fields from parent classes.
   */
  default List<SerializedField> getSerializedFields() {
    List<SerializedField> serializedFields = new ArrayList<>();
    Class<?> clazz = this.getClass();

    while (clazz != null) {
      processClassFields(clazz, serializedFields);
      clazz = clazz.getSuperclass();
    }

    return serializedFields;
  }

  /**
   * Get the field annotated @SerializableField with the specified name.
   *
   * @apiNote We assume that each name only has one corresponding serializedField, otherwise we only
   *          return the first one.
   */
  default SerializedField getSerializedField(String fieldName) {
    return getSerializedFields().stream().filter(serializedField -> serializedField.getFieldName().equals(fieldName)).findFirst().orElse(null);
  }

  private void processClassFields(Class<?> clazz, List<SerializedField> list) {
    for (Field field : clazz.getDeclaredFields()) {
      if (field.isAnnotationPresent(SerializableField.class)) {
        SerializedField serializedField = createSerializedField(clazz, field);
        if (serializedField != null) {
          list.add(serializedField);
        }
      }
    }
  }

  private SerializedField createSerializedField(Class<?> clazz, Field field) {
    Type fieldGenericType = field.getGenericType();

    // Check if field's type matches supported types (including primitive/wrapper matching)
    Type fieldType = getValidType(fieldGenericType);

    if (fieldType == null) {
      GameConfig.LOGGER.warn(GameConfig.getText("unsupportedSerializableField",
          (fieldGenericType instanceof TypeVariable<?> typeVar ? resolveTypeVariable(this.getClass(), typeVar) : fieldGenericType.getTypeName()),
          clazz.getSimpleName(),
          field.getName()));

      return null;
    }

    // Construct getter and setter method names
    String fieldName = field.getName();
    String capitalized = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

    Method getter = null;
    Method setter = null;

    try {
      getter = clazz.getMethod("get" + capitalized);
    } catch (NoSuchMethodException ignored) {}

    try {
      setter = clazz.getMethod("set" + capitalized, field.getType());
    } catch (NoSuchMethodException ignored) {}

    return new SerializedField(this, field, fieldType, getter, setter);
  }

  private Type getValidType(Type fieldGenericType) {
    // Handle unresolved TypeVariables after all types checked
    if (fieldGenericType instanceof TypeVariable<?> typeVar) {
      return TypeRef.wrapperForPrimitive(resolveTypeVariable(this.getClass(), typeVar));
    }
    return TypeRef.wrapperForPrimitive(fieldGenericType);
  }

  private static Type resolveTypeVariable(Class<?> currentClass, TypeVariable<?> typeVar) {
    Type superclassType = currentClass.getGenericSuperclass();

    while (superclassType != null) {
      if (superclassType instanceof ParameterizedType pt) {
        Type resolved = resolveFromParameterizedType(typeVar, pt);
        if (resolved != null) return resolved;
      }

      superclassType = getRawClass(superclassType).getGenericSuperclass();
    }

    return null;
  }

  private static Class<?> getRawClass(Type type) {
    if (type instanceof Class<?>) {
      return (Class<?>) type;
    } else if (type instanceof ParameterizedType pt) {
      return (Class<?>) pt.getRawType();
    }
    return Object.class;
  }

  private static Type resolveFromParameterizedType(TypeVariable<?> typeVar, ParameterizedType pt) {
    Class<?> rawType = (Class<?>) pt.getRawType();
    Type[] actualTypes = pt.getActualTypeArguments();
    TypeVariable<?>[] typeParams = rawType.getTypeParameters();

    for (int i = 0; i < typeParams.length; i++) {
      if (typeParams[i].getName().equals(typeVar.getName())) {
        return actualTypes[i];
      }
    }
    return null;
  }
}

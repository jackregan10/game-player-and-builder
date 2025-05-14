package oogasalad.model.engine.subComponent.behavior;

import oogasalad.model.engine.architecture.KeyCode;

/**
 * This is the behaviorComponent's parameter allowed type.
 * NOTE: The enum name should be all capitalized and matches the class's name
 *
 * @author Hsuan-Kai Liao
 */
public enum BehaviorParamType {
  VOID(Void.class),
  STRING(String.class),
  INTEGER(Integer.class),
  DOUBLE(Double.class),
  BOOLEAN(Boolean.class),
  KEYCODE(KeyCode.class);

  private final Class<?> classType;

  /**
   * The constructor to create the allowed behaviorParamType.
   * @param classType the classType allowed.
   */
  BehaviorParamType(Class<?> classType) {
    this.classType = classType;
  }

  /**
   * Get the classType of the corresponding BehaviorParamType.
   */
  public Class<?> getClassType() {
    return classType;
  }

  /**
   * Check whether the given class is supported by any BehaviorParamType.
   * @param clazz the class to check
   * @return true if supported, false otherwise
   */
  public static boolean isSupported(Class<?> clazz) {
    for (BehaviorParamType type : values()) {
      if (type.getClassType().equals(clazz)) {
        return true;
      }
    }
    return false;
  }
}

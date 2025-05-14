package oogasalad.model.resource;

import oogasalad.model.serialization.serializable.Serializable;
import oogasalad.model.serialization.serializable.SerializableField;

/**
 * Represents a path to a resource, which could either be a normal file path or a packed path.
 * This class is serializable and can be used to store or load resource paths in serialized form.
 * The path is stored as a string, and its type (NORMAL or PACKED) can be specified globally for all instances.
 * The class also includes methods for getting and setting the path and its type.
 *
 * @author Hsuan-Kai Liao
 */
public class ResourcePath implements Serializable {

  private static String resourceToContext = null;
  private static String resourceFromContext = null;
  private static PathType pathType = PathType.NORMAL;

  @SerializableField
  private String path = "";

  /**
   * Enum representing the different types of paths.
   * NORMAL: Standard file path.
   * PACKED: A packed or compressed file path.
   */
  public enum PathType {
    NORMAL,
    PACKED
  }

  /**
   * Gets the path of the resource.
   *
   * @return The file path as a string.
   */
  public String getPath() {
    return path;
  }

  /**
   * Sets the path of the resource.
   *
   * @param path The file path to set.
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * Provides a string representation of the ResourcePath object.
   *
   * @return A string representing the ResourcePath object.
   */
  @Override
  public String toString() {
    return "ResourcePath{" +
        "path='" + path + '\'' +
        '}';
  }

  /**
   * Sets the TO context of the resource. The context is the base path for the resource.
   * NOTE: PLEASE REMEMBER TO CLEAN THE CONTEXT AFTER USING IT.
   * @param context The TO context to set.
   */
  public static void setToContext(String context, PathType type) {
    if (context != null && !context.isEmpty() && type != null) {
      resourceToContext = context;
      pathType = type;
    } else {
      pathType = PathType.NORMAL;
    }
  }

  /**
   * Cleans the context of the resource.
   * NOTE: This method MUST be called after using the context.
   */
  public static void cleanToContext() {
    resourceToContext = null;
    pathType = PathType.NORMAL;
  }

  /**
   * Sets the FROM context of the resource. The context is the base path for the resource.
   * @param context The FROM context to set.
   */
  public static void setFromContext(String context) {
    if (context != null && !context.isEmpty()) {
      resourceFromContext = context;
    }
  }

  /**
   * Get the current TO resource context.
   */
  public static String getToContext() {
    return resourceToContext;
  }

  /**
   * Get the current FROM resource context.
   */
  public static String getFromContext() {
    return resourceFromContext;
  }

  /**
   * Gets the current path type for the path output methods.
   *
   * @return The current path type (NORMAL or PACKED).
   */
  public static PathType getPathType() {
    return pathType;
  }
}

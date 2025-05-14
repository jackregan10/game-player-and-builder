package oogasalad.model.resource;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the class to hold the loaded resources with its paths.
 *
 * @author Hsuan-Kai Liao
 */
public final class ResourceCache {

  private static ResourceCache instance;

  private final Map<String, Object> resourcePool;

  private ResourceCache() {
    resourcePool = new HashMap<>();
  }

  /**
   * Get the instance of the singleton of this class.
   */
  private static ResourceCache getInstance() {
    if (instance == null) {
      instance = new ResourceCache();
    }
    return instance;
  }

  /**
   * Registers an object into the cache with a specified path.
   *
   * @param path The key under which the object will be stored in the cache.
   * @param objectCache The object to be cached.
   */
  public static void registerCache(String path, Object objectCache) {
    getInstance().resourcePool.put(path, objectCache);
  }

  /**
   * Retrieves an object from the cache by its path.
   *
   * @param path The key associated with the cached object.
   * @return The cached object, or null if the object is not found.
   */
  public static Object getCache(String path) {
    return getInstance().resourcePool.get(path);
  }

  /**
   * Clear all the current caches in the game.
   */
  public static void clearCache() {
    getInstance().resourcePool.clear();
  }
}

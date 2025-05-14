package oogasalad.model.resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * This is the class that handles the IO of the resources.
 */
public class ResourceIO {

  public static final String RESOURCES_FOLDER_NAME = "resources";

  /**
   * Creates a directory for the game file based on the JSON exporting path.
   * @param filepath the path of the JSON file.
   * @return the Json path of the new directory.
   */
  public static String createGameFileDirectoryFromJsonPath(String filepath) {
    String parentDirectory = Paths.get(filepath).getParent().toString();
    String fileName = Paths.get(filepath).getFileName().toString().replace(".json", "");

    File newDirectory = new File(parentDirectory, fileName);

    if (!newDirectory.exists()) {
      newDirectory.mkdirs();
    }

    return Paths.get(newDirectory.getPath(), fileName + ".json").toString();
  }

  /**
   * Moves a file to the resources folder.
   * @param targetPath the original path of the file to be moved.
   * @return the new path of the file in the resources folder, or "" if the move was unsuccessful.
   */
  public static String copyToResourcesFolder(String targetPath) {
    String resourcesDir = ResourcePath.getToContext() + "/" + RESOURCES_FOLDER_NAME;

    File dir = new File(resourcesDir);
    if (!dir.exists()) {
      dir.mkdirs();
    }

    // absolute path
    Path absolutePath = Paths.get(targetPath);
    if (Files.exists(absolutePath)) {
      return copyFileToResources(absolutePath);
    }

    // relative path
    Path relativePath = Paths.get(ResourcePath.getFromContext(), targetPath);
    if (Files.exists(relativePath)) {
      return copyFileToResources(relativePath);
    }

    // empty string if the file does not exist
    return "";
  }

  /**
   * Helper method to copy the file to resources folder.
   */
  private static String copyFileToResources(Path sourcePath) {
    String fileName = sourcePath.getFileName().toString();
    Path destinationPath = Paths.get(ResourcePath.getToContext() + "/" + RESOURCES_FOLDER_NAME, fileName);

    try {
      Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      return "";
    }

    return RESOURCES_FOLDER_NAME + "/" + fileName;
  }
}

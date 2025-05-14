package oogasalad.database;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FirebaseManagerTest {

  @BeforeAll
  public static void setup() throws IOException {
    FirebaseManager.initializeFirebase();
  }

  @Test
  void getDB_initializedDB_returnsNonNullInstance() {
    assertNotNull(FirebaseManager.getDB());
  }

  @Test
  void uploadPfp_createsPublicURL_Success() throws IOException {
    String username = "testUser_" + System.currentTimeMillis();

    File tempFile = File.createTempFile("test_pfp", ".png");
    FileWriter writer = new FileWriter(tempFile);
    writer.write("fakepngdata");
    writer.close();

    String url = FirebaseManager.uploadPfp(username, tempFile);
    assertNotNull(url);
    assertTrue(url.contains("https://storage.googleapis.com/"), "Should return public URL");

    // No need to delete manually anymore since you don't have deleteFileByFirebaseUrl
    // We assume profile pictures are for testing only

    assertTrue(tempFile.delete());
  }

  @Test
  void uploadLevelToFirebase_createsPublicURLs_andDeletesSuccessfully() throws IOException {
    String userId = "testUser_" + System.currentTimeMillis();
    String levelName = "TestLevelFolder";

    File tempLevelFolder = createTempLevelFolder(levelName);

    assertDoesNotThrow(() -> FirebaseManager.uploadLevelToFirebase(userId, levelName, tempLevelFolder));

    assertTrue(FirebaseManager.deleteLevelFolder(userId, levelName), "Uploaded level folder should be deleted successfully");

    deleteFolderRecursively(tempLevelFolder);
  }

  @Test
  void downloadLevelFolder_correctlyInitializedFolder_reconstructsFolderSuccessfully()
      throws IOException, DatabaseException {
    String userId = "testUser_" + System.currentTimeMillis();
    String levelName = "TestLevel";

    File tempLevelFolder = createTempLevelFolder(levelName);

    FirebaseManager.uploadLevelToFirebase(userId, levelName, tempLevelFolder);

    File downloadedFolder = FirebaseManager.downloadLevelFolder(userId, levelName);

    assertNotNull(downloadedFolder);
    assertTrue(downloadedFolder.isDirectory(), "Downloaded folder should be a directory");

    File downloadedJson = new File(downloadedFolder, levelName + ".json");
    assertTrue(downloadedJson.exists(), "Level JSON should exist");

    File downloadedResources = new File(downloadedFolder, "resources");
    assertTrue(downloadedResources.isDirectory(), "Resources folder should exist");

    List<String> resourceFiles = Arrays.asList(Objects.requireNonNull(downloadedResources.list()));
    assertTrue(resourceFiles.contains("platform.png"), "Platform image should exist");
    assertTrue(resourceFiles.contains("enemy.png"), "Enemy image should exist");

    deleteFolderRecursively(tempLevelFolder);
    deleteFolderRecursively(downloadedFolder);

    assertTrue(FirebaseManager.deleteLevelFolder(userId, levelName), "Uploaded level should be deleted after test");
  }

  /* Helper methods */

  private static File createTempLevelFolder(String levelName) throws IOException {
    File tempLevelFolder = Files.createTempDirectory(levelName).toFile();

    File levelJson = new File(tempLevelFolder, levelName + ".json");
    try (FileWriter writer = new FileWriter(levelJson)) {
      writer.write("{ \"name\": \"Test Level\" }");
    }

    File resourcesFolder = new File(tempLevelFolder, "resources");
    assertTrue(resourcesFolder.mkdir());

    File platformImage = new File(resourcesFolder, "platform.png");
    try (FileWriter writer = new FileWriter(platformImage)) {
      writer.write("fake platform data");
    }

    File enemyImage = new File(resourcesFolder, "enemy.png");
    try (FileWriter writer = new FileWriter(enemyImage)) {
      writer.write("fake enemy data");
    }

    return tempLevelFolder;
  }

  private static void deleteFolderRecursively(File folder) {
    if (folder == null || !folder.exists()) return;
    try {
      Files.walk(folder.toPath())
          .sorted(Comparator.reverseOrder())
          .map(Path::toFile)
          .forEach(File::delete);
    } catch (IOException e) {
      System.err.println("Failed to delete temp folder: " + folder.getAbsolutePath());
    }
  }
}
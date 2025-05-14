package oogasalad.model.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import oogasalad.database.DatabaseException;
import oogasalad.database.FirebaseManager;
import oogasalad.model.profile.LevelData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LevelServiceTest {

  private final LevelService levelService = new LevelService();

  @BeforeAll
  static void setUp() throws IOException {
    FirebaseManager.initializeFirebase();
  }

  @Test
  void uploadUserLevel_validLevel_success() throws DatabaseException {
    LevelData levelData = new LevelData();
    String username = "testuser" + System.currentTimeMillis();
    String description = "testDescription";
    String levelName = "testlevel" + System.currentTimeMillis();
    String levelUrl = "https://example.com/test.json";

    levelData.setCreatorUsername(username);
    levelData.setLevelDescription(description);
    levelData.setLevelName(levelName);
    levelData.setLevelUrl(levelUrl);

    assertDoesNotThrow(() -> levelService.uploadUserLevel(levelData));
    assertTrue(levelService.deleteUserLevel(username + "_" + levelName));
  }


  @Test
  void uploadUserLevel_duplicateLevel_throwsError()
      throws DatabaseException, ExecutionException, InterruptedException {
    String username = "testuser_" + System.currentTimeMillis();
    String levelName = "DuplicateLevel";
    String levelUrl = "https://example.com/dupe.json";

    LevelData level = new LevelData();
    level.setCreatorUsername(username);
    level.setLevelName(levelName);
    level.setLevelDescription("Original level.");
    level.setLevelUrl(levelUrl);

    assertTrue(levelService.uploadUserLevel(level));

    Exception exception = assertThrows(DatabaseException.class, () ->
        levelService.uploadUserLevel(level));
    assertTrue(exception.getMessage().contains("already exists"));

    assertTrue(levelService.deleteUserLevel(username + "_" + levelName));
  }

  @Test
  void updateUserLevel_existingLevel_success()
      throws DatabaseException, ExecutionException, InterruptedException {
    String username = "testuser_" + System.currentTimeMillis();
    String levelName = "UpdateLevel";


    LevelData level = new LevelData();
    level.setCreatorUsername(username);
    level.setLevelName(levelName);
    level.setLevelDescription("Original description");
    level.setLevelUrl("https://example.com/original.json");

    String prevLevelName = levelName;

    assertTrue(levelService.uploadUserLevel(level));

    level.setLevelName("NewName");
    level.setLevelDescription("Updated description");
    level.setLevelUrl("https://example.com/updated.json");

    assertTrue(levelService.updateUserLevel(level, prevLevelName));

    assertTrue(levelService.deleteUserLevel(username + "_" + level.getLevelName()));
  }

  @Test
  void updateUserLevel_nonexistentLevel_throwsError() {
    String username = "testUser" + System.currentTimeMillis();
    String levelName = "levelNotExist";

    LevelData level = new LevelData();
    level.setCreatorUsername(username);
    level.setLevelName(levelName);
    level.setLevelDescription("Should not exist");
    level.setLevelUrl("https://example.com/ghost.json");

    Exception exception = assertThrows(DatabaseException.class, () ->
        levelService.updateUserLevel(level, levelName));
    assertTrue(exception.getMessage().contains("does not exist"));
  }

  @Test
  void deleteUserLevel_existingLevel_success()
      throws DatabaseException, ExecutionException, InterruptedException {
    String username = "testuser_" + System.currentTimeMillis();
    String levelName = "DeletableLevel";

    LevelData level = new LevelData();
    level.setCreatorUsername(username);
    level.setLevelName(levelName);
    level.setLevelDescription("Level to be deleted.");
    level.setLevelUrl("https://example.com/delete.json");

    assertTrue(levelService.uploadUserLevel(level));
    assertTrue(levelService.deleteUserLevel(username + "_" + levelName));
  }

  @Test
  void retrieveUserLevels_existingUser_returnsCorrectList()
      throws DatabaseException, ExecutionException, InterruptedException {
    String username = "testuser_" + System.currentTimeMillis();
    String levelName1 = "Level1";
    String levelName2 = "Level2";

    LevelData level1 = new LevelData();
    level1.setCreatorUsername(username);
    level1.setLevelName(levelName1);
    level1.setLevelDescription("First");
    level1.setLevelUrl("https://example.com/1.json");

    LevelData level2 = new LevelData();
    level2.setCreatorUsername(username);
    level2.setLevelName(levelName2);
    level2.setLevelDescription("Second");
    level2.setLevelUrl("https://example.com/2.json");

    assertTrue(levelService.uploadUserLevel(level1));
    assertTrue(levelService.uploadUserLevel(level2));

    List<LevelData> levels = levelService.retrieveUserLevels(username);
    assertEquals(2, levels.size());
    assertTrue(levels.stream().anyMatch(l -> l.getLevelName().equals(levelName1)));
    assertTrue(levels.stream().anyMatch(l -> l.getLevelName().equals(levelName2)));

    assertTrue(levelService.deleteUserLevel(username + "_" + levelName1));
    assertTrue(levelService.deleteUserLevel(username + "_" + levelName2));
  }
}
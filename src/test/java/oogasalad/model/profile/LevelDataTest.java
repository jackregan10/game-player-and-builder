package oogasalad.model.profile;

import static org.junit.jupiter.api.Assertions.*;

import com.google.cloud.Timestamp;
import org.junit.jupiter.api.Test;

class LevelDataTest {
  @Test
  void testSetAndGetCreatorUsername() {
    LevelData levelData = new LevelData();
    levelData.setCreatorUsername("justin123");
    assertEquals("justin123", levelData.getCreatorUsername());
  }

  @Test
  void testSetAndGetLevelName() {
    LevelData levelData = new LevelData();
    levelData.setLevelName("Sky Fortress");
    assertEquals("Sky Fortress", levelData.getLevelName());
  }

  @Test
  void testSetAndGetLevelDescription() {
    LevelData levelData = new LevelData();
    levelData.setLevelDescription("A floating castle level");
    assertEquals("A floating castle level", levelData.getLevelDescription());
  }

  @Test
  void testSetAndGetLevelUrl() {
    LevelData levelData = new LevelData();
    levelData.setLevelUrl("https://storage.googleapis.com/level.json");
    assertEquals("https://storage.googleapis.com/level.json", levelData.getLevelUrl());
  }

  @Test
  void testSetAndGetCreatedAt() {
    LevelData levelData = new LevelData();
    Timestamp now = Timestamp.now();
    levelData.setCreatedAt(now);
    assertEquals(now, levelData.getCreatedAt());
  }

  @Test
  void testSetAndGetUpdatedAt() {
    LevelData levelData = new LevelData();
    Timestamp now = Timestamp.now();
    levelData.setUpdatedAt(now);
    assertEquals(now, levelData.getUpdatedAt());
  }

  @Test
  void testFullLevelData() {
    LevelData levelData = new LevelData();
    String username = "justin123";
    String levelName = "Cactus Dash";
    String description = "Desert-themed level with obstacles";
    String url = "https://storage.googleapis.com/levels/justin123/cactus_dash.json";
    Timestamp created = Timestamp.now();
    Timestamp updated = Timestamp.now();

    levelData.setCreatorUsername(username);
    levelData.setLevelName(levelName);
    levelData.setLevelDescription(description);
    levelData.setLevelUrl(url);
    levelData.setCreatedAt(created);
    levelData.setUpdatedAt(updated);

    assertAll("LevelData fields",
        () -> assertEquals(username, levelData.getCreatorUsername()),
        () -> assertEquals(levelName, levelData.getLevelName()),
        () -> assertEquals(description, levelData.getLevelDescription()),
        () -> assertEquals(url, levelData.getLevelUrl()),
        () -> assertEquals(created, levelData.getCreatedAt()),
        () -> assertEquals(updated, levelData.getUpdatedAt())
    );
  }
}
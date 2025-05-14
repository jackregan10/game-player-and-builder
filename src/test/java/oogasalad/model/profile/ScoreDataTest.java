package oogasalad.model.profile;

import static org.junit.jupiter.api.Assertions.*;

import com.google.cloud.Timestamp;
import org.junit.jupiter.api.Test;

class ScoreDataTest {

  @Test
  void constructor_WithValidInputs_ShouldInitializeCorrectly() {
    String username = "justin";
    String game = "ExampleGame";
    int score = 1500;
    Timestamp createdAt = Timestamp.now();

    ScoreData scoreData = new ScoreData(username, game, score, createdAt);

    assertEquals(username, scoreData.getUsername(), "Username should be set correctly");
    assertEquals(game, scoreData.getGame(), "Game should be set correctly");
    assertEquals(score, scoreData.getScore(), "Score should be set correctly");
    assertEquals(createdAt, scoreData.getCreatedAt(), "Timestamp should be set correctly");
  }

  @Test
  void setters_WithValidInput_ShouldUpdateFieldsCorrectly() {
    ScoreData scoreData = new ScoreData();

    String username = "testUser";
    String game = "Snake";
    int score = 500;
    Timestamp createdAt = Timestamp.now();

    scoreData.setUsername(username);
    scoreData.setGame(game);
    scoreData.setScore(score);
    scoreData.setCreatedAt(createdAt);

    assertEquals(username, scoreData.getUsername(), "Username should be updated correctly");
    assertEquals(game, scoreData.getGame(), "Game should be updated correctly");
    assertEquals(score, scoreData.getScore(), "Score should be updated correctly");
    assertEquals(createdAt, scoreData.getCreatedAt(), "Timestamp should be updated correctly");
  }

  /**
   * Test the default no-argument constructor to ensure it initializes with null values.
   */
  @Test
  void constructor_NoArgs_ShouldInitializeWithNullValues() {
    ScoreData scoreData = new ScoreData();

    assertNull(scoreData.getUsername(), "Username should be null");
    assertNull(scoreData.getGame(), "Game should be null");
    assertEquals(0, scoreData.getScore(), "Score should be 0");
    assertNull(scoreData.getCreatedAt(), "Timestamp should be null");
  }
}
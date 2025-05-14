package oogasalad.model.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import oogasalad.database.DatabaseException;
import oogasalad.database.FirebaseManager;
import oogasalad.model.profile.ScoreData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ScoreServiceTest {
  private final ScoreService scoreService = new ScoreService();

  @BeforeAll
  public static void setUp() throws Exception {
    FirebaseManager.initializeFirebase();
  }

  @Test
  public void saveHighScore_NewHighScore_ReturnTrue() throws DatabaseException {
    String username = "test";
    String game = "test";
    int newScore = 10;

    assertTrue(scoreService.saveHighScore(username, game, newScore));

    // delete the created result
    boolean result = scoreService.deleteScore(username, game);
    assertTrue(result);
  }

  @Test
  public void saveHighScore_ExistingHighScore_ReturnFalse() throws DatabaseException {
    String username = "test";
    String game = "test";
    boolean startResult = scoreService.saveHighScore(username, game, 10);
    assertTrue(startResult);
    boolean sameResult = scoreService.saveHighScore(username, game, 10);
    assertFalse(sameResult, "score is the same");

    boolean deleteResult = scoreService.deleteScore(username, game);
    assertTrue(deleteResult);
  }

  @Test
  public void getHighScore_ExistingHighScore_ReturnScoreForUserData() throws DatabaseException {
    String username = "test";
    String game = "test";
    int newScore = 10;
    boolean newScoreResult = scoreService.saveHighScore(username, game, newScore);
    assertTrue(newScoreResult);

    ScoreData scoreData = scoreService.getHighScoreForUser(username, game);
    assertNotNull(scoreData);
    assertEquals(newScore, scoreData.getScore());
    assertEquals(username, scoreData.getUsername());
    assertEquals(game, scoreData.getGame());

    boolean deleteResult = scoreService.deleteScore(username, game);
    assertTrue(deleteResult);
  }

  @Test
  void getHighScoresForAllUsers_FetchThreeTopUsers_ReturnsSortedTopScores()
      throws DatabaseException, ExecutionException, InterruptedException {
    String game = "test";

    scoreService.saveHighScore("user1", game, 15);
    scoreService.saveHighScore("user2", game, 25);
    scoreService.saveHighScore("user3", game, 10);

    List<ScoreData> scores = scoreService.getHighScoresForAllUsers(game);

    assertNotNull(scores);
    assertTrue(scores.size() >= 3);

    assertEquals("user2", scores.get(0).getUsername());
    assertEquals("user1", scores.get(1).getUsername());
    assertEquals("user3", scores.get(2).getUsername());

    scoreService.deleteScore("user1", game);
    scoreService.deleteScore("user2", game);
    scoreService.deleteScore("user3", game);
  }

  @Test
  void getHighScoresForFollowingUsers_GiveTwoFollowers_ReturnsCorrectSubset() throws DatabaseException, ExecutionException, InterruptedException {
    String game = "test";

    scoreService.saveHighScore("followed1", game, 20);
    scoreService.saveHighScore("followed2", game, 30);
    scoreService.saveHighScore("notfollowed", game, 50);

    List<String> followedUsernames = List.of("followed1", "followed2");

    List<ScoreData> scores = scoreService.getHighScoresForFollowingUsers(game, followedUsernames);

    assertNotNull(scores);
    assertEquals(2, scores.size());

    List<String> usernames = scores.stream().map(ScoreData::getUsername).toList();
    assertTrue(usernames.contains("followed1"));
    assertTrue(usernames.contains("followed2"));
    assertFalse(usernames.contains("notfollowed"));

    scoreService.deleteScore("followed1", game);
    scoreService.deleteScore("followed2", game);
    scoreService.deleteScore("notfollowed", game);
  }

  @Test
  void getHighScoresForFollowingUsers_MoreThan10Users_ReturnsSortedSubset() throws DatabaseException, ExecutionException, InterruptedException {
    String game = "testGame_following";
    List<String> followedUsernames = new ArrayList<>();
    Map<String, Integer> expectedScores = new HashMap<>();

    // Add 15 followed users with increasing scores
    for (int i = 1; i <= 15; i++) {
      String username = "followedUser" + i;
      int score = i * 10;
      followedUsernames.add(username);
      expectedScores.put(username, score);
      scoreService.saveHighScore(username, game, score);
    }

    // Add 2 non-followed users with higher scores (should not appear)
    scoreService.saveHighScore("nonFollowed1", game, 1000);
    scoreService.saveHighScore("nonFollowed2", game, 2000);

    // Call the method under test
    List<ScoreData> result = scoreService.getHighScoresForFollowingUsers(game, followedUsernames);

    // Validate the size
    assertEquals(15, result.size());

    // Validate descending order
    for (int i = 0; i < result.size() - 1; i++) {
      assertTrue(result.get(i).getScore() >= result.get(i + 1).getScore(),
          "Scores should be in descending order");
    }

    // Validate only followed users are present
    List<String> resultUsernames = result.stream().map(ScoreData::getUsername).toList();
    assertTrue(resultUsernames.containsAll(followedUsernames));
    assertFalse(resultUsernames.contains("nonFollowed1"));
    assertFalse(resultUsernames.contains("nonFollowed2"));

    // Cleanup followed users
    for (String username : followedUsernames) {
      scoreService.deleteScore(username, game);
    }

    // Cleanup non-followed users
    scoreService.deleteScore("nonFollowed1", game);
    scoreService.deleteScore("nonFollowed2", game);
  }
}
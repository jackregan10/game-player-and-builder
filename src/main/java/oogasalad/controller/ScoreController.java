package oogasalad.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;
import oogasalad.database.DatabaseException;
import oogasalad.model.config.GameConfig;
import oogasalad.model.profile.PlayerData;
import oogasalad.model.profile.ScoreData;
import oogasalad.model.service.ScoreService;
import oogasalad.model.service.SocialService;

/**
 * ScoreController is the class that separates the model and view logic,
 * calling APIs fetching/pushing scores and high scores
 *
 * @author Justin Aronwald
 */
public class ScoreController {
  private final ScoreService scoreService;
  private final SocialService socialService;

  /**
   * Creates an instance of the ScoreController with the service instantiated
   */
  public ScoreController() {
    this.scoreService = new ScoreService();
    this.socialService = new SocialService("follows");
  }

  /**
   * Calls backend logic to check for an existing high score, and if the new score is bigger, then it saves it.
   *
   * @param username - the unique username of the player
   * @param gameName     - the String name of the game in which the high score was achieved
   * @param newScore - the newScore achiever
   * @return - true if the score was updated/saved, false if not necessary
   * @throws DatabaseException - if the player already exists or a database error occurs
   */
  public boolean handleSavingHighScore(String username, String gameName, int newScore)
      throws DatabaseException {
    try {
      return scoreService.saveHighScore(username, gameName, newScore);
    } catch (DatabaseException e) {
      throw new DatabaseException(GameConfig.getText("scoreStorageError"), e);
    }
  }

  /**
   * Calls backend logic to delete the score associated with the given game name and username
   *
   * @param username - the unique username of the player to delete
   * @param gameName     - the game name (String)
   * @return - true if the player was successfully deleted
   * @throws DatabaseException - if the player does not exist or a database error occurs
   */
  public boolean handleDeleteScore(String username, String gameName) throws DatabaseException {
    try {
      return scoreService.deleteScore(username, gameName);
    } catch (DatabaseException e) {
      throw new DatabaseException(GameConfig.getText("scoreDeleteError"), e);
    }
  }

  /**
   * Calls backend logic to get the ScoreData for a given username and game
   *
   * @param username - the unique username of the player to delete
   * @param gameName - the game name (String)
   * @return - a ScoreData object containing all the relevant information about the high score
   * @throws DatabaseException - if the player does not exist or a database error occurs
   */
  public ScoreData handleGetHighScoreForUser(String username, String gameName) throws DatabaseException {
    try {
      return scoreService.getHighScoreForUser(username, gameName);
    } catch (DatabaseException e) {
      throw new DatabaseException(String.format(GameConfig.getText("highScoreDoesNotExist", username, gameName)), e);
    }
  }

  /**
   * Calls backend logic to fetch the top 100 high scores of users for a certain Game
   *
   * @param gameName - the name of the game
   * @return - a list of the scoreData objects that contain name and score
   * @throws DatabaseException - thrown if there's an interruption or execution error
   */
  public List<ScoreData> handleGetHighScoresForAllUsers(String gameName) throws DatabaseException {
    try {
      return scoreService.getHighScoresForAllUsers(gameName);
    } catch (ExecutionException | InterruptedException e) {
      throw new DatabaseException(GameConfig.getText("fetchHighScoresError"), e);
    }
  }



  /**
   * Calls backend logic to get the top high scores for a specific game
   * for the following list of the user with the username provided
   *
   * @param gameName - the name of the game to retrieve scores for
   * @param username - the username of the user whose followers are fetched
   * @return - a sorted list of ScoreData objects representing the top scores from followed users
   * @throws DatabaseException - if there is an error or interruption executing any Firestore query
   */
  public List<ScoreData> handleGetHighScoresForFollowingUsers(String username, String gameName) throws DatabaseException {
    try {
      List<PlayerData> followedUsers = socialService.getFollowers(username);
      List<String> followedUsernames = followedUsers.stream().map(PlayerData::getUsername).toList();

      return scoreService.getHighScoresForFollowingUsers(gameName, followedUsernames);
    } catch (DatabaseException | ExecutionException | InterruptedException e) {
      throw new DatabaseException(GameConfig.getText("fetchHighScoresError"), e);
    }

  }


}

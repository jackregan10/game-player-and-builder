package oogasalad.model.profile;

import com.google.cloud.Timestamp;

/**
 * This class represents the data of an individual score. This would be a record, but the firebase
 * deserialization doesn't work well with records.
 *
 * @author Justin Aronwald
 */
public class ScoreData {

  private String username;
  private String game;
  private int score;
  private Timestamp createdAt;

  /**
   * This is a required constructor for the Firestone deserialization
   */
  public ScoreData() {
    // Required no-arg constructor for Firestore
  }

  /**
   * Constructor to create a new instance of a score object
   *
   * @param username  - the username of the player who got the score
   * @param game      - the name of the game the score is registered to
   * @param score     - the value of the score
   * @param createdAt - the time the score is registered
   */
  public ScoreData(String username, String game, int score, Timestamp createdAt) {
    this.username = username;
    this.game = game;
    this.score = score;
    this.createdAt = createdAt;
  }

  /**
   * Returns the username of the player who achieved the score
   *
   * @return the username of a player
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username of the player who achieved the score
   *
   * @param username - the player's username
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Returns the name of the game where the score was achieved
   *
   * @return - the String game name
   */
  public String getGame() {
    return game;
  }

  /**
   * Sets the name of the game where the score was achieved
   *
   * @param game - the name of the game
   */
  public void setGame(String game) {
    this.game = game;
  }

  /**
   * Returns the score value achieved by the player
   *
   * @return the score as an integer
   */
  public int getScore() {
    return score;
  }

  /**
   * Sets the score value achieved by the player
   *
   * @param score - the score value to set
   */
  public void setScore(int score) {
    this.score = score;
  }

  /**
   * Returns the timestamp of when the score was recorded
   *
   * @return the timestamp it was created at
   */
  public Timestamp getCreatedAt() {
    return createdAt;
  }

  /**
   * Sets the timestamp of when the score was recorded
   *
   * @param createdAt the timestamp of score creation
   */
  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }
}
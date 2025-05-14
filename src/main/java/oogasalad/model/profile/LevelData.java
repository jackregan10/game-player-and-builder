package oogasalad.model.profile;

import com.google.cloud.Timestamp;

/**
 * A model representing metadata for a user-created level.
 * This data can be stored in Firestore and used to display or manage user-generated content
 *
 * @author Justin Aronwald
 */
public class LevelData {

  private String creatorUsername;
  private String levelName;
  private String levelDescription;
  private String levelUrl;
  private Timestamp createdAt;
  private Timestamp updatedAt;


  /**
   * Getter for the username of the level creator
   * @return - the creator's username
   */
  public String getCreatorUsername() {
    return creatorUsername;
  }

  /**
   * Setter for the username of the level creator
   * @param creatorUsername the username to set
   */
  public void setCreatorUsername(String creatorUsername) {
    this.creatorUsername = creatorUsername;
  }

  /**
   * Getter for the name of the level
   * @return - the level name
   */
  public String getLevelName() {
    return levelName;
  }

  /**
   * Setter for the name/title of the level
   * @param levelName - the level name to set
   */
  public void setLevelName(String levelName) {
    this.levelName = levelName;
  }

  /**
   * Getter for the level description
   * @return - the level description
   */
  public String getLevelDescription() {
    return levelDescription;
  }

  /**
   * Setter for the level description.
   * @param levelDescription -  the description to set
   */
  public void setLevelDescription(String levelDescription) {
    this.levelDescription = levelDescription;
  }

  /**
   * Gets the public Firebase Storage URL for this level
   * @return the level URL
   */
  public String getLevelUrl() {
    return levelUrl;
  }

  /**
   * Setter for the public Firebase Storage URL for this level
   * @param levelUrl - the URL to set
   */
  public void setLevelUrl(String levelUrl) {
    this.levelUrl = levelUrl;
  }

  /**
   * Getter for the creation timestamp of the level.
   * @return - the createdAt timestamp
   */
  public Timestamp getCreatedAt() {
    return createdAt;
  }

  /**
   * Sets the creation timestamp of the level.
   * @param createdAt the timestamp to set
   */
  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * Getter for the last updated timestamp of the level.
   * @return - the updatedAt timestamp
   */
  public Timestamp getUpdatedAt() {
    return updatedAt;
  }

  /**
   * Setter for the last updated timestamp of the level.
   * @param updatedAt - the timestamp to set
   */
  public void setUpdatedAt(Timestamp updatedAt) {
    this.updatedAt = updatedAt;
  }
}
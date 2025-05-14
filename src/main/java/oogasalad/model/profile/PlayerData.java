package oogasalad.model.profile;

import com.google.cloud.Timestamp;
import oogasalad.model.config.PasswordHashingException;

/**
 * This class represents the data of a player profile. It a representation of the data stored in the
 * database This would be a record, but the firebase deserialization doesn't work well with
 * records.
 *
 * @author Justin Aronwald
 */
public class PlayerData {

  private String username;
  private String fullName;
  private Password password;
  private String bio;
  private String imageUrl;
  private Timestamp modifiedAt;
  private Timestamp createdAt;

  /**
   * This a required constructor for the Firestone deserialization
   */
  public PlayerData() {
    //This is a required no-arg constructor for Firestore deserialization
  }

  /**
   * PlayerData constructor for creating a new player on signup
   * @param username - the unique username of a user
   * @param fullName - the full name of the user
   * @param password - the password object of a user
   * @param pfpUrl - the link to a photo stored in a cloud storage bucket
   * @param bio - the short bio of a user
   * @param createdAt - the time a user was created
   * @param modifiedAt - the time at which a user's information is modified
   */
  public PlayerData(String username, String fullName, Password password, Timestamp createdAt, String pfpUrl, String bio,  Timestamp modifiedAt) {
    this.username = username;
    this.password = password;
    this.createdAt = createdAt;
    this.fullName = fullName;
    this.bio = bio;
    this.imageUrl = pfpUrl;
    this.modifiedAt = modifiedAt;
  }

  /**
   * Getter for the username
   *
   * @return - the unique name for a user
   */
  public String getUsername() {
    return username;
  }

  /**
   * Setter for the username of a player
   *
   * @param username - the unique name for a user
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Getter for the full-name
   *
   * @return - the full name of the user
   */
  public String getFullName() {
    return fullName;
  }

  /**
   * Setter for the full name
   *
   * @param fullName - the full name of the users
   */
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  /**
   * Getter for the time a player was created
   *
   * @return - the time at which a player was created
   */
  public Timestamp getCreatedAt() {
    return createdAt;
  }

  /**
   * Setter for the time of which a player is created
   *
   * @param createdAt - the timestamp at which a player is created
   */
  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * Method that delegates verification logic to Password class
   *
   * @param input - the password being tried
   * @return - true if the password is verified
   * @throws PasswordHashingException - if failure occurs throughout the process
   */
  public boolean verifyPassword(String input) throws PasswordHashingException {
    return password.verify(input);
  }

  /**
   * A getter exclusively to be used internally or for Firebase
   *
   * @return - the Password object
   */
  public Password getPassword() {
    return password;
  }

  /**
   * Setter for the password -- exclusively to be used internally or for Firebase
   *
   * @param password - the Password object for a user
   */
  public void setPassword(Password password) {
    this.password = password;
  }

  /**
   * Getter for the user's bio
   *
   * @return - the short bio of the user
   */
  public String getBio() {
    return bio;
  }

  /**
   * Setter for the user's bio
   *
   * @param bio - the short bio of the user
   */
  public void setBio(String bio) {
    this.bio = bio;
  }

  /**
   * Getter for the user's profile photo URL
   *
   * @return - the URL or path to the user's profile photo
   */
  public String getImageUrl() {
    return imageUrl;
  }

  /**
   * Setter for the user's profile photo URL
   *
   * @param pfpUrl - the URL or path to the user's profile photo
   */
  public void setImageUrl(String pfpUrl) {
    this.imageUrl = pfpUrl;
  }

  /**
   * Getter for the last modified timestamp as a string
   *
   * @return - the time the player profile was last modified
   */
  public Timestamp getModifiedAt() {
    return modifiedAt;
  }

  /**
   * Setter for the last modified timestamp as a string
   *
   * @param modifiedAt - the time the player profile was last modified
   */
  public void setModifiedAt(Timestamp modifiedAt) {
    this.modifiedAt = modifiedAt;
  }

  /**
   * Override the toString to protect the password
   *
   * @return - a random string to protect the password
   */
  @Override
  public String toString() {
    return "[PROTECTED]";
  }

}
package oogasalad.model.service;

import static oogasalad.model.config.GameConfig.LOGGER;
import static oogasalad.model.config.GameConfig.getText;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import oogasalad.database.DatabaseException;
import oogasalad.model.config.GameConfig;
import oogasalad.model.config.PasswordHashingException;
import oogasalad.model.profile.PlayerData;
import oogasalad.model.profile.SessionManagement;

/**
 * PlayerService methods for creating, retrieving, and deleting player profiles from the Firestore
 * database
 *
 * @author Justin Aronwald
 */
public class PlayerService extends FirestoreService {

  private static final String COLLECTION_NAME = "players";
  private static final String PLAYER_NOT_EXIST_ERROR_MESSAGE = "playerNotExistError";

  /**
   * Creates a new instance of a player service object with the collection name
   */
  public PlayerService() {
    super(COLLECTION_NAME);
  }

  /**
   * Creates a new player with the given username
   *
   * @param playerData - the playerData object containing all the user information
   * @return - the playerData object
   * @throws DatabaseException - if the player already exists or a database error occurs
   */
  public boolean createNewPlayer(PlayerData playerData)
      throws DatabaseException {
    if (documentExists(playerData.getUsername())) {
      LOGGER.error(getText("playerExistsError"), playerData.getUsername());
      throw new DatabaseException(getText("playerExistsError", playerData.getUsername()));
    }

    Map<String, Object> newPlayerData = new HashMap<>();
    newPlayerData.put("username", playerData.getUsername());
    newPlayerData.put("fullName", playerData.getFullName());
    newPlayerData.put("password", playerData.getPassword());
    newPlayerData.put("createdAt", FieldValue.serverTimestamp());
    newPlayerData.put("bio", playerData.getBio());
    newPlayerData.put("imageUrl", playerData.getImageUrl());
    newPlayerData.put("modifiedAt", null);

    saveToDatabase(playerData.getUsername(), newPlayerData);
    LOGGER.info(getText("createPlayerMessage"), playerData.getUsername());
    return true;
  }


  /**
   * Deletes the player associated with the given username
   *
   * @param username - the unique username of the player to delete
   * @return - true if the player was successfully deleted
   * @throws DatabaseException - if the player does not exist or a database error occurs
   */
  public boolean deletePlayer(String username) throws DatabaseException {
    if (!documentExists(username)) {
      LOGGER.warn(getText(PLAYER_NOT_EXIST_ERROR_MESSAGE), username, COLLECTION_NAME);
      throw new DatabaseException(
          getText(PLAYER_NOT_EXIST_ERROR_MESSAGE, username, COLLECTION_NAME));
    }

    deleteFromDatabase(username);
    LOGGER.info(getText("deletePlayerSuccess", username));
    return true;
  }

  /**
   * Gets the PlayerData for the given username
   *
   * @param username - the unique username of the player
   * @return - the PlayerData object, if it exists
   * @throws DatabaseException if the player does not exist or a database error occurs
   */
  public PlayerData getPlayerByUsername(String username) throws DatabaseException {
    DocumentSnapshot snapshot = getDocument(username);
    if (!snapshot.exists()) {
      LOGGER.warn(getText(PLAYER_NOT_EXIST_ERROR_MESSAGE, username, COLLECTION_NAME));
      throw new DatabaseException(
          getText(PLAYER_NOT_EXIST_ERROR_MESSAGE, username, COLLECTION_NAME));
    }
    return snapshot.toObject(PlayerData.class);
  }

  /**
   * Method to fetch all of the profiles in the database
   *
   * @return - a list of all the PlayerData objects
   * @throws DatabaseException - if there's an interruption or error with Firebase
   */
  public List<PlayerData> getAllPlayers() throws DatabaseException {
    CollectionReference collection = getCollection();
    try {
      ApiFuture<QuerySnapshot> future = collection.get();
      QuerySnapshot snap = future.get();

      return snap.getDocuments().stream()
          .map(document -> document.toObject(PlayerData.class))
          .filter(user -> !user.getUsername().contains("rememberme"))
          .toList();
    } catch (InterruptedException | ExecutionException e) {
      LOGGER.error(getText("Failed to fetch all players database"));
      throw new DatabaseException(getText("Failed to fetch players database"), e);
    }
  }

  /**
   * Endpoint to authenticate and login a user -- adds user to sessionManagement
   *
   * @param username   - the inputted username
   * @param password   - the inputted password
   * @param rememberMe - true if rememberMe checkbox is selected
   */
  public void login(String username, String password, boolean rememberMe)
      throws PasswordHashingException, IOException {
    PlayerData curUser;
    try {
      curUser = getPlayerByUsername(username);

      if (!curUser.verifyPassword(password)) {
        LOGGER.warn(GameConfig.getText("incorrectPassword"));
        throw new PasswordHashingException(GameConfig.getText("incorrectPassword"));
      }

      SessionManagement.getInstance().login(curUser, rememberMe);
      LOGGER.info(GameConfig.getText("successfulLogin", username));
      //switch screens

    } catch (DatabaseException e) {
      LOGGER.error(GameConfig.getText("noUserInDatabase"), e);
      // show on the frontend
    } catch (IOException e) {
      throw new IOException(GameConfig.getText("autoLoginStoreFailure"), e);
    }
  }

  /**
   * Method to handle logging out -- removes the current user from session manager
   */
  public void logout() {
    SessionManagement.getInstance().logout();
    LOGGER.info(GameConfig.getText("successfulLogout"));
    //switch scenes
  }

  /**
   * Updates user information with new information
   *
   * @param playerData - the new data that a player saves
   * @return - true if the action is successful
   * @throws DatabaseException - if there's an issue saving new data
   */
  public boolean updateProfileInformation(PlayerData playerData) throws DatabaseException {
    if (!documentExists(playerData.getUsername())) {
      throw new DatabaseException(GameConfig.getText("noSuchPlayer"));
    }

    Map<String, Object> updatedData = new HashMap<>();
    updatedData.put("fullName", playerData.getFullName());
    updatedData.put("bio", playerData.getBio());
    updatedData.put("imageUrl", playerData.getImageUrl());
    updatedData.put("modifiedAt", FieldValue.serverTimestamp());

    try {
      updateDatabase(playerData.getUsername(), updatedData);
      return true;
    } catch (DatabaseException e) {
      throw new DatabaseException(getText("databaseUpdateError", playerData.getUsername()), e);
    }
  }

}
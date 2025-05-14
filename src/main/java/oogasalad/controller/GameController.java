package oogasalad.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import oogasalad.database.DatabaseException;
import oogasalad.database.FirebaseManager;
import oogasalad.model.config.GameConfig;
import oogasalad.model.config.PasswordHashingException;
import oogasalad.model.profile.Password;
import oogasalad.model.profile.PlayerData;
import oogasalad.model.profile.SessionManagement;
import oogasalad.model.profile.SignUpRequest;
import oogasalad.model.service.PlayerService;
import oogasalad.view.gui.error.PopUpError;
import oogasalad.view.scene.MainViewManager;

import static oogasalad.model.config.GameConfig.DEFAULT_PFP_URL;
import static oogasalad.model.config.GameConfig.LOGGER;
import static oogasalad.model.config.GameConfig.getText;

/**
 * GameController is a class to separate model and view logic and
 * calls the APIs fetching/pushing relating to the player
 *
 * @author Justin Aronwald
 *
 */
public class GameController {

  private final MainViewManager mainViewManager;
  private static final String PLAYER_NOT_EXIST_ERROR_MESSAGE = "playerNotExistError";
  private final PlayerService playerService;

  /**
   * Creates an instance of the GameController object
   */
  public GameController() {
    this.mainViewManager = MainViewManager.getInstance();
    this.playerService = new PlayerService();
  }

  /**
   * Method that is called by the view to handle login
   *
   * @param username - the inputted username
   * @param password - the inputted password
   * @return - true if login is successful
   */
  public boolean handleLogin(String username, String password, boolean rememberMe)
      throws IOException {
    try {
      playerService.login(username, password, rememberMe);
      if (SessionManagement.getInstance().isLoggedIn()) {
        mainViewManager.switchTo("MainMenuScene");
        return true;
      }
    } catch (PasswordHashingException e) {
      LOGGER.warn("Error");
      //Show error on the frontend
    } catch (IOException e) {
      throw new IOException(GameConfig.getText("autoLoginError"), e);
    }
    return false;
  }

  /**
   * Method called by the view to handle logging out
   */
  public void handleLogout() {
    playerService.logout();
    mainViewManager.switchTo("LogInScene");
  }

  /**
   * Method called by the view to sign a user up
   *
   * @param signUpRequest - object containing the strings used to log in
   * @throws PasswordHashingException - true if there's an issue hashing the password
   * @throws DatabaseException        - true if there's a database error or username exists
   */
  public void handleSignUp(SignUpRequest signUpRequest)
      throws PasswordHashingException, DatabaseException, IOException {
    String fullName = signUpRequest.firstName() + " " + signUpRequest.lastName();
    String imageUrl = getImageUrlForSignUp(signUpRequest);
    try {
      createNewPlayerInFirebase(signUpRequest, fullName, imageUrl);
      handleLogin(signUpRequest.username(), signUpRequest.password(), false);
    } catch (PasswordHashingException e) {
      throw new PasswordHashingException(GameConfig.getText("errorHashingPassword"), e);
    } catch (DatabaseException e) {
      throw new DatabaseException(GameConfig.getText("usernameAlreadyExists"), e);
    }
  }

  private String getImageUrlForSignUp(SignUpRequest request) throws DatabaseException {
    if (request.pfp() == null) {
      return DEFAULT_PFP_URL;
    }
    try {
      return FirebaseManager.uploadPfp(request.username(), request.pfp());
    } catch (IOException e) {
      throw new DatabaseException(GameConfig.getText("errorSavingUserPhoto"), e);
    }
  }

  private void createNewPlayerInFirebase(SignUpRequest signUpRequest, String fullName, String imageUrl)
      throws DatabaseException, PasswordHashingException {
    PlayerData newPlayerData = new PlayerData();
    newPlayerData.setUsername(signUpRequest.username());
    newPlayerData.setFullName(fullName);
    newPlayerData.setPassword(Password.fromPlaintext(signUpRequest.password()));
    newPlayerData.setBio(signUpRequest.bio());
    newPlayerData.setImageUrl(imageUrl);

    if (playerService.createNewPlayer(newPlayerData)) {
      mainViewManager.switchTo("MainMenuScene");
    }
  }

  /**
   * Method called by the view to update a profile
   *
   * @param updatedPlayer - the new data that a player saves
   * @return - true if the action is successful
   * @throws DatabaseException - if there's an issue saving new data
   */
  public boolean handleUpdateProfile(PlayerData updatedPlayer, File newImageFile) throws DatabaseException {
    setNewImageUrl(updatedPlayer, newImageFile);

    try {
      if (playerService.updateProfileInformation(updatedPlayer)) {
        LOGGER.info(GameConfig.getText("profileUpdated"));
        return true;
      }
      return false;
    } catch (DatabaseException e) {
      throw new DatabaseException(getText("databaseUpdateError", updatedPlayer.getUsername()), e);
    }
  }

  private static void setNewImageUrl(PlayerData updatedPlayer, File newImageFile)
      throws DatabaseException {
    if (newImageFile != null) {
      try {
        String imageUrl = FirebaseManager.uploadPfp(updatedPlayer.getUsername(), newImageFile);
        updatedPlayer.setImageUrl(imageUrl);
      } catch (IOException e) {
        throw new DatabaseException(GameConfig.getText("errorUploadingProfilePhoto"), e);
      }
    }
  }

  /**
   * Gets the PlayerData for the given username
   *
   * @param username - the unique username of the player
   * @return - the PlayerData object, if it exists
   * @throws DatabaseException if the player does not exist or a database error occurs
   */
  public PlayerData getPlayerByUsername(String username) throws DatabaseException {
    try {
      return playerService.getPlayerByUsername(username);
    } catch (DatabaseException e) {
      throw new DatabaseException(getText(PLAYER_NOT_EXIST_ERROR_MESSAGE, username), e);
    }
  }

  /**
   * Method to get every single account in the game
   * @return A list containing PlayerData type
   * @throws DatabaseException if failed to get users from database
   */
  public List<PlayerData> getAllPlayers() throws DatabaseException {
    try {
      return playerService.getAllPlayers();
    } catch (DatabaseException e) {
      throw new DatabaseException(GameConfig.getText("couldNotFetchPlayerList"), e);
    }
  }

}

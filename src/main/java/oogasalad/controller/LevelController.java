package oogasalad.controller;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import oogasalad.database.DatabaseException;
import oogasalad.database.FirebaseManager;
import oogasalad.model.config.GameConfig;
import oogasalad.model.profile.LevelData;
import oogasalad.model.profile.SessionException;
import oogasalad.model.profile.SessionManagement;
import oogasalad.model.service.LevelService;

/**
 * LevelController separates model and view logic and controls the
 * APIs for fetching and pushing user-generated levels
 *
 * @author Justin Aronwald
 */
public class LevelController {
  private final LevelService levelService;

  /**
   * Creates an instance of the level controller
   *
   */
  public LevelController() {
    this.levelService = new LevelService();
  }


  /**
   * Method to upload a user level to the Firestore database
   *
   * @param userLevel - the LevelData object storing all the information
   * @param levelFile - the file representation of the level to store in blob storage
   * @throws DatabaseException - if there's an error storing the information
   */
  public void handleUploadUserLevel(LevelData userLevel, File levelFile)
      throws DatabaseException {
    setImageLinkForLevelData(userLevel, levelFile);

    try {
      levelService.uploadUserLevel(userLevel);
    } catch (ExecutionException | InterruptedException e) {
      throw new DatabaseException(GameConfig.getText("levelStorageFailure"), e);
    }
  }

  private void setImageLinkForLevelData(LevelData userLevel, File levelFile) throws DatabaseException {
    String levelFileUrl = getLevelUrl(userLevel.getCreatorUsername(), userLevel.getLevelName(),
        levelFile);
    userLevel.setLevelUrl(levelFileUrl);
  }

  /**
   * Method to call backend logic to update a user level stored in the database
   *
   * @param userLevel - the LevelData object storing new data
   * @throws DatabaseException - if the level does not exist or update fails
   */
  public void handleUpdatingLevel(LevelData userLevel, File levelFile, String previousLevelName)
      throws DatabaseException {
    setImageLinkForLevelData(userLevel, levelFile);

    try {
      levelService.updateUserLevel(userLevel, previousLevelName);
    } catch (ExecutionException | InterruptedException e) {
      throw new DatabaseException(GameConfig.getText("levelUpdateFailure"), e);
    }
  }

  private String getLevelUrl(String username, String levelName, File levelFile)
      throws DatabaseException {
    FirebaseManager.uploadLevelToFirebase(username, levelName, levelFile);
    return buildLevelJsonUrl(username, levelName);
  }

  /**
   * Calls backend logic to delete user level at a given id
   *
   * @param levelName - the unsanitized name of a level
   * @throws DatabaseException - if there's an error with deleting
   */
  public void handleDeleteUserLevel(String levelName) throws DatabaseException {
    try {
      String username = SessionManagement.getInstance().getCurrentUser().getUsername();
      String sanitizedLevelName = levelName.replaceAll(" ", "_");
      boolean fileDeleted = FirebaseManager.deleteLevelFolder(username, sanitizedLevelName);
      if (!fileDeleted) {
        throw new DatabaseException(GameConfig.getText("levelDeleteFailureS3"));
      }

      String levelId = username + "_" + levelName;
      levelService.deleteUserLevel(levelId);
    } catch (DatabaseException | SessionException e) {
      throw new DatabaseException(GameConfig.getText("levelDeleteFailure"), e);
    }
  }

  /**
   * Calls backend logic to fetch all the userLevels created by a specific user
   *
   * @param creatorUsername - the creator username of all the levels
   * @return - a list of all the LevelData objects
   * @throws DatabaseException - if any errors occur with fetching levels
   */
  public List<LevelData> handleRetrieveUserLevels(String creatorUsername) throws DatabaseException {
    List<LevelData> userLevels;

    try {
      userLevels = levelService.retrieveUserLevels(creatorUsername);
    } catch (InterruptedException | ExecutionException e) {
      throw new DatabaseException(GameConfig.getText("levelRetrievalFailure"), e);
    }

    return userLevels;
  }

  private String buildLevelJsonUrl(String username, String levelName) {
    String sanitizedLevelName = levelName.replaceAll(" ", "_");

    return String.format(
        "https://storage.googleapis.com/%s/levels/%s/%s/%s.json",
        "oogasalad-a908c.appspot.com",
        username,
        sanitizedLevelName,
        sanitizedLevelName
    );
  }
}

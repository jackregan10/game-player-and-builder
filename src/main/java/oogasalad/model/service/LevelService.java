package oogasalad.model.service;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import oogasalad.database.DatabaseException;
import oogasalad.model.config.GameConfig;
import oogasalad.model.profile.LevelData;

import static oogasalad.model.config.GameConfig.LOGGER;


/**
 * LevelService methods are used to create, retrieve, and delete user generated levels
 * from the database
 *
 * @author Justin Aronwald
 */
public class LevelService extends FirestoreService{
  private static final String COLLECTION_NAME = "levels";
  private static final String CREATOR_USERNAME = "creatorUsername";
  private static final String CREATED_AT = "createdAt";

  /**
   * Creates a new instance of the Firestore service with the collection name
   *
   */
  public LevelService() {
    super(COLLECTION_NAME);
  }

  /**
   * Method to upload a user level to the Firestore database
   *
   * @param levelData - the LevelData object storing all the information
   * @return - true if successful
   * @throws ExecutionException - if execution errors out
   * @throws InterruptedException - if execution is interrupted
   * @throws DatabaseException - if there's an error storing the information
   */
  public boolean uploadUserLevel(LevelData levelData)
      throws ExecutionException, InterruptedException, DatabaseException {
    String docId = levelData.getCreatorUsername() + "_" + levelData.getLevelName();

    if (documentExists(docId)) {
      throw new DatabaseException(GameConfig.getText("levelAlreadyExists"));
    }

    Map<String, Object> levelMap = buildLevelMap(levelData, docId);
    return uploadLevelToDatabase(docId, levelMap);
  }

  private boolean uploadLevelToDatabase(String docId, Map<String, Object> levelMap) throws DatabaseException {
    try {
      saveToDatabase(docId, levelMap);
      return true;
    } catch (DatabaseException e) {
      throw new DatabaseException(GameConfig.getText("levelStorageFailure"), e);
    }
  }

  private Map<String, Object> buildLevelMap(LevelData levelData, String docId) {
    Map<String, Object> levelMap = new HashMap<>();
    levelMap.put("id", docId);
    levelMap.put(CREATOR_USERNAME, levelData.getCreatorUsername());
    levelMap.put("levelName", levelData.getLevelName());
    levelMap.put("levelDescription", levelData.getLevelDescription());
    levelMap.put("levelUrl", levelData.getLevelUrl());
    levelMap.put(CREATED_AT, FieldValue.serverTimestamp());
    levelMap.put("updatedAt", null);
    return levelMap;
  }

  /**
   * Backend logic to update a user level stored in the database
   *
   * @param levelData - the LevelData object storing new data
   * @return - true if the update occurs
   * @throws ExecutionException - if Firestore execution fails
   * @throws InterruptedException - if execution is interrupted
   * @throws DatabaseException - if the level does not exist or update fails
   */
  public boolean updateUserLevel(LevelData levelData, String prevLevelName)
      throws ExecutionException, InterruptedException, DatabaseException {
    String oldDocId = levelData.getCreatorUsername() + "_" + prevLevelName;
    if (!documentExists(oldDocId)) {
      throw new DatabaseException(GameConfig.getText("levelDoesNotExist"));
    }

    String newId = levelData.getCreatorUsername() + "_" + levelData.getLevelName();
    String newName = levelData.getLevelName();

    Map<String, Object> levelMap = new HashMap<>();
    levelMap.put("id", newId);
    levelMap.put(CREATOR_USERNAME, levelData.getCreatorUsername());
    levelMap.put("levelName", newName);
    levelMap.put("levelUrl", levelData.getLevelUrl());
    levelMap.put("levelDescription", levelData.getLevelDescription());
    levelMap.put(CREATED_AT, levelData.getCreatedAt());
    levelMap.put("updatedAt", FieldValue.serverTimestamp());

    try {
      deleteFromDatabase(oldDocId);
      saveToDatabase(newId, levelMap);
      return true;
    } catch (DatabaseException e) {
      throw new DatabaseException(GameConfig.getText("levelStorageFailure"), e);
    }
  }

  /**
   * Deletes a user level from the database
   *
   * @param levelId - the unique id of the level
   * @return - true if the level is deleted successfully
   * @throws DatabaseException - if there's an error deleting
   */
  public boolean deleteUserLevel(String levelId) throws DatabaseException {
    if (!documentExists(levelId)) {
      throw new DatabaseException(GameConfig.getText("levelDoesNotExist"));
    }

    deleteFromDatabase(levelId);
    LOGGER.info(GameConfig.getText("successfulDeletion"));
    return true;
  }

  /**
   * Fetches all the userLevels created by a specific user
   *
   * @param creatorUsername - the creator username of all the levels
   * @return - a list of all the LevelData objects
   * @throws ExecutionException - if Firestore execution fails
   * @throws InterruptedException - if execution is interrupted
   */
  public List<LevelData> retrieveUserLevels(String creatorUsername)
      throws ExecutionException, InterruptedException {
    CollectionReference levels = getCollection();

    List<LevelData> levelList = new ArrayList<>();

    Query query = levels
        .whereEqualTo(CREATOR_USERNAME, creatorUsername)
        .orderBy(CREATED_AT, Query.Direction.DESCENDING);

    QuerySnapshot querySnapshot =  query.get().get();

    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
      LevelData level = document.toObject(LevelData.class);
      levelList.add(level);
    }

    LOGGER.info(GameConfig.getText("levelRetrieved"), levelList.size(), creatorUsername);
    return levelList;
  }






}

package oogasalad.model.service;

import static oogasalad.model.config.GameConfig.getText;
import static oogasalad.model.config.GameConfig.LOGGER;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.Query.Direction;
import com.google.cloud.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import oogasalad.database.DatabaseException;
import oogasalad.model.profile.ScoreData;

/**
 * PlayerService methods for creating, retrieving, and deleting player profiles from the Firestore
 * database
 *
 * @author Justin Aronwald
 */
public class ScoreService extends FirestoreService {

  private static final String COLLECTION_NAME = "scores";
  private static final String SCORE = "score";
  private static final String USERNAME = "username";
  private static final String GAME = "game";
  private static final String SCORE_NOT_EXIST_ERROR_MESSAGE = "scoreNotExistError";
  private static final int MAX_HIGH_SCORES = 100;

  /**
   * Creates an instance of the score service to use in other
   * classes with the collection name predefined
   *
   */
  public ScoreService() {
    super(COLLECTION_NAME);
  }

  /**
   * Checks for an existing high score, and if the new score is bigger, then it saves it.
   *
   * @param username - the unique username of the player
   * @param game     - the String name of the game in which the high score was achieved
   * @param newScore - the newScore achiever
   * @return - true if the score was updated/saved, false if not necessary
   * @throws DatabaseException - if the player already exists or a database error occurs
   */
  public boolean saveHighScore(String username, String game, int newScore)
      throws DatabaseException {
    String docId = username + "_" + game;

    DocumentSnapshot curScore = getDocument(docId);

    if (curScore.exists()) {
      Long existingScore = curScore.getLong(SCORE);

      if (existingScore != null && existingScore >= newScore) {
        LOGGER.info(getText("existingScoreMessage", existingScore, username, game, newScore));
        return false;
      }
    }

    return saveOrUpdateScore(username, game, newScore, docId);
  }

  /**
   * Works to either save a field if there isn't one already, otherwise it overwrites it.
   */
  private boolean saveOrUpdateScore(String username, String game, int newScore, String docId)
      throws DatabaseException {
    Map<String, Object> scoreData = new HashMap<>();
    scoreData.put(USERNAME, username);
    scoreData.put(SCORE, newScore);
    scoreData.put(GAME, game);
    scoreData.put("createdAt", FieldValue.serverTimestamp());

    saveToDatabase(docId, scoreData);
    LOGGER.info(getText("saveUpdateScoreMessage", newScore, username, game));
    return true;
  }

  /**
   * Deletes the score associated with the given game name and username
   *
   * @param username - the unique username of the player to delete
   * @param game     - the game name (String)
   * @return - true if the player was successfully deleted
   * @throws DatabaseException - if the player does not exist or a database error occurs
   */
  public boolean deleteScore(String username, String game) throws DatabaseException {
    String docId = username + "_" + game;

    if (!documentExists(docId)) {
      LOGGER.warn(getText(SCORE_NOT_EXIST_ERROR_MESSAGE, docId, COLLECTION_NAME));
      return false;
    }

    deleteFromDatabase(docId);
    LOGGER.info(getText("deletePlayerSuccess", username));
    return true;
  }

  /**
   * Gets the ScoreData for a given username and game
   *
   * @param username - the unique username of the player to delete
   * @param game     - the game name (String)
   * @return - a ScoreData object containing all the relevant information about the high score
   * @throws DatabaseException - if the player does not exist or a database error occurs
   */
  public ScoreData getHighScoreForUser(String username, String game) throws DatabaseException {
    String docId = username + "_" + game;
    DocumentSnapshot highScore = getDocument(docId);
    if (!highScore.exists()) {
      LOGGER.warn(getText(SCORE_NOT_EXIST_ERROR_MESSAGE, docId, COLLECTION_NAME));
      throw new DatabaseException(getText(SCORE_NOT_EXIST_ERROR_MESSAGE, docId, COLLECTION_NAME));
    }
    return highScore.toObject(ScoreData.class);
  }

  /**
   * Fetches the top 100 high scores of users for a certain Game
   *
   * @param game - the name of the game
   * @return - a list of the scoreData objects that contain name and score
   * @throws ExecutionException - thrown if there's an error in query execution
   * @throws InterruptedException - thrown if there's an interruption in query execution
   */
  public List<ScoreData> getHighScoresForAllUsers(String game)
      throws ExecutionException, InterruptedException {
    CollectionReference scores = getCollection();

    List<ScoreData> scoresList = new ArrayList<>();

    Query query = scores
        .whereEqualTo(GAME, game)
        .orderBy(SCORE, Direction.DESCENDING)
        .limit(MAX_HIGH_SCORES);

    QuerySnapshot querySnapshot = query.get().get();

    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
      ScoreData score = document.toObject(ScoreData.class);
      scoresList.add(score);
    }
    return scoresList;
  }

  /**
   * Had a little help from ChatGPT with chunking logic
   * <p>
   * Gets the top high scores for a specific game, for users in the provided following list
   * Firestore's query limitations require chunking the user list into groups of 10 due to the
   * `whereIn` clause restriction. Each chunk is queried independently, and the resulting scores
   * are merged, sorted by score in descending order, and trimmed to the maximum allowed size.
   *
   * @param game - the name of the game to retrieve scores for
   * @param followedUsernames - the list of usernames the current user follows
   * @return - a sorted list of ScoreData objects representing the top scores from followed users
   * @throws ExecutionException - if there is an error executing any Firestore query
   * @throws InterruptedException  - if any Firestore operation is interrupted
   */
  public List<ScoreData> getHighScoresForFollowingUsers(String game, List<String> followedUsernames)
    throws ExecutionException, InterruptedException {

    CollectionReference scores = getCollection();
    List<ApiFuture<QuerySnapshot>> futures = new ArrayList<>();

    handleChunkingQueries(game, followedUsernames, scores, futures);
    List<ScoreData> combined = new ArrayList<>();
    createScoreDataObjects(futures, combined);

    return combined.stream()
        .sorted(Comparator.comparingInt(ScoreData::getScore).reversed())
        .limit(MAX_HIGH_SCORES)
        .collect(Collectors.toList());
    }

  /**
   * Resolves the query futures and extracts ScoreData objects from the resulting document snapshots.
   * The deserialized objects are added to the provided list.
   *
   * @param futures - the list of Firestore query futures
   * @param combined - the list to populate with deserialized ScoreData objects
   * @throws ExecutionException - if there is an error resolving any query
   * @throws InterruptedException - if any query is interrupted during resolution
   */
  private static void createScoreDataObjects(List<ApiFuture<QuerySnapshot>> futures, List<ScoreData> combined)
      throws InterruptedException, ExecutionException {
    for (ApiFuture<QuerySnapshot> future : futures) {
      for (DocumentSnapshot doc : future.get().getDocuments()) {
        combined.add(doc.toObject(ScoreData.class));
      }
    }
  }

  /**
   * Breaks the followed usernames into chunks of 10 (due to Firestore's whereIn limit) and creates
   * Firestore queries to fetch scores for each chunk
   *
   * @param game - the game name
   * @param followedUsernames - the full list of followed usernames
   * @param scores - the Firestore CollectionReference to the scores collection
   * @param futures - the list to populate with query futures
   */
  private static void handleChunkingQueries(String game, List<String> followedUsernames,
      CollectionReference scores, List<ApiFuture<QuerySnapshot>> futures) {
    for (int i = 0; i < followedUsernames.size(); i += 10) {
      List<String> chunk = followedUsernames.subList(i, Math.min(i + 10, followedUsernames.size()));

      Query query = scores
          .whereEqualTo(GAME, game)
          .whereIn(USERNAME, chunk)
          .orderBy(SCORE, Direction.DESCENDING)
          .limit(MAX_HIGH_SCORES);

      futures.add(query.get());
    }
  }
}

package oogasalad.model.service;

import static oogasalad.model.config.GameConfig.LOGGER;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import oogasalad.database.DatabaseException;
import oogasalad.model.config.GameConfig;
import oogasalad.model.profile.PlayerData;

/**
 * SocialService creates backend endpoint for follower/following and social endpoints used in leaderboard
 *
 * @author Justin Aronwald
 */
public class SocialService extends FirestoreService {
  private static final String FOLLOWER = "follower";
  private static final String FOLLOWING = "following";

  private final PlayerService playerService;

  /**
   * Create an instance of the score service with a certain collection name
   *
   * @param collectionName - the name of the collection
   */
  public SocialService(String collectionName) {
    super(collectionName);
    this.playerService = new PlayerService();
  }


  /**
   * Endpoint to allow one user to follow another user
   *
   * @param follower - the user requesting the follow
   * @param following - the user being followed
   * @return - true if the action is successful
   * @throws DatabaseException - thrown if there's an error storing the data
   */
  public boolean followUser(String follower, String following) throws DatabaseException {
    String docId = follower + "_" + following;
    handleFollowErrors(follower, following, docId);

    Map<String, Object> relationship = new HashMap<>();
    relationship.put(FOLLOWER, follower);
    relationship.put(FOLLOWING, following);
    relationship.put("createdAt", FieldValue.serverTimestamp());
    saveToDatabase(docId, relationship);
    LOGGER.info("Could not determine whether {} follows {}", follower, following);
    return true;
  }

  private void handleFollowErrors(String follower, String following, String docId)
      throws DatabaseException {
    if (documentExists(docId)) {
      LOGGER.error(GameConfig.getText("relationshipAlreadyExists"));
      throw new DatabaseException("Follower " + follower + " already follows " + following);
    }

    handleSelfFollowError(follower, following);
  }

  private void handleSelfFollowError(String follower, String following) throws DatabaseException {
    if (follower.equals(following)) {
      throw new DatabaseException(GameConfig.getText("userUnfollowSelf"));
    }
  }

  /**
   * Endpoint to allow one user to unfollow another user
   *
   * @param follower - the user requesting the unfollow
   * @param following - the user being unfollowed
   * @return - true if the action is successful
   * @throws DatabaseException - thrown if there's an error deleting the data
   */
  public boolean unfollowUser(String follower, String following) throws DatabaseException {
    String docId = follower + "_" + following;
    if (!documentExists(docId)) {
      LOGGER.error(GameConfig.getText("Relationship doesn't exists"));
      throw new DatabaseException(GameConfig.getText("noFollow", follower, following));
    }
    handleSelfFollowError(follower, following);

    deleteFromDatabase(docId);
    LOGGER.info(GameConfig.getText("unfollow", follower, following));
    return true;
  }

  /**
   * Endpoint that fetches all PlayerData followers of a user
   *
   * @param username - the username getting the fetches
   * @return - a list of all of the players that follows the user
   * @throws DatabaseException - if there's an error fetching the data
   */
  public List<PlayerData> getFollowers(String username) throws DatabaseException {
    CollectionReference follows = getCollection();
    return getPlayerData(username, follows, FOLLOWING, FOLLOWER);
  }

  private List<PlayerData> getPlayerData(String username, CollectionReference follows,
      String following, String follower) throws DatabaseException {
    List<PlayerData> followers = new ArrayList<>();
    try {
      // Query all docs in 'follows' where 'following' == username
      ApiFuture<QuerySnapshot> query = follows
          .whereEqualTo(following, username)
          .get();

      List<QueryDocumentSnapshot> docs = query.get().getDocuments();

      for (QueryDocumentSnapshot doc : docs) {
        String followerUsername = doc.getString(follower);
        PlayerData followerData = playerService.getPlayerByUsername(followerUsername);
        followers.add(followerData);
      }

      return followers;
    } catch (InterruptedException | ExecutionException e) {
      throw new DatabaseException(GameConfig.getText("failedToFetchFollowers", username), e);
    }
  }

  /**
   * Endpoint that fetches all PlayerData that a user is following
   *
   * @param username - the username getting the fetches
   * @return - a list of all of the players that the user follows
   * @throws DatabaseException - if there's an error fetching the data
   */
  public List<PlayerData> getFollowing(String username) throws DatabaseException {
    CollectionReference follows = getCollection();
    return getPlayerData(username, follows, FOLLOWER, FOLLOWING);
  }


}

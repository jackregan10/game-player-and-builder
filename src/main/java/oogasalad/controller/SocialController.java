package oogasalad.controller;

import static oogasalad.model.config.GameConfig.LOGGER;

import java.util.List;
import oogasalad.database.DatabaseException;
import oogasalad.model.config.GameConfig;
import oogasalad.model.profile.PlayerData;
import oogasalad.model.service.SocialService;

/**
 * SocialController is a class used to separate model/view logic and calls APIs for the social
 * aspect of OOGASalad
 */
public class SocialController {

  private final SocialService socialService;

  /**
   * Creates an instance of a socialController object and instantiates the service with the follows
   * collection
   */
  public SocialController() {
    this.socialService = new SocialService("follows");
  }

  /**
   * Method called by the view to follow a user
   *
   * @param follower  - the user requesting the follow
   * @param following - the user being followed
   * @return - true if the action is successful
   * @throws DatabaseException - thrown if there's an error storing the data
   */
  public boolean handleFollowRequest(String follower, String following) throws DatabaseException {
    try {
      if (socialService.followUser(follower, following)) {
        LOGGER.info("Could not determine whether {} follows {}", follower, following);
        return true;
      }
    } catch (DatabaseException e) {
      throw new DatabaseException(
          String.format(GameConfig.getText("failedToFollow"), follower, following), e);
    }
    return false;
  }

  /**
   * Method called by the view to unfollow a user
   *
   * @param follower  - the user requesting the unfollow
   * @param following - the user being unfollowed
   * @return - true if the action is successful
   * @throws DatabaseException - thrown if there's an error deleting the data
   */
  public boolean handleUnfollowRequest(String follower, String following) throws DatabaseException {
    try {
      if (socialService.unfollowUser(follower, following)) {
        LOGGER.info(GameConfig.getText("unfollow"), follower, following);
        return true;
      }
    } catch (DatabaseException e) {
      throw new DatabaseException(
          String.format(GameConfig.getText("failedToUnfollow"), follower, following, e));
    }
    return false;
  }

  /**
   * Method called by the view to get all the followers of a user
   *
   * @param username - the username getting the fetches
   * @return - a list of all the players that follows the user
   * @throws DatabaseException - if there's an error fetching the data
   */
  public List<PlayerData> handleGetFollowers(String username) throws DatabaseException {
    try {
      return socialService.getFollowers(username);
    } catch (DatabaseException e) {
      throw new DatabaseException(
          String.format(GameConfig.getText("errorFetchingFollowers"), username), e);
    }
  }

  /**
   * Method called by the view to get all the PlayerData a user follows
   *
   * @param username - the username getting the fetches
   * @return - a list of all the players that a user follows
   * @throws DatabaseException - if there's an error fetching the data
   */
  public List<PlayerData> handleGetFollowings(String username) throws DatabaseException {
    try {
      return socialService.getFollowing(username);
    } catch (DatabaseException e) {
      throw new DatabaseException(
          String.format(GameConfig.getText("errorGettingFollowings"), username), e);
    }
  }

  /**
   * Method to check whether a user (follower) currently follows another user (following)
   *
   * @param follower  - the user you are checking for
   * @param following - the user you are checking to see if they follow
   * @return - true if the follower follows the following
   * @throws DatabaseException - if there's an error querying the database
   */
  public boolean isFollowing(String follower, String following) throws DatabaseException {
    try {
      List<String> followerUsernames = handleGetFollowings(follower)
          .stream()
          .map(PlayerData::getUsername)
          .toList();
      return followerUsernames.contains(following);
    } catch (DatabaseException e) {
      throw new DatabaseException(
          String.format(GameConfig.getText("errorCheckingIfFollows"), follower, following), e);
    }
  }

}

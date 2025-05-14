package oogasalad.model.service;

import static org.junit.jupiter.api.Assertions.*;

import com.google.cloud.Timestamp;
import java.util.List;
import oogasalad.database.DatabaseException;
import oogasalad.database.FirebaseManager;
import oogasalad.model.profile.Password;
import oogasalad.model.profile.PlayerData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SocialServiceTest {
  private final SocialService socialService= new SocialService("follows");
  private final PlayerService playerService = new PlayerService();

  @BeforeEach
  void setUp() throws Exception {
    FirebaseManager.initializeFirebase();

    assertDoesNotThrow(() -> {
      playerService.createNewPlayer(new PlayerData("t1", "p1", Password.fromPlaintext("t1"), Timestamp.now(), null, null, null));
    });
    assertDoesNotThrow(() -> {
      playerService.createNewPlayer(new PlayerData("t2", "p2", Password.fromPlaintext("t2"), Timestamp.now(), null, null, null));
    });

    if (playerService.documentExists("t3")) {
      playerService.deletePlayer("t3");
    }

  }

  @AfterEach
  void tearDown() throws Exception {
    boolean deleteResult = playerService.deletePlayer("t1");
    assertTrue(deleteResult);

    boolean deleteResult2 = playerService.deletePlayer("t2");
    assertTrue(deleteResult2);

    if (playerService.documentExists("t3")) {
      playerService.deletePlayer("t3");
    }
  }

  @Test
  void followAndUnfollowUser_knownUsers_success() throws DatabaseException {
    boolean followResult = socialService.followUser("t1", "t2");
    assertTrue(followResult);

    boolean unfollowResult = socialService.unfollowUser("t1", "t2");
    assertTrue(unfollowResult);
  }

  @Test
  void followAndUnfollowUser_sameName_fail() {
    assertThrows(DatabaseException.class, () -> socialService.followUser("t1", "t1"));
  }

  @Test
  void followUser_duplicateFollow_throwsException() throws DatabaseException {
    socialService.followUser("t1", "t2");
    assertThrows(DatabaseException.class, () -> socialService.followUser("t1", "t2"));
    boolean unfollowResult = socialService.unfollowUser("t1", "t2");
    assertTrue(unfollowResult);
  }

  @Test
  void unfollowUser_doesNotExist_throwsException() {
    assertThrows(DatabaseException.class, () -> socialService.unfollowUser("t1", "t2"));
  }

  @Test
  void getFollowers_twoFollowers_returnsCorrectCount() throws Exception {
    socialService.followUser("t2", "t1");
    assertDoesNotThrow(() -> {
      playerService.createNewPlayer(new PlayerData("t3", "t3", Password.fromPlaintext("p3"), Timestamp.now(), null, null, null));
    });
    socialService.followUser("t3", "t1");

    List<PlayerData> followers = socialService.getFollowers("t1");
    assertEquals(2, followers.size());
    assertInstanceOf(PlayerData.class, followers.getFirst());

    socialService.unfollowUser("t2", "t1");
    socialService.unfollowUser("t3", "t1");
    playerService.deletePlayer("t3");
  }

  @Test
  void getFollowing_twoFollowers_returnsCorrectCount() throws Exception {
    socialService.followUser("t1", "t2");
    assertDoesNotThrow(() -> {
      playerService.createNewPlayer(new PlayerData("t3", "t3", Password.fromPlaintext("p3"), Timestamp.now(), null, null, null));
    });
    socialService.followUser("t1", "t3");

    List<PlayerData> following = socialService.getFollowing("t1");
    assertEquals(2, following.size());
    assertInstanceOf(PlayerData.class, following.getFirst());

    socialService.unfollowUser("t1", "t2");
    socialService.unfollowUser("t1", "t3");
    playerService.deletePlayer("t3");
  }


}
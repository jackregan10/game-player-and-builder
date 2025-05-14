package oogasalad.model.service;

import com.google.cloud.Timestamp;
import java.io.IOException;
import oogasalad.database.DatabaseException;
import oogasalad.database.FirebaseManager;
import oogasalad.model.config.PasswordHashingException;
import oogasalad.model.profile.Password;
import oogasalad.model.profile.PlayerData;
import oogasalad.model.profile.SessionException;
import oogasalad.model.profile.SessionManagement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerServiceTest {
  private PlayerService playerService = new PlayerService();

  @BeforeAll
  public static void setUp() throws Exception {
    FirebaseManager.initializeFirebase();
  }

  @Test
  void createNewPlayer_creatingANewPlayer_Success()
      throws DatabaseException, PasswordHashingException {
    String username = "testuser_" + System.currentTimeMillis();
    String password = "testpassword";
    String fullName = "testfullname";

    PlayerData player = new PlayerData(username, fullName, Password.fromPlaintext(password), Timestamp.now(), null, null, null);


    assertDoesNotThrow(() -> {
      playerService.createNewPlayer(player);
    });

    boolean deleteResult = playerService.deletePlayer(username);
    assertTrue(deleteResult);
  }

  @Test
  void createNewPlayer_Duplicate_ThrowsError()
      throws DatabaseException, PasswordHashingException {
    String username = "duplicateUser_" + System.currentTimeMillis();
    String password = "testpassword";
    String fullName = "testfullname";

    PlayerData player = new PlayerData(username, fullName, Password.fromPlaintext(password), Timestamp.now(), null, null, null);

    boolean result = playerService.createNewPlayer(player);
    assertTrue(result);
    Exception exception = assertThrows(DatabaseException.class, () -> playerService.createNewPlayer(player));
    assertTrue(exception.getMessage().contains("Player " + username +  " already exists"));

    boolean deleteResult = playerService.deletePlayer(username);
    assertTrue(deleteResult);
  }

  @Test
  void deletePlayer_DeletingAPlayer_Success()
      throws DatabaseException, PasswordHashingException {
    String username = "testuser_" + System.currentTimeMillis();
    String password = "testpassword";
    String fullName = "testfullname";

    PlayerData player = new PlayerData(username, fullName, Password.fromPlaintext(password), Timestamp.now(), null, null, null);

    boolean result = playerService.createNewPlayer(player);
    assertTrue(result);

    boolean deleteResult = playerService.deletePlayer(username);
    assertTrue(deleteResult);
  }

  @Test
  void getPlayerByUsername_FetchPlayer_Success()
      throws DatabaseException, PasswordHashingException {
    String username = "justin";
    String password = "testpassword";
    String fullName = "testfullname";
    PlayerData player = new PlayerData(username, fullName, Password.fromPlaintext(password), Timestamp.now(), null, null, null);


    boolean result = playerService.createNewPlayer(player);
    assertTrue(result);

    PlayerData player2 = playerService.getPlayerByUsername(username);
    assertEquals(username, player2.getUsername());

    boolean deleteResult = playerService.deletePlayer(username);
    assertTrue(deleteResult);
  }

  @Test
  void login_normalUser_success()
      throws DatabaseException, PasswordHashingException, SessionException {
    String username = "testuser_" + System.currentTimeMillis();
    String password = "testpassword";
    String fullName = "testfullname";

    PlayerData player = new PlayerData(username, fullName, Password.fromPlaintext(password), Timestamp.now(), null, null, null);

    boolean result = playerService.createNewPlayer(player);
    assertTrue(result);

    assertDoesNotThrow(() -> playerService.login(username, password, false));
    assertEquals(username, SessionManagement.getInstance().getCurrentUser().getUsername());

    boolean deleteResult = playerService.deletePlayer(username);
    assertTrue(deleteResult);
    assertDoesNotThrow(playerService::logout);

  }

  @Test
  void login_notRealUser_ThrowsError()
      throws PasswordHashingException, IOException {
    SessionManagement.getInstance().logout();
    String username = "testuser_" + System.currentTimeMillis();
    String password = "testpassworddsadasdas";

    playerService.login(username, password, false);
    assertFalse(SessionManagement.getInstance().isLoggedIn());
  }

  @Test
  void logout_normalUser_logsOutSuccess() throws DatabaseException, PasswordHashingException {
    String username = "testuser_" + System.currentTimeMillis();
    String password = "testpassword";
    String fullName = "testfullname";

    PlayerData player = new PlayerData(username, fullName, Password.fromPlaintext(password), Timestamp.now(), null, null, null);

    boolean result = playerService.createNewPlayer(player);
    assertTrue(result);

    assertDoesNotThrow(() -> playerService.login(username, password, false));
    assertDoesNotThrow(playerService::logout);
    assertTrue(playerService.deletePlayer(username));
    assertFalse(SessionManagement.getInstance().isLoggedIn());
  }

  @Test
  void updateProfileInformation_correctNewData_ReturnTrue() throws DatabaseException, PasswordHashingException {
    String username = "testuser_" + System.currentTimeMillis();
    String password = "testpassword";
    String fullName = "testfullname";

    PlayerData player = new PlayerData(username, fullName, Password.fromPlaintext(password), Timestamp.now(), null, null, null);

    boolean result = playerService.createNewPlayer(player);
    assertTrue(result);

    String newBio = "testnewbio";
    String pfpUrl = "testpfpurl";
    fullName = "newFullName";
    player.setBio(newBio);
    player.setImageUrl(pfpUrl);
    player.setFullName(fullName);
    boolean updateResult = playerService.updateProfileInformation(player);

    PlayerData updatedPlayer = playerService.getPlayerByUsername(username);

    assertTrue(updateResult);
    assertEquals(newBio, updatedPlayer.getBio());
    assertEquals(pfpUrl, updatedPlayer.getImageUrl());
    assertEquals(fullName, updatedPlayer.getFullName());

    assertTrue(playerService.deletePlayer(username));
  }


}
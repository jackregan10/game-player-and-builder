package oogasalad.model.profile;

import static org.junit.jupiter.api.Assertions.*;

import com.google.cloud.Timestamp;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import oogasalad.database.DatabaseException;
import oogasalad.database.FirebaseManager;
import oogasalad.model.config.PasswordHashingException;
import oogasalad.model.service.PlayerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SessionManagementTest {
  private final PlayerService playerService = new PlayerService();

  @BeforeEach
  @AfterEach
  public void cleanUp() {
    SessionManagement.getInstance().logout(); // reset session
    try {
      Files.deleteIfExists(Paths.get("rememberme.properties"));
    } catch (IOException e) {
      System.err.println("Error cleaning up user properties: " + e.getMessage());
    }
  }

  @Test
  public void login_normalUser_loggedIn()
      throws PasswordHashingException, SessionException, IOException {
    Password password = Password.fromPlaintext("testPassword");
    Timestamp createdAt = Timestamp.now();
    String username = "justin";
    String fullName = "Justin Aronwald";

    PlayerData testUser = new PlayerData(username, fullName, password, createdAt, null, null, null);
    SessionManagement.getInstance().login(testUser, false);

    assertEquals(testUser, SessionManagement.getInstance().getCurrentUser());
    SessionManagement.getInstance().logout();
    assertThrows(SessionException.class, () -> SessionManagement.getInstance().getCurrentUser());
  }

  @Test
  public void isLoggedIn_normalUser_returnsTrue()
      throws PasswordHashingException, SessionException, IOException {
    Password password = Password.fromPlaintext("testPassword");
    Timestamp createdAt = Timestamp.now();
    String username = "justin";
    String fullName = "Justin Aronwald";

    assertFalse(SessionManagement.getInstance().isLoggedIn());

    PlayerData testUser = new PlayerData(username, fullName, password, createdAt, null, null, null);
    SessionManagement.getInstance().login(testUser, false);

    assertTrue(SessionManagement.getInstance().isLoggedIn());
    assertEquals(testUser.getUsername(), SessionManagement.getInstance().getCurrentUser().getUsername());
  }

  @Test
  public void rememberMe_loginAndAutoLogin_Success()
      throws PasswordHashingException, IOException, DatabaseException {
    FirebaseManager.initializeFirebase();

    String username = "rememberme_user";
    String password = "securePassword";
    String fullName = "Remember Me Test";

    try {
      playerService.getPlayerByUsername(username);
      playerService.deletePlayer(username);
    } catch (DatabaseException e) {
      // this is what we want. fixing error
    }


      PlayerData player = new PlayerData(username, fullName, Password.fromPlaintext(password), Timestamp.now(), null, null, null);

    assertTrue(playerService.createNewPlayer(player));

    playerService.login(username, password, true);
    assertTrue(SessionManagement.getInstance().tryAutoLogin());

    SessionManagement.getInstance().logout();
    assertFalse(SessionManagement.getInstance().isLoggedIn());

    assertTrue(playerService.deletePlayer(username));
    SessionManagement.getInstance().logout();
    assertFalse(SessionManagement.getInstance().tryAutoLogin());
  }

}
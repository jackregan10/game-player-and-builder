package oogasalad.model.profile;

import static org.junit.jupiter.api.Assertions.*;

import com.google.cloud.Timestamp;
import oogasalad.model.config.PasswordHashingException;
import org.junit.jupiter.api.Test;

class PlayerDataTest {
  @Test
  void constructor_WithValidInformation_ShouldSetFieldsCorrectly() throws PasswordHashingException {
    String username = "justin";
    String fullName = "Justin Aronwald";
    String bio = "hi all";
    String pfpUrl = "EXAMPLEpfpUrl";
    Timestamp createdAt = Timestamp.now();
    Password password = Password.fromPlaintext("secure123");
    Timestamp modifiedAt = Timestamp.now();


    PlayerData player = new PlayerData(username, fullName, password, createdAt, pfpUrl, bio,modifiedAt);

    assertEquals(username, player.getUsername());
    assertEquals(fullName, player.getFullName());
    assertEquals(password, player.getPassword());
    assertEquals(pfpUrl, player.getImageUrl());
    assertEquals(bio, player.getBio());
    assertEquals(createdAt, player.getCreatedAt());
    assertEquals(modifiedAt, player.getModifiedAt());
  }

  @Test
  void setters_WithValidInput_ShouldUpdateFieldsCorrectly() throws PasswordHashingException {
    PlayerData player = new PlayerData();
    String username = "tester";
    Timestamp createdAt = Timestamp.now();
    String bio = "hi all";
    String pfpUrl = "EXAMPLEpfpUrl";
    Password password = Password.fromPlaintext("secure123");
    String fullName = "Justin Aronwald";
    Timestamp modifiedAt = Timestamp.now();


    player.setUsername(username);
    player.setFullName(fullName);
    player.setPassword(password);
    player.setCreatedAt(createdAt);
    player.setImageUrl(pfpUrl);
    player.setBio(bio);
    player.setModifiedAt(modifiedAt);

    assertEquals(username, player.getUsername());
    assertEquals(fullName, player.getFullName());
    assertEquals(password, player.getPassword());
    assertEquals(pfpUrl, player.getImageUrl());
    assertEquals(bio, player.getBio());
    assertEquals(createdAt, player.getCreatedAt());
    assertEquals(modifiedAt, player.getModifiedAt());
  }
}
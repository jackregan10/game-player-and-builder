package oogasalad.model.profile;

import static org.junit.jupiter.api.Assertions.*;

import oogasalad.model.config.PasswordConfig;
import oogasalad.model.config.PasswordHashingException;
import org.junit.jupiter.api.Test;

class PasswordTest {

  @Test
  void fromPlaintext_generateCorrectly_ShouldGenerateHashAndSalt()
      throws PasswordHashingException {
    Password pw = Password.fromPlaintext("myPass23");

    assertNotNull(pw.getHash(), "hash not null");
    assertNotNull(pw.getSalt(), "salt not null");
    assertFalse(pw.getHash().isEmpty(), "hash not empty");
    assertFalse(pw.getSalt().isEmpty(), "salt not empty");
  }

  @Test
  void verify_CorrectPassword_ShouldReturnTrue() throws PasswordHashingException {
    Password pw = Password.fromPlaintext("mypassword");

    assertTrue(pw.verify("mypassword"));
  }

  @Test
  void verify_IncorrectPassword_ShouldReturnFalse() throws PasswordHashingException {
    Password pw = Password.fromPlaintext("mypassword");

    assertFalse(pw.verify("wrongpassword"));
  }

  @Test
  void samePasswordDifferentSalt_ShouldHaveDifferentHashes() throws PasswordHashingException {
    Password pw1 = Password.fromPlaintext("hello");
    Password pw2 = Password.fromPlaintext("hello");

    assertNotEquals(pw1.getSalt(), pw2.getSalt(), "Salts should be different");
    assertNotEquals(pw1.getHash(), pw2.getHash(), "Hashes should be different");
  }

  @Test
  void verify_KnownSaltAndHash_ShouldMatch() throws PasswordHashingException {
    String rawPassword = "randomPassword";
    String salt = PasswordConfig.generateSalt();
    String hash = PasswordConfig.hashPassword(rawPassword, salt);

    Password saved = new Password(hash, salt);
    assertTrue(saved.verify(rawPassword));
    assertFalse(saved.verify("wrong"));
  }
}
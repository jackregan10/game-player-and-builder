package oogasalad.model.config;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * This class services to provide the means to hash a password for a player profile. It also
 * decrypts to verify the password. Much of this class was either assisted through online code or
 * ChatGPT
 *
 * @author Justin Aronwald
 */
public class PasswordConfig {

  private static final int ITERATIONS = 5000; // lower for testing;
  private static final int KEY_LENGTH = 256;
  private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

  /**
   * Generates the salt for a given password/user
   *
   * @return - a String random value added to a password before hashing
   */
  public static String generateSalt() {
    byte[] salt = new byte[16];
    new SecureRandom().nextBytes(salt);
    return Base64.getEncoder().encodeToString(salt);
  }

  /**
   * Hashes a String password with a given String salt value
   *
   * @param password - the string password inputted
   * @param salt     - a String random value added to a password before hashing
   * @return - the hashed String password
   * @throws PasswordHashingException - an exception notifying of a failure to hash the password
   */
  public static String hashPassword(String password, String salt) throws PasswordHashingException {
    try {
      byte[] saltBytes = Base64.getDecoder().decode(salt);
      PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
      SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
      byte[] hash = skf.generateSecret(spec).getEncoded();
      return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new PasswordHashingException(GameConfig.getText("errorHashingPassword"), e);
    }
  }

  /**
   * Method to determine if an inputted password matches a stored, hashed password
   *
   * @param inputPassword - the current password being inputted
   * @param salt          - the String random value added to a password before hashing
   * @param storedHash    - the stored, already-hashed password
   * @return true - if the password is the same
   * @throws PasswordHashingException - will throw if failure occurs
   */
  public static boolean verifyPassword(String inputPassword, String salt, String storedHash)
      throws PasswordHashingException {
    return hashPassword(inputPassword, salt).equals(storedHash);
  }
}

package oogasalad.model.profile;

import oogasalad.model.config.PasswordConfig;
import oogasalad.model.config.PasswordHashingException;

/**
 * Specific class for handling the password of a user Note: This class uses PBKDF2-based hashing
 * with per-user salt.
 *
 * @author Justin Aronwald
 */
public class Password {

  private String hash;
  private String salt;

  /**
   * This is a required no-arq constructor firebase
   */
  public Password() {
    // Required no-arg constructor for serialization with Firestore */
  }

  /**
   * Constructs a Password object with an existing hash and salt
   *
   * @param hash - the hashed password
   * @param salt - the salt used to generate the hash
   */
  public Password(String hash, String salt) {
    this.hash = hash;
    this.salt = salt;
  }

  /**
   * Creates a new Password object from a plaintext password. Generates a new salt and hashes the
   * password securely.
   *
   * @param plainText the raw password input
   * @return a new Password object containing the hash and salt
   * @throws PasswordHashingException if hashing fails
   */
  public static Password fromPlaintext(String plainText)
      throws PasswordHashingException {
    String salt = PasswordConfig.generateSalt();
    String hash = PasswordConfig.hashPassword(plainText, salt);
    return new Password(hash, salt);
  }

  /**
   * Verifies that a provided String password matches this hashed password
   *
   * @param input - the password to verify
   * @return - true if the password is valid; false otherwise
   * @throws PasswordHashingException - if hashing the input fails
   */
  public boolean verify(String input) throws PasswordHashingException {
    return PasswordConfig.verifyPassword(input, this.salt, this.hash);
  }

  /**
   * Returns the hashed password
   *
   * @return - the password hash
   */
  public String getHash() {
    return hash;
  }

  /**
   * Returns the salt used to hash the password
   *
   * @return - the salt string
   */
  public String getSalt() {
    return salt;
  }

  /**
   * Sets the hashed password
   *
   * @param hash - the password hash
   */
  public void setHash(String hash) {
    this.hash = hash;
  }

  /**
   * Sets the salt (used during deserialization)
   *
   * @param salt - the salt string
   */
  public void setSalt(String salt) {
    this.salt = salt;
  }
}

package oogasalad.model.engine.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import oogasalad.database.FirebaseManager;
import oogasalad.model.config.GameConfig;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

public class GameConfigTest extends DukeApplicationTest {

  private static final String TEST_LANGUAGE = "spanish"; // Add a test language
  private static final String MISSING_LANGUAGE = "NonExistent";
  private static final String TEST_KEY = "testKey";
  private static final String TEST_KEY_ARGUMENT = "testKeyArgument";
  private static final String TEST_MESSAGE = "This is a test message.";
  private static final String TEST_MESSAGE_FORMATTED = "This is a test message with argument: argument";
  private static final String TEST_NUMBER_KEY = "testNumber";
  private static final String TEST_NUMBER_VALUE = "123.45";
  private static final String TEST_LIST_KEY = "testList";




  @Test
  void testSetLanguage_positive() {
    runAsJFXAction(() -> {
      try {
        FirebaseManager.initializeFirebase();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      // Given: The test language is Spanish. Assume Spanish properties file exists.
      GameConfig.setLanguage(TEST_LANGUAGE);

      // Then: The myMessages ResourceBundle should now be Spanish.
      // To verify: check some known Spanish key is not null
      String message = GameConfig.getText(TEST_KEY);
      assertNotNull(message, "Expected a non-null message after setting Spanish language");
    });
  }

  @Test
  void testSetLanguage_negative() {
    // Given: A non-existent language.

    // When: Set the language to the non-existent language.
    GameConfig.setLanguage(MISSING_LANGUAGE);

    // Then: The myMessages ResourceBundle should be for the default language.
    String message = GameConfig.getText(TEST_KEY);
    assertNotNull(message);
  }

  @Test
  void testGetText_positive() {
    // Given: A valid key in the properties file.

    // When: Get the text for the key.
    String result = GameConfig.getText("testKey");

    // Then: The result should match the expected message.
    assertEquals(TEST_MESSAGE, result);
  }

  @Test
  void testGetText_positive_withArgs() {
    // Given: A valid key with placeholders in the properties file.

    // When: Get the text for the key with arguments.
    String result = GameConfig.getText(TEST_KEY_ARGUMENT, "argument");

    // Then: The result should be formatted correctly.
    assertEquals(TEST_MESSAGE_FORMATTED, result);
  }

  @Test
  void testGetText_negative() {
    // Given: A non-existent key.
    String result = GameConfig.getText("nonExistentKey");
    // When & Then: Getting key back
    assertEquals("nonExistentKey", result);
  }

  @Test
  void testGetNumber_positive() {
    // Given: A valid key for a number in the properties file.

    // When: Get the number for the key.
    Double result = GameConfig.getNumber(TEST_NUMBER_KEY);

    // Then: The result should match the expected number.
    assertEquals(Double.parseDouble(TEST_NUMBER_VALUE), result);
  }

  @Test
  void testGetNumber_negative_missingKey() {
    // Given: A non-existent key.
    Double result = GameConfig.getNumber("nonExistentKey");
    // When & Then: Getting default double
    assertEquals(0.0, result);
  }

  @Test
  void testGetNumber_negative_invalidFormat() {
    // Given: A key with an invalid number format.
    Double result = GameConfig.getNumber("testNumberFail");
    // When & Then: Getting default double
    assertEquals(0.0, result);
  }

  @Test
  void testGetTextList_positive() {
    // Given: A valid key for a list of strings in the properties file.

    // When: Get the list of strings for the key.
    List<String> result = GameConfig.getTextList(TEST_LIST_KEY);

    // Then: The result should match the expected list.
    List<String> expectedList = Arrays.asList("item1", "item2", "item3");
    assertEquals(expectedList, result);
  }

  @Test
  void testGetTextList_negative() {
    // Given: A non-existent key.
    List<String> result = GameConfig.getTextList("nonExistentListKey");
    // When & Then: Getting default empty list
    assertEquals(result, List.of());
  }
}
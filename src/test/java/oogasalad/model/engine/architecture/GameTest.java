package oogasalad.model.engine.architecture;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.Dimension;
import org.junit.jupiter.api.Test;

public class GameTest {

  @Test
  public void getGameInfo_getGameInformation_returnNull() {
    Game game = new Game();
    assertNull(game.getGameInfo());
  }

  @Test
  public void setGameInfo_getGameInformation_returnGameInfo() {
    Game game = new Game();
    GameInfo gameInfo = new GameInfo("Test Name", "Test Description", "Test Author", new Dimension(800, 600));
    game.setGameInfo(gameInfo);
    assertEquals(gameInfo, game.getGameInfo());
  }

  @Test
  public void addScene_addGameScene_returnTrue() {
    Game game = new Game();
    GameScene gameScene = new GameScene("Test Scene");
    game.addScene(gameScene);
    assertTrue(game.getAllScenes().containsValue(gameScene));
  }

  @Test
  public void addScene_addDuplicateScene_throwsException() {
    Game game = new Game();
    GameScene gameScene = new GameScene("Test Scene");
    game.addScene(gameScene);
    assertThrows(IllegalArgumentException.class, () -> game.addScene(gameScene));
  }

  @Test
  public void getCurrentScene_getFirstAddedScene_returnGameScene() {
    Game game = new Game();
    GameScene gameScene = new GameScene("Test Scene");
    game.addScene(gameScene);
    assertEquals(gameScene, game.getCurrentScene());
  }

  @Test
  public void changeScene_getSecondAddedScene_returnGameScene() {
    Game game = new Game();
    GameScene gameScene1 = new GameScene("Test Scene 1");
    GameScene gameScene2 = new GameScene("Test Scene 2");
    game.addScene(gameScene1);
    game.addScene(gameScene2);
    game.changeScene("Test Scene 2");
    assertEquals(gameScene2, game.getCurrentScene());
  }

  @Test
  public void getAllScenes_getAllAddedScenes_returnGameScene() {
    Game game = new Game();
    GameScene gameScene1 = new GameScene("Test Scene 1");
    GameScene gameScene2 = new GameScene("Test Scene 2");
    game.addScene(gameScene1);
    game.addScene(gameScene2);
    assertEquals(2, game.getAllScenes().size());
    assertTrue(game.getAllScenes().containsValue(gameScene1));
    assertTrue(game.getAllScenes().containsValue(gameScene2));
  }

  @Test
  public void removeScene_removeGameScene_returnTrue() {
    Game game = new Game();
    GameScene gameScene = new GameScene("Test Scene");
    game.addScene(gameScene);
    game.removeScene(gameScene.getName());
    assertFalse(game.getAllScenes().containsValue(gameScene));
  }

  @Test
  public void removeScene_removeNonExistentGameScene_noExceptionThrown() {
    Game game = new Game();
    GameScene gameScene = new GameScene("Test Scene");
    game.addScene(gameScene);
    game.removeScene("Non Existent Scene");
  }

  @Test
  public void getCurrentInputKeys_getCurrentInputKeys_returnFalse() {
    Game game = new Game();
    assertFalse(game.getCurrentInputKeys().contains(1));
  }

  @Test
  public void keyPressed_keyPressedContain_returnTrue() {
    Game game = new Game();
    game.keyPressed(1);
    assertTrue(game.getCurrentInputKeys().contains(1));
  }

  @Test
  public void keyReleased_keyReleasedContain_returnTrue() {
    Game game = new Game();
    game.keyPressed(1);
    assertTrue(game.getCurrentInputKeys().contains(1));
    game.keyReleased(1);
    assertFalse(game.getCurrentInputKeys().contains(1));
  }

}

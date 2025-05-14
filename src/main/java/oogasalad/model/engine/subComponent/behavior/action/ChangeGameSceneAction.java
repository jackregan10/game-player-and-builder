package oogasalad.model.engine.subComponent.behavior.action;

import java.util.Set;
import oogasalad.model.engine.architecture.Game;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.subComponent.behavior.BehaviorAction;

/**
 * Changes the current GameScene within the Game. - If parameter == -2, go to last level - If
 * parameter == -1, go to next level - If parameter >= 0, go to level at that index
 *
 * @author Logan Dracos
 */
public class ChangeGameSceneAction extends BehaviorAction<Integer> {

  private static final int LAST_LEVEL = -2;
  private static final int NEXT_LEVEL = -1;
  private static final int TRY_AGAIN = 0;

  @Override
  protected Integer defaultParameter() {
    return NEXT_LEVEL;
  }

  @Override
  public Set<Class<? extends GameComponent>> requiredComponents() {
    return Set.of();
  }

  @Override
  protected void perform(Integer levelIndex) {
    Game game = getBehavior().getController().getParent().getScene().getGame();

    if (levelIndex == LAST_LEVEL) {
      goToLastLevel(game);
    } else if (levelIndex == NEXT_LEVEL) {
      goToNextLevel(game);
    } else if(levelIndex == TRY_AGAIN){
      tryAgain(game);
    } else if (isValidLevelIndex(levelIndex, game)) {
      goToSpecificLevel(levelIndex, game);
    } else {
      throw new IllegalArgumentException("Invalid level index: " + levelIndex);
    }
  }

  private void goToLastLevel(Game game) {
    game.goToScene(game.getLevelOrder().getLast());
  }

  private void goToNextLevel(Game game) {
    game.goToNextLevel();
  }

  private void tryAgain(Game game) {
    game.resetGame();
  }

  private void goToSpecificLevel(int levelIndex, Game game) {
    game.goToScene(game.getLevelOrder().get(levelIndex));
  }

  private boolean isValidLevelIndex(int levelIndex, Game game) {
    return levelIndex >= 0 && levelIndex < game.getLevelOrder().size();
  }
}

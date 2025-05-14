package oogasalad.model.config;

/**
 * Enum to denote the default game names in our game player.
 */
public enum DefaultGames {
  InfiniteDino,
  InfiniteGeoDash,
  InfiniteDoodle;

  @Override
  public String toString() {
    return this.name().replace("_", " ");
  }
}

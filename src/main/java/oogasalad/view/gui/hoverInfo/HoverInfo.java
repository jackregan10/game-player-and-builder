package oogasalad.view.gui.hoverInfo;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.architecture.GameObject;

/**
 * Utility class for installing hover tooltips on JavaFX Nodes that display basic information about
 * a {@link GameObject}.
 */
public class HoverInfo {

  /**
   * Installs a hover tooltip on the specified JavaFX {@code node}. The tooltip will display the
   * name and tag of the provided {@link GameObject}, formatted on separate lines.
   *
   * @param n   the JavaFX Node to which the tooltip will be attached
   * @param obj the GameObject containing the information to display in the tooltip
   */
  public static void installHoverTooltip(Node n, GameObject obj) {
    String objectName = obj.getName();
    String objectTag = obj.getTag();
    Tooltip tip = new Tooltip(String.format("%s\n%s", objectName, objectTag));
    Tooltip.install(n, tip);
  }

  /**
   * Installs a hover tooltip on the specified JavaFX {@code node}. The tooltip will display the
   * name, tag, and instructions of the given component.
   *
   * @param n         the JavaFX Node to which the tooltip will be attached
   * @param component the GameComponent containing the information to display in the tooltip
   */
  public static void installHoverTooltip(Node n, GameComponent component) {
    String componentName = component.getClass().getSimpleName();
    String componentTag = "" + component.componentTag();
    Tooltip tip = new Tooltip(String.format("%s %s\n%s %s\n%s %s",
        GameConfig.getText("nameLabel"), componentName,
        GameConfig.getText("typeLabel"), componentTag,
        GameConfig.getText("descriptionLabel"),
        GameConfig.getText(componentName + "Description")));
    Tooltip.install(n, tip);
  }

  /**
   * Installs a hover tooltip on any JavaFX node showing the given text and its description, if one
   * exists within the current language properties file.
   *
   * @param n           the JavaFX Node to which the tooltip will be attached
   * @param displayItem the name of the action, constraint, etc. to display in the tooltip
   */
  public static void installHoverTooltip(Node n, String displayItem) {
    if (displayItem == null || displayItem.isEmpty()) {
      return;
    }
    String textKey = displayItem + "Description";
    String description = GameConfig.getText(textKey);

    // If the description for displayItem is not found in the language file, only show displayItem in tooltip
    if (description.equals(textKey)) {
      Tooltip.install(n, new Tooltip(displayItem));
      return;
    }
    Tooltip tip = new Tooltip(String.format("%s\n\n%s", displayItem, description));
    Tooltip.install(n, tip);
  }

}

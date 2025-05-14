package oogasalad.view.gui.panel;

import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.engine.component.SpriteRenderer;
import oogasalad.view.renderer.componentRenderer.SpriteRendererRenderer;

/**
 * This is the prefab panel that holds all the prefabricated objects in the
 * scene in the current game.
 *
 * @author Hsuan-Kai Liao
 */
public class PrefabPanel extends Pane {

  private static final String DEFAULT_PREFAB_IMAGE_PATH = "oogasalad/placeHolder/prefab.png";

  private static final int PREFAB_IMAGE_WIDTH = 80;
  private static final int PREFAB_IMAGE_HEIGHT = 80;

  private final ScrollPane scrollPane;

  private final TilePane tilePane;

  private GameScene prefabScene;
  private Consumer<GameObject> onInstantiatePrefab;

  /**
   * The constructor to create a prefab scene panel
   */
  public PrefabPanel() {
    this.tilePane = createTilePane();
    this.scrollPane = createScrollPane(tilePane);
    this.getChildren().add(scrollPane);
  }

  /**
   * Set the prefab scene for this panel
   * @param prefabScene the prefab scene given.
   */
  public void setPrefabScene(GameScene prefabScene) {
    this.prefabScene = prefabScene;
    prefabsSync();
  }

  /**
   * Refresh the prefab buttons shown in the panel
   */
  public void prefabsSync() {
    tilePane.getChildren().clear();
    for (GameObject prefab : prefabScene.getActiveObjects()) {
      tilePane.getChildren().add(createPrefabButton(prefab));
    }
  }

  /**
   * Sync the prefab view with the current game scene.
   */
  public void prefabsViewSync() {
    for (Node child : tilePane.getChildren()) {
      if (child instanceof VBox vBox) {
        GameObject prefab = (GameObject) child.getUserData();
        ((Button) vBox.getChildren().getFirst()).setGraphic(getPreviewImage(prefab));
      }
    }
  }

  /**
   * Set the callback for the prefab instantiation.
   * @param onInstantiatePrefab the callback.
   */
  public void setOnInstantiatePrefab(Consumer<GameObject> onInstantiatePrefab) {
    this.onInstantiatePrefab = onInstantiatePrefab;
  }

  private TilePane createTilePane() {
    TilePane tilePane = new TilePane();
    tilePane.setHgap(10);
    tilePane.setVgap(10);
    tilePane.setPadding(new Insets(10));
    tilePane.setPrefColumns(2);
    tilePane.setAlignment(Pos.TOP_CENTER);
    return tilePane;
  }

  private ScrollPane createScrollPane(Pane content) {
    ScrollPane scrollPane = new ScrollPane(content);
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scrollPane.prefWidthProperty().bind(widthProperty());
    scrollPane.prefHeightProperty().bind(heightProperty());
    return scrollPane;
  }

  private VBox createPrefabButton(GameObject prefab) {
    Button spriteButton = new Button();
    spriteButton.setGraphic(getPreviewImage(prefab));
    spriteButton.setStyle("-fx-background-color: orange; -fx-padding: 10;");
    spriteButton.setMaxWidth(Double.MAX_VALUE);

    spriteButton.setOnAction(e -> {
      GameObject newObject = prefab.clone();
      if (onInstantiatePrefab != null) {
        onInstantiatePrefab.accept(newObject);
      }
    });

    Label nameLabel = new Label(prefab.getName());
    nameLabel.setWrapText(true);
    nameLabel.setAlignment(Pos.CENTER);
    nameLabel.setMaxWidth(100);
    nameLabel.setStyle("-fx-font-size: 10px;");

    VBox vBox = new VBox(spriteButton, nameLabel);
    vBox.setSpacing(5);
    vBox.setAlignment(Pos.CENTER);
    vBox.setUserData(prefab);
    return vBox;
  }

  private ImageView getPreviewImage(GameObject prefab) {
    Image image;
    if (!prefab.hasComponent(SpriteRenderer.class)) {
      image = SpriteRendererRenderer.loadImage(null, DEFAULT_PREFAB_IMAGE_PATH);
    } else {
      SpriteRenderer spriteRenderer = prefab.getComponent(oogasalad.model.engine.component.SpriteRenderer.class);
      image = SpriteRendererRenderer.loadImage(spriteRenderer, SpriteRendererRenderer.PLACEHOLDER_IMAGE_PATH);
    }

    ImageView imageView = new ImageView();
    imageView.setPreserveRatio(true);
    imageView.setFitWidth(PREFAB_IMAGE_WIDTH);
    imageView.setFitHeight(PREFAB_IMAGE_HEIGHT);
    imageView.setImage(image);

    return imageView;
  }

}

package oogasalad.view.renderer.componentRenderer;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.component.TextRenderer;
import oogasalad.model.engine.component.Transform;
import oogasalad.view.config.StyleConfig;

/**
 * The renderer to render a TextRenderer.
 */
public class TextRendererRenderer extends ComponentRenderer<TextRenderer> {

  private final AtomicReference<Double> canvasDrawX = new AtomicReference<>(Double.MAX_VALUE);
  private final AtomicReference<Double> canvasDrawY = new AtomicReference<>(Double.MAX_VALUE);

  @Override
  protected void onEditorRender(TextRenderer component, Pane graphicPane, double cameraX, double cameraY) {
    StackPane wrapper = createTextWrapper(component);
    handleEditorCentered(component, wrapper, cameraX, cameraY);
    graphicPane.getChildren().add(wrapper);
  }

  @Override
  protected void onCanvasRender(TextRenderer component, GraphicsContext graphicsContext,
      double cameraX, double cameraY) {
    StackPane wrapper = createTextWrapper(component);
    handleCanvasCentered(component, wrapper, cameraX, cameraY);
    WritableImage snapshot = wrapper.snapshot(null, null);
    graphicsContext.drawImage(snapshot, canvasDrawX.get(), canvasDrawY.get());
  }

  private StackPane createTextWrapper(TextRenderer component) {
    Text textNode = new Text(component.getText());
    textNode.getStyleClass().add(component.getStyleClass());
    textNode.setFont(Font.font(component.getFontSize()));

    StackPane wrapper = new StackPane(textNode);
    wrapper.getStyleClass().add(component.getStyleClass() + "-container");
    applyStyleSheet(wrapper, component.getStyleClass());
    wrapper.applyCss();

    return wrapper;
  }

  private void handleEditorCentered(TextRenderer component, StackPane wrapper, double cameraX, double cameraY) {
    Transform transform = component.getComponent(Transform.class);
    wrapper.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
      double drawX = transform.getX() - cameraX;
      double drawY = transform.getY() - cameraY;
      if (component.isCentered()) {
        drawX = transform.getX() + transform.getScaleX() / 2 - newValue.getWidth() / 2 - cameraX;
        drawY = transform.getY() + transform.getScaleY() / 2 - newValue.getHeight() / 2 - cameraY;
      }

      wrapper.setLayoutX(drawX);
      wrapper.setLayoutY(drawY);
    });
  }

  private void handleCanvasCentered(TextRenderer component, StackPane wrapper, double cameraX, double cameraY) {
    Transform transform = component.getComponent(Transform.class);
    wrapper.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
      if (component.isCentered()) {
        canvasDrawX.set(transform.getX() + transform.getScaleX() / 2 - newValue.getWidth() / 2 - cameraX);
        canvasDrawY.set(transform.getY() + transform.getScaleY() / 2 - newValue.getHeight() / 2 - cameraY);
      } else {
        canvasDrawX.set(transform.getX() - cameraX);
        canvasDrawY.set(transform.getY() - cameraY);
      }
    });
  }

  private void applyStyleSheet(Node node, String styleClass) {
    if (!node.getStyleClass().contains(styleClass)) {
      node.getStyleClass().add(styleClass);
    }

    Group tempRoot = new Group(node);
    Scene tempScene = new Scene(tempRoot, Color.TRANSPARENT);
    List<String> targetStyles = tempScene.getStylesheets();

    if (node.getScene() != null && !node.getScene().getStylesheets().isEmpty()) {
      applyInheritedStylesheets(node, targetStyles);
    } else {
      applyFallbackStylesheet(targetStyles);
    }
  }

  private void applyInheritedStylesheets(Node node, List<String> targetStyles) {
    node.getScene().getStylesheets().stream()
        .filter(sheet -> !targetStyles.contains(sheet))
        .forEach(targetStyles::add);
  }

  private void applyFallbackStylesheet(List<String> targetStyles) {
    String path = "/oogasalad/stylesheets/" + StyleConfig.getBaseTheme().toLowerCase() + ".css";
    var resource = getClass().getResource(path);

    if (resource != null) {
      String css = resource.toExternalForm();
      if (!targetStyles.contains(css)) {
        targetStyles.add(css);
      }
    } else {
      GameConfig.LOGGER.error("Could not load fallback stylesheet: {}", path);
    }
  }
}

package oogasalad.view.renderer.componentRenderer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.Pane;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.SpriteRenderer;
import oogasalad.model.engine.component.Transform;
import oogasalad.model.resource.ResourcePath;
import oogasalad.model.resource.ResourceCache;

/**
 * The renderer to render a spriteRenderer.
 */
public class SpriteRendererRenderer extends ComponentRenderer<SpriteRenderer> {

  public static final String PLACEHOLDER_IMAGE_PATH = "oogasalad/placeHolder/placeholder.png";

  @Override
  protected void onEditorRender(SpriteRenderer component, Pane graphicPane, double cameraX, double cameraY) {
    renderImage(component, graphicPane, cameraX, cameraY, false);
  }

  @Override
  protected void onCanvasRender(SpriteRenderer component, GraphicsContext graphicsContext, double cameraX, double cameraY) {
    renderImage(component, graphicsContext, cameraX, cameraY, true);
  }

  private void renderImage(SpriteRenderer component, Object context, double cameraX, double cameraY, boolean isCanvasRender) {
    GameObject obj = component.getParent();
    Transform transform = obj.getComponent(Transform.class);

    Image img = loadImage(component, PLACEHOLDER_IMAGE_PATH);
    if (img == null) {
      return;
    }

    double posX = transform.getX() + component.getOffsetX() - cameraX;
    double posY = transform.getY() + component.getOffsetY() - cameraY;
    double scaleX = transform.getScaleX();
    double scaleY = transform.getScaleY();

    if (scaleX == 0 || scaleY == 0) {
      return;
    }

    double rotationDegrees = transform.getRotation();
    double rotationRadians = Math.toRadians(rotationDegrees);

    boolean isTiled = component.getTiled();

    RenderSettings renderSettings = new RenderSettings(posX, posY, scaleX, scaleY, rotationRadians, isCanvasRender);
    if (isTiled) {
      renderTiledImage(component, context, img, renderSettings);
    } else {
      renderStretchedImage(context, img, renderSettings);
    }
  }

  private void renderTiledImage(SpriteRenderer component, Object context, Image img, RenderSettings settings) {
    double horizontalTiledAmount = component.getHorizontalTiledAmount();
    double verticalTiledAmount = component.getVerticalTiledAmount();

    double tileWidth = settings.scaleX / horizontalTiledAmount;
    double tileHeight = settings.scaleY / verticalTiledAmount;

    for (double x = 0; x < horizontalTiledAmount; x++) {
      for (double y = 0; y < verticalTiledAmount; y++) {
        double xOffset = x * tileWidth;
        double yOffset = y * tileHeight;

        if (settings.isCanvasRender) {
          renderImageOnCanvas((GraphicsContext) context, img, settings.posX + xOffset, settings.posY + yOffset, tileWidth, tileHeight, settings.rotationRadians);
        } else {
          renderImageOnPane((Pane) context, img, settings.posX + xOffset, settings.posY + yOffset, tileWidth, tileHeight, settings.rotationRadians);
        }
      }
    }
  }

  private void renderStretchedImage(Object context, Image img, RenderSettings settings) {
    double xOffset = 0;
    double yOffset = 0;

    if (settings.isCanvasRender) {
      renderImageOnCanvas((GraphicsContext) context, img, settings.posX + xOffset, settings.posY + yOffset, settings.scaleX, settings.scaleY, settings.rotationRadians);
    } else {
      renderImageOnPane((Pane) context, img, settings.posX + xOffset, settings.posY + yOffset, settings.scaleX, settings.scaleY, settings.rotationRadians);
    }
  }

  private void renderImageOnCanvas(GraphicsContext graphicsContext, Image img, double x, double y, double width, double height, double rotationRadians) {
    graphicsContext.save();
    graphicsContext.translate(x, y);
    graphicsContext.rotate(Math.toDegrees(rotationRadians));
    graphicsContext.drawImage(img, 0, 0, width, height);
    graphicsContext.restore();
  }

  private void renderImageOnPane(Pane graphicPane, Image img, double x, double y, double width, double height, double rotationRadians) {
    ImageView iv = new ImageView(img);

    iv.setLayoutX(x);
    iv.setLayoutY(y);

    iv.setFitWidth(width);
    iv.setFitHeight(height);

    iv.setRotate(Math.toDegrees(rotationRadians));

    graphicPane.getChildren().add(iv);
  }

  /**
   * Load the image from the given path. The image is first loaded from the cache, if it is not
   * found, it will try to load from the relative path and then the absolute path.
   * @param component the spriteRenderer component
   * @return the image loaded from the path
   */
  public static Image loadImage(SpriteRenderer component, String placeholderPath) {
    if (component == null) {
      return loadPlaceholderImage(placeholderPath);
    }

    // Check if the image is already cached
    String imgPath = component.getImagePath().getPath();
    Image img = (Image) ResourceCache.getCache(imgPath);
    if (img != null) {
      return img;
    }

    // First, try relative path
    img = tryLoadImage(ResourcePath.getFromContext() + "/" + imgPath);
    if (img != null) {
      ResourceCache.registerCache(imgPath, img);
      return img;
    }

    // Then, try absolute path
    img = tryLoadImage(new File(imgPath).getAbsolutePath());
    if (img != null) {
      ResourceCache.registerCache(imgPath, img);
      return img;
    }

    // Try to load the placeholder image
    return loadPlaceholderImage(placeholderPath);
  }

  private static Image tryLoadImage(String path) {
    try {
      File file = new File(path);
      if (file.exists()) {
        return new Image(file.toURI().toString());
      }
    } catch (IllegalArgumentException ignored) {
    }
    return null;
  }

  private static Image loadPlaceholderImage(String resourcePath) {
    Image placeholder = (Image) ResourceCache.getCache(resourcePath);
    if (placeholder != null) {
      return placeholder;
    }

    try (InputStream stream = SpriteRenderer.class.getResourceAsStream("/" + resourcePath)) {
      if (stream != null) {
        placeholder = new Image(stream);
        ResourceCache.registerCache(resourcePath, placeholder);
        return placeholder;
      }
    } catch (IllegalArgumentException | IOException e) {
      GameConfig.LOGGER.error("Failed to load placeholder image from resources", e);
    }

    return null;
  }

  private record RenderSettings(
      double posX, double posY, double scaleX, double scaleY, double rotationRadians, boolean isCanvasRender
  ) {}
}

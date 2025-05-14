package oogasalad.view.gui.button;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * ImageButton is a class that extends the StylizedButton class to provide buttons with images.
 */

public class ImageButton extends Button {

  public static final double IMAGE_BUTTON_MARGIN = 0.1;

  private ImageView myImageView;

  /**
   * Constructor for ImageButton
   *
   * @param image the image to display on the button
   */
  public ImageButton(Image image) {
    super();
    createNewImageView(image);
  }

  /**
   * Set the image for the button
   *
   * @param image the image to set
   */
  public void setImage(Image image) {
    myImageView.setImage(image);
  }

  /**
   * Get the image for the button
   *
   * @return the image
   */
  public Image getImage() {
    return myImageView.getImage();
  }

  /**
   * Set the image for the button
   *
   * @param image the image to set
   */
  private void createNewImageView(Image image) {
    myImageView = new ImageView(image);
    myImageView.setPreserveRatio(true);
    this.setGraphic(myImageView);

    // Bind the ImageView size dynamically to the button's size
    myImageView.fitWidthProperty().bind(Bindings.createDoubleBinding(
        () -> (getWidth() * (1 - IMAGE_BUTTON_MARGIN)), widthProperty()));
    myImageView.fitHeightProperty().bind(Bindings.createDoubleBinding(
        () -> (getHeight() * (1 - IMAGE_BUTTON_MARGIN)), heightProperty()));
  }
}

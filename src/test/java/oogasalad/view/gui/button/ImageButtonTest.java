package oogasalad.view.gui.button;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

class ImageButtonTest extends ApplicationTest {

  private Image testImage;
  private ImageButton imageButton;

  @Override
  public void start(Stage stage) {
    testImage = new Image(getClass().getResourceAsStream("/image/test.png"));;
    imageButton = new ImageButton(testImage);

    StackPane root = new StackPane(imageButton);
    Scene scene = new Scene(root, 300, 300);
    stage.setScene(scene);
    stage.show();
  }

  @Test
  void imageButton_ShouldDisplayCorrectImage() {
    ImageView imageView = (ImageView) imageButton.getGraphic();
    assertNotNull(imageView);
    assertEquals(testImage, imageView.getImage());
  }

  @Test
  void setImage_ShouldUpdateImageOnButton() {
    Image newImage = new Image(getClass().getResourceAsStream("/image/test2.png"));
    imageButton.setImage(newImage);
    ImageView imageView = (ImageView) imageButton.getGraphic();
    assertEquals(newImage, imageView.getImage());
  }

  @Test
  void getImage_ShouldReturnCurrentlySetImage() {
    assertEquals(testImage, imageButton.getImage());
  }
}

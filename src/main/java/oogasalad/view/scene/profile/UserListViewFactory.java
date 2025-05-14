package oogasalad.view.scene.profile;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import oogasalad.model.profile.PlayerData;
import oogasalad.view.scene.MainViewManager;
import javafx.scene.Node;

/**
 * Class with APIs to generate a list view of users with varying list types
 *
 * @author Daniel Rodriguez-Florido
 */
public class UserListViewFactory {

  private static final String PROFILE_SCENE_NAME = "ProfileScene";
  private static final String USER_ROW_ID = "userRow";
  private static final String LIST_VIEW_ID = "listView";
  private static final int HBOX_SPACING = 10;
  private static final double PFP_HEIGHT = 40;
  private static final double PFP_WIDTH = 40;
  private static final int HBOX_HEIGHT = 40;
  private static final int VBOX_SPACING = 20;
  private static final int CARD_WIDTH = 500;
  private static final int RADIUS = 18;

  /**
   * API to set up a listview of clickable player objects that comes with a search bar
   *
   * @param players A list of type PlayerData that contains the players you wish to display
   * @return A VBox front-end component
   */
  public static VBox setUpListPlayerDataView(List<PlayerData> players) {
    VBox vbox = new VBox();
    configureVBox(vbox);

    ListView<HBox> listView = new ListView<>();
    List<HBox> userRows = players.stream()
        .filter(playerData -> playerData.getImageUrl() != null)
        .map(UserListViewFactory::createUserRow)
        .collect(Collectors.toCollection(ArrayList::new));

    TextField searchBar = new TextField();
    ObservableList<HBox> filteredList = createFilteredObservableList(userRows, searchBar);

    listView.setItems(filteredList);
    listView.setId(LIST_VIEW_ID);

    vbox.getChildren().addAll(searchBar, listView);
    return vbox;
  }

  private static void configureVBox(VBox card) {
    card.setMaxWidth(CARD_WIDTH);
    card.setAlignment(Pos.TOP_CENTER);
    card.setSpacing(VBOX_SPACING);
  }

  private static boolean checkForContainsLabel(HBox hb, String lower) {
    for (Node n : hb.getChildren()) {
      if (n instanceof Label && ((Label) n).getText().toLowerCase().contains(lower)) {
        return true;
      }
    }
    return false;
  }

  private static HBox createUserRow(PlayerData user) {
    HBox hbox = new HBox();
    configureListViewHbox(hbox);

    String avatarUrl = user.getImageUrl();
    String username = user.getUsername();

    ImageView imageView = new ImageView(avatarUrl);
    configureImageView(imageView);

    Label usernameLabel = new Label(username);

    hbox.setOnMouseClicked(event ->
        switchToProfileScene(username)
    );

    hbox.getChildren().addAll(imageView, usernameLabel);
    return hbox;
  }

  /**
   * API to switch to a user's profile scene based on their username.
   * @param username The username of the user you wish to navigate to.
   */
  public static void switchToProfileScene(String username) {
    MainViewManager.getInstance().addViewScene(ProfileScene.class, PROFILE_SCENE_NAME);
    ProfileScene profileScene = (ProfileScene) MainViewManager.getInstance().getViewScene(PROFILE_SCENE_NAME);
    profileScene.setUsername(username);
    profileScene.updatePage();
    MainViewManager.getInstance().switchTo(PROFILE_SCENE_NAME);
  }

  private static void configureImageView(ImageView imageView) {
    imageView.setFitHeight(PFP_HEIGHT);
    imageView.setFitWidth(PFP_WIDTH);
    imageView.setPreserveRatio(true);
    imageView.setSmooth(true);

    Circle clip = new Circle(PFP_WIDTH/2, PFP_HEIGHT/2, RADIUS); // radius
    imageView.setClip(clip);
  }

  private static void configureListViewHbox(HBox hbox) {
    hbox.setSpacing(HBOX_SPACING);
    hbox.setPrefHeight(HBOX_HEIGHT);
    hbox.setMinHeight(HBOX_HEIGHT);
    hbox.setAlignment(Pos.CENTER_LEFT);
    hbox.setId(USER_ROW_ID);
  }

  /**
   * Creates an {@link ObservableList} with dynamic filtering, based on the search input.
   * <p>
   * This method avoids using implementation types in the codebase visible to PMD's loose coupling rules.
   *
   * @param baseItems the base list of HBox items to display
   * @param searchBar the search bar whose input filters the list
   * @return a filtered ObservableList of HBox items
   */
  public static ObservableList<HBox> createFilteredObservableList(List<HBox> baseItems, TextField searchBar) {
    ObservableList<HBox> observableList = FXCollections.observableArrayList(baseItems);

    // Declare as ObservableList to satisfy PMD, while assigning a FilteredList anonymously
    ObservableList<HBox> filtered = new javafx.collections.transformation.FilteredList<>(observableList, hb -> true);
    filtered.forEach(UserListViewFactory::configureListViewHbox);

    searchBar.setId("searchBar");

    searchBar.textProperty().addListener((obs, oldText, newText) -> {
      String lower = newText.toLowerCase().trim();
      //couldn't eliminate PMD coupling error without this
      ((javafx.collections.transformation.FilteredList<HBox>) filtered).setPredicate(hb -> {
        if (lower.isEmpty()) return true;
        return checkForContainsLabel(hb, lower);
      });
    });

    return filtered;
  }

}

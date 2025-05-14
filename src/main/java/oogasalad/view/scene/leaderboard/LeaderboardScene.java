package oogasalad.view.scene.leaderboard;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import oogasalad.controller.GameController;
import oogasalad.controller.ScoreController;
import oogasalad.database.DatabaseException;
import oogasalad.model.config.DefaultGames;
import oogasalad.model.config.GameConfig;
import oogasalad.model.profile.ScoreData;
import oogasalad.view.config.StyleConfig;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.ViewScene;
import oogasalad.view.scene.profile.UserListViewFactory;

import static oogasalad.model.config.GameConfig.LOGGER;

/**
 * Front-end code for the leaderboard ViewScene.
 *
 * @author Daniel Rodriguez-Florido
 */
public class LeaderboardScene extends ViewScene {

  private static final int HEADER_HEIGHT = 40;
  private static final int PFP_DIM = 25;
  private static final GameController GAME_CONTROLLER = new GameController();
  private static final ScoreController SCORE_CONTROLLER = new ScoreController();
  private static final int HBOX_SPACING = 10;
  private static final int MAX_LEADERBOARD_WIDTH = 800;
  private static final int MAX_LEADERBOARD_HEIGHT = 500;

  private final BorderPane root;

  protected LeaderboardScene(Stage stage) {
    super(stage);
    root = new BorderPane();
    initScene(stage);
    addHeader();
    addTabs();
  }

  private void initScene(Stage stage) {
    Scene scene = new Scene(root);
    StyleConfig.registerScene(scene);
    stage.setScene(scene);
  }

  private void addHeader() {
    Button returnBtn = new Button(GameConfig.getText("returnButton"));
    returnBtn.setOnAction(e -> MainViewManager.getInstance().switchToPrevScene());

    Label title = new Label(GameConfig.getText("leaderboardTitle"));
    title.setId("title");

    StackPane header = new StackPane();
    header.getChildren().addAll(title, returnBtn);

    StackPane.setAlignment(returnBtn, Pos.CENTER_LEFT);
    StackPane.setAlignment(title, Pos.CENTER);

    header.setPadding(new Insets(20, 0, 0, 0));
    header.setMaxHeight(HEADER_HEIGHT);

    root.setTop(header);
  }

  private void addTabs() {
    TabPane pane = new TabPane();

    for (DefaultGames game : DefaultGames.values()) {
      pane.getTabs().add(createTab(game));
    }

    Tab custom = new Tab("Custom");
    custom.setClosable(false);
    attachCustomLoader(custom);
    pane.getTabs().add(custom);

    pane.setPadding(new Insets(0));
    pane.setMaxWidth(MAX_LEADERBOARD_WIDTH);
    pane.setMaxHeight(MAX_LEADERBOARD_HEIGHT);
    pane.getStyleClass().add("leaderboard-card");

    VBox wrapper = new VBox(pane);
    wrapper.setAlignment(Pos.CENTER);
    wrapper.setPadding(new Insets(30, 0, 30, 0));
    root.setCenter(wrapper);
  }

  private Tab createTab(DefaultGames game) {
    Tab tab = new Tab(GameConfig.getText(game.toString()));
    tab.setClosable(false);
    tab.setContent(new Label(GameConfig.getText("loadLeaderboard")));
    attachLoader(tab, game);
    return tab;
  }

  private void attachLoader(Tab tab, DefaultGames game) {
    AtomicBoolean loaded = new AtomicBoolean();
    tab.setOnSelectionChanged(evt -> {
      if (tab.isSelected() && !loaded.get()) {
        loaded.set(true);
        loadLeaderboard(tab, game);
      }
    });
  }

  private void loadLeaderboard(Tab tab, DefaultGames game) {
    try {
      List<ScoreData> scores = SCORE_CONTROLLER
          .handleGetHighScoresForAllUsers(game.toString());
      tab.setContent(buildLeaderboard(scores));
    } catch (DatabaseException e) {
      tab.setContent(new Label(GameConfig.getText("loadScoresFail")));
    }
  }

  private VBox buildLeaderboard(List<ScoreData> scores) throws DatabaseException {
    TextField search = createSearchBar();
    ListView<HBox> list = createListView(scores, search);
    VBox box = new VBox(20, search, list);
    box.setAlignment(Pos.CENTER);
    return box;
  }

  private TextField createSearchBar() {
    TextField search = new TextField();
    search.setPromptText(GameConfig.getText("searchForUserTitle"));
    return search;
  }

  private ListView<HBox> createListView(List<ScoreData> scores,
      TextField search) throws DatabaseException {
    List<HBox> rows = buildRows(scores);
    ObservableList<HBox> filtered =
        UserListViewFactory.createFilteredObservableList(rows, search);
    ListView<HBox> view = new ListView<>();
    view.setItems(filtered);
    return view;
  }

  private List<HBox> buildRows(List<ScoreData> scores) throws DatabaseException {
    List<HBox> rows = new java.util.ArrayList<>();
    int rank = 1;
    for (ScoreData sd : scores) {
      rows.add(createRow(rank++, sd));
    }
    return rows;
  }

  private HBox createRow(int rank, ScoreData sd) throws DatabaseException {
    ImageView avatar = new ImageView(fetchAvatarUrl(sd.getUsername()));
    avatar.setFitWidth(PFP_DIM);
    avatar.setFitHeight(PFP_DIM);

    Circle clip = new Circle((double) PFP_DIM / 2, (double) PFP_DIM / 2, PFP_DIM * 2); // radius
    avatar.setClip(clip);

    Label rankLbl = new Label(String.valueOf(rank));
    Label nameLbl = new Label(sd.getUsername());
    Label scoreLbl = new Label(GameConfig.getText("scoreTitleDisplay", sd.getScore()));

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox box = new HBox(HBOX_SPACING, rankLbl, avatar, nameLbl, spacer, scoreLbl);
    box.setAlignment(Pos.CENTER);
    box.setOnMouseClicked(e ->
        UserListViewFactory.switchToProfileScene(sd.getUsername()));
    return box;
  }

  private String fetchAvatarUrl(String user) throws DatabaseException {
    try {
      return GAME_CONTROLLER.getPlayerByUsername(user).getImageUrl();
    } catch (DatabaseException e) {
      LOGGER.error(GameConfig.getText("avatarFetchFail"));
      throw new DatabaseException(GameConfig.getText("avatarFetchFail"), e);
    }
  }

  private void attachCustomLoader(Tab tab) {
    AtomicBoolean initialized = new AtomicBoolean(false);
    tab.setOnSelectionChanged(evt -> {
      if (tab.isSelected() && !initialized.get()) {
        initialized.set(true);
        tab.setContent(buildCustomTab());
      }
    });
  }

  private VBox buildCustomTab() {
    TextField gameField = createGameField();
    Button loadBtn = createLoadButton(gameField);
    HBox inputBox = createInputBox(gameField, loadBtn);
    TextField searchBar = createSearchBar();
    ListView<HBox> listView = createResultsListView();

    VBox layout = assembleLayout(inputBox, searchBar, listView);
    configureLoadAction(loadBtn, gameField, searchBar, listView);

    return layout;
  }

  private TextField createGameField() {
    TextField tf = new TextField();
    tf.setPromptText(GameConfig.getText("enterGameName"));
    return tf;
  }

  private Button createLoadButton(TextField gameField) {
    Button btn = new Button(GameConfig.getText("search"));
    btn.disableProperty().bind(gameField.textProperty().isEmpty());
    return btn;
  }

  private HBox createInputBox(TextField gameField, Button loadBtn) {
    HBox box = new HBox(6, gameField, loadBtn);
    box.setAlignment(Pos.CENTER_LEFT);
    return box;
  }

  private ListView<HBox> createResultsListView() {
    ListView<HBox> listView = new ListView<>();
    listView.setPlaceholder(new Label(GameConfig.getText("noScoresToDisplay")));
    return listView;
  }

  private VBox assembleLayout(HBox inputBox, TextField searchBar, ListView<HBox> listView) {
    VBox v = new VBox(20, inputBox, searchBar, listView);
    v.setAlignment(Pos.CENTER);
    v.setPadding(new Insets(12));
    return v;
  }

  private void configureLoadAction(Button loadBtn,
      TextField gameField,
      TextField searchBar,
      ListView<HBox> listView) {
    loadBtn.setOnAction(e -> {
      String gameName = gameField.getText().trim();
      try {
        List<ScoreData> scores = SCORE_CONTROLLER
            .handleGetHighScoresForAllUsers(gameName);
        List<HBox> rows = buildRows(scores);
        ObservableList<HBox> filtered =
            UserListViewFactory.createFilteredObservableList(rows, searchBar);
        listView.setItems(filtered);

      } catch (DatabaseException ex) {
        listView.setItems(FXCollections.emptyObservableList());
        listView.setPlaceholder(new Label(GameConfig.getText("couldNotLoadLeaderboard")));
      }
    });
  }

}
package oogasalad.view.gui.docking;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * A docking system that allows users to dock and undock floating windows to the main stage.
 *
 * @author Hsuan-Kai Liao
 */
public class Docker {

  // Constants
  private static final int DOCK_OUTSIDE_OFFSET = 10;
  private static final String DOCK_SPLIT_PANE = "dock-split-pane";
  // Instance variables
  private final List<SplitPane> splitPanes = new ArrayList<>();
  private final List<DWindow> floatingWindows = new ArrayList<>();
  private DWindow undockNewWindow = null;

  // Docker attributes
  private final DIndicator dockIndicator;
  private final Stage mainStage;

  // Docker settings
  private boolean isWindowOpaqueOnDragging = false;

  /**
   * The position of the docking.
   */
  public enum DockPosition {
    NONE, LEFT, RIGHT, TOP, BOTTOM, CENTER
  }

  /* PACKAGE GETTERS */

  /**
   * Package-private getter for the dock indicator
   */
  DIndicator getDockIndicator() {
    return dockIndicator;
  }

  /* APIS BELOW */

  /**
   * Creates a new Docker with the specified main stage and dimensions.
   * After creating a Docker, users should NOT modify the scene of the main stage.
   *
   * @param mainStage the main stage of the docking system
   */
  public Docker(Stage mainStage) {
    this.mainStage = mainStage;
    this.dockIndicator = new DIndicator(this);
    initializeSplitPane();
    initializeScene();
    initializeEventHandlers();
  }

  /**
   * Creates a floating window with the specified title and content.
   *
   * @param title the title of the floating window
   * @param content the content of the floating window
   * @param dockPosition the default dock position of the floating window
   * @return the floating window
   */
  public DWindow createDWindow(StringProperty title, Node content, DockPosition dockPosition) {
    Stage floatingStage = new Stage();
    floatingStage.initStyle(StageStyle.UTILITY);
    TabPane floatingTabPane = createTabPane(title, content);
    DWindow dWindow = new DWindow(floatingStage, floatingTabPane, this);
    dWindow.getFloatingStage().getScene().getStylesheets().setAll(mainStage.getScene().getStylesheets());
    floatingWindows.add(dWindow);

    // Initial dock check
    if (dockPosition != null && dockPosition != DockPosition.NONE) {
      dWindow.getFloatingStage().setOpacity(0);
      dockTab(dWindow, null, dockPosition);
    }

    return dWindow;
  }

  /**
   * Set the topBar for the docking mainWindow.
   * @param topBar the topBar JavaFX Node.
   */
  public void setTopBar(Node topBar) {
    ((BorderPane) mainStage.getScene().getRoot()).setTop(topBar);
  }

  /**
   * Returns the main docker stage of the docking system.
   *
   * @return the main docker stage
   */
  public Stage getMainStage() {
    return mainStage;
  }

  /**
   * Adds a style sheet to the docker and the dock indicator.
   *
   * @param styleSheet the style sheet to add
   */
  public void addStyleSheet(String styleSheet) {
    mainStage.getScene().getStylesheets().add(styleSheet);
    dockIndicator.getIndicatorStage().getScene().getStylesheets().add(styleSheet);

    // Add the style sheet to the floating windows
    for (DWindow floatingWindow : floatingWindows) {
      if (floatingWindow.getFloatingStage().getScene() != null) {
        floatingWindow.getFloatingStage().getScene().getStylesheets().add(styleSheet);
      }
    }
  }

  /**
   * Clears all style sheets from the docker and the dock indicator.
   */
  public void clearStyleSheets() {
    mainStage.getScene().getStylesheets().clear();
    dockIndicator.getIndicatorStage().getScene().getStylesheets().clear();

    // Clear the style sheets from the floating windows
    for (DWindow floatingWindow : floatingWindows) {
      if (floatingWindow.getFloatingStage().getScene() != null) {
        floatingWindow.getFloatingStage().getScene().getStylesheets().clear();
      }
    }
  }

  /**
   * Reformats the docker to adjust the divider positions of the split panes.
   */
  public void reformat() {
    SplitPane mainSplitPane = (SplitPane) ((BorderPane) mainStage.getScene().getRoot()).getCenter();
    reformatSplitPane(mainSplitPane);
  }

  /* SETTINGS */

  /**
   * Modifies window opacity once dragging
   */
  public void setWindowOpaqueOnDragging(boolean isOpaque) {
    this.isWindowOpaqueOnDragging = isOpaque;
  }

  /**
   * Retrieve whether the window is keeping opaque on dragging.
   */
  public boolean getWindowOpaqueOnDragging() {
    return this.isWindowOpaqueOnDragging;
  }

  /* DOCKING CORE */

  void dockTab(DWindow dWindow, TabPane destTabPane, DockPosition dockPosition) {
    // Add the floating TabPane to the target TabPane
    addTabToDocker(dWindow.getFloatingTabPane(), destTabPane, dockPosition);

    // Hide the floating stage
    hideFloatingWindow(dWindow);

    // Call the onDockEvent
    if (dWindow.getOnDockEvent() != null) {
      dWindow.getOnDockEvent().handle(null);
    }
  }

  void undockTab(DWindow dWindow) {
    TabPane floatingTabPane = dWindow.getFloatingTabPane();
    Stage floatingWindow = dWindow.getFloatingStage();

    // Get the target Tab and its TabPane
    Tab targetTab = (Tab) floatingTabPane.getTabs().getFirst().getUserData();
    TabPane targetTabPane = targetTab.getTabPane();

    // Transfer content and update Tab
    Node content = targetTab.getContent();
    dWindow.getFloatingTabPane().getTabs().getFirst().setContent(content);
    targetTab.setContent(null);

    // Remove the TabPane or Tab from the Docker
    int single = 1;
    if (targetTabPane.getTabs().size() == single) {
      removeTabFromDocker(targetTab);
      removeTabPaneFromDocker(targetTabPane);
      collapseSplitPanes();
    } else {
      removeTabFromDocker(targetTab);
    }

    // Set up the floating window
    setupFloatingWindow(floatingTabPane, floatingWindow, targetTabPane);
    undockNewWindow = dWindow;

    // Trigger undock event and update state
    if (dWindow.getOnUndockEvent() != null) {
      dWindow.getOnUndockEvent().handle(null);
    }
    dWindow.setIsDocked(false);
  }

  /* PACKAGE-PRIVATE METHODS */

  boolean isMouseInsideMainScene(double mouseX, double mouseY) {
    double sceneX = mainStage.getScene().getWindow().getX();
    double sceneY = mainStage.getScene().getWindow().getY();
    double sceneWidth = mainStage.getScene().getWidth();
    double sceneHeight = mainStage.getScene().getHeight();
    double decorationBarHeight = mainStage.getHeight() - sceneHeight;

    return mouseX >= sceneX + DOCK_OUTSIDE_OFFSET && mouseX <= sceneX - DOCK_OUTSIDE_OFFSET + sceneWidth &&
        mouseY >= sceneY + DOCK_OUTSIDE_OFFSET + decorationBarHeight && mouseY <= sceneY - DOCK_OUTSIDE_OFFSET + sceneHeight;
  }

  private void reformatSplitPane(SplitPane splitPane) {
    int size = splitPane.getItems().size();
    int splitMinSize = 2;

    if (size < splitMinSize) {
      return;
    }

    double[] positions = IntStream
        .range(0, size - 1)
        .mapToDouble(i -> (i + 1) / (double) size)
        .toArray();
    splitPane.setDividerPositions(positions);

    splitPane.getItems().stream()
        .filter(node -> node instanceof SplitPane)
        .map(node -> (SplitPane) node)
        .forEach(this::reformatSplitPane);
  }

  private boolean containsOnScreen(TabPane tp, double x, double y) {
    Bounds b = tp.localToScreen(tp.getBoundsInLocal());
    return b != null && b.contains(x, y);
  }

  TabPane findTabPaneUnderMouse(double mouseX, double mouseY) {
    return splitPanes.stream()
        .flatMap(sp -> sp.getItems().stream().filter(TabPane.class::isInstance).map(TabPane.class::cast))
        .filter(tp -> containsOnScreen(tp, mouseX, mouseY))
        .findFirst()
        .orElse(null);
  }


  void removeFloatingWindow(DWindow dWindow) {
    floatingWindows.remove(dWindow);
  }

  /* On Dock Action */

  private void addTabToDocker(TabPane srcTabPane, TabPane destTabPane, DockPosition dockPosition) {
    // Invalid dock position
    assert dockPosition != DockPosition.NONE && dockPosition != null;

    // Get splitPanes
    SplitPane targetSplitPane = findParentSplitPane(destTabPane);
    targetSplitPane = (destTabPane == null) ? (SplitPane) ((BorderPane) mainStage.getScene().getRoot()).getCenter() : targetSplitPane == null ? new SplitPane() : targetSplitPane;
    targetSplitPane.getStyleClass().add(DOCK_SPLIT_PANE);

    Tab newTab = CreateCorrespondingTab(srcTabPane);

    // Center dock position case
    if (dockPosition == DockPosition.CENTER) {
      assert destTabPane != null;
      dockToCenter(destTabPane, newTab);
    }

    // Other dock positions
    else {
      dockToOther(destTabPane, dockPosition, newTab, targetSplitPane);
    }
  }

  private void dockToOther(TabPane destTabPane,
      DockPosition dockPosition,
      Tab newTab,
      SplitPane targetSplitPane) {

    TabPane newTabPane      = createNewTabPane(newTab);
    double[] originalPos    = targetSplitPane.getDividerPositions();
    double[] newPos = new double[originalPos.length + 1];
    boolean isHorizontal         = isHorizontalDock(dockPosition);
    boolean shouldFrontInsert    = shouldInsertInFront(dockPosition);
    boolean shouldInsertDirectly = isSameOrientation(targetSplitPane, isHorizontal)
        && !targetSplitPane.getItems().isEmpty();

    int index = computeInsertIndex(destTabPane, targetSplitPane, shouldFrontInsert);

    if (shouldInsertDirectly) {
      directInsertTab(targetSplitPane,
          shouldFrontInsert,
          index,
          newTabPane,
          originalPos, newPos);
    } else {
      indirectInsertTab(destTabPane,
          targetSplitPane,
          isHorizontal,
          newTabPane,
          shouldFrontInsert,
          index,
          originalPos);
    }
  }

  private TabPane createNewTabPane(Tab newTab) {
    TabPane pane = new TabPane();
    pane.getStyleClass().add("dock-tab-pane");
    pane.getTabs().add(newTab);
    pane.getSelectionModel().select(newTab);
    return pane;
  }

  private boolean isHorizontalDock(DockPosition p) {
    return p == DockPosition.LEFT || p == DockPosition.RIGHT;
  }

  private boolean shouldInsertInFront(DockPosition p) {
    return p == DockPosition.LEFT || p == DockPosition.TOP;
  }

  private boolean isSameOrientation(SplitPane sp, boolean horizontal) {
    return sp.getOrientation() == (horizontal
        ? Orientation.HORIZONTAL
        : Orientation.VERTICAL);
  }

  private int computeInsertIndex(TabPane dest, SplitPane sp, boolean front) {
    if (dest == null) {
      return front ? 0 : sp.getItems().size() - 1;
    }
    int idx = sp.getItems().indexOf(dest);
    return (idx != -1)
        ? idx
        : (front ? 0 : sp.getItems().size() - 1);
  }

  private void indirectInsertTab(TabPane destTabPane,
      SplitPane targetSplitPane,
      boolean isHorizontal,
      TabPane newTabPane,
      boolean shouldFrontInsert,
      int index,
      double[] originalPositions) {

    SplitPane newSplitPane = createNewSplitPane(isHorizontal);

    insertIntoNewSplit(newSplitPane,
        targetSplitPane,
        destTabPane,
        newTabPane,
        shouldFrontInsert,
        index);

    resetDividerPositions(targetSplitPane, originalPositions);

    cleanupEmptyOldPane(targetSplitPane, newSplitPane);

    maybeSetAsRoot(destTabPane, newSplitPane);
  }

  private SplitPane createNewSplitPane(boolean horizontal) {
    SplitPane sp = new SplitPane();
    sp.getStyleClass().add(DOCK_SPLIT_PANE);
    sp.setOrientation(horizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL);
    return sp;
  }

  private void insertIntoNewSplit(SplitPane newSplit,
      SplitPane oldSplit,
      TabPane dest,
      TabPane newTabPane,
      boolean front,
      int idx) {
    newSplit.getItems().add(newTabPane);

    int insertIndex = front ? 1 : 0;

    if (dest != null) {
      replaceOldSplit(newSplit, oldSplit, dest, insertIndex, idx);
    } else {
      newSplit.getItems().add(insertIndex, oldSplit);
    }
  }

  private void replaceOldSplit(SplitPane newSplit, SplitPane oldSplit, TabPane dest, int insertIndex, int idx) {
    newSplit.getItems().add(insertIndex, dest);
    if (!oldSplit.getItems().isEmpty()) {
      oldSplit.getItems().set(idx, newSplit);
    }
  }

  private void resetDividerPositions(SplitPane sp, double[] positions) {
    if (positions.length > 0) {
      sp.setDividerPositions(positions);
    }
  }

  private void cleanupEmptyOldPane(SplitPane oldSplit, SplitPane newSplit) {
    if (oldSplit.getItems().isEmpty()) {
      newSplit.getItems().remove(oldSplit);
      splitPanes.remove(oldSplit);
    }
    if (!newSplit.getItems().isEmpty()) {
      splitPanes.add(newSplit);
    }
  }

  private void maybeSetAsRoot(TabPane dest, SplitPane newSplit) {
    if (dest == null) {
      ((BorderPane) mainStage.getScene().getRoot()).setCenter(newSplit);
    }
  }

  private void directInsertTab(SplitPane targetSplitPane, boolean shouldFrontInsert, int index,
      TabPane newTabPane, double[] originalPositions, double[] newPositions) {
    targetSplitPane.getItems().add(shouldFrontInsert ? index : index + 1, newTabPane);

    // New divider positions
    if (originalPositions.length != 0) {
      System.arraycopy(originalPositions, 0, newPositions, 0, index);
      double newDividerPosition = (index == 0) ? originalPositions[0] / 2.0
          : (index == originalPositions.length) ? (originalPositions[index - 1] + 1.0) / 2.0
              : (originalPositions[index - 1] + originalPositions[index]) / 2.0;
      newPositions[index] = newDividerPosition;
      System.arraycopy(originalPositions, index, newPositions, index + 1,
          originalPositions.length - index);
      targetSplitPane.setDividerPositions(newPositions);
    }
  }

  private void dockToCenter(TabPane destTabPane, Tab newTab) {
    destTabPane.getTabs().add(newTab);
    destTabPane.getSelectionModel().select(newTab);
  }

  private Tab CreateCorrespondingTab(TabPane srcTabPane) {
    // Get the srcTabPane properties
    Tab srcTab = srcTabPane.getTabs().getFirst();
    Label srcTabLabel = (Label) srcTab.getGraphic();
    Node srcTabContent = srcTab.getContent();

    // Create a new Tab with the same properties with the source Tab, and pass the content
    Label newTabLabel = new Label();
    newTabLabel.getStyleClass().add("dock-tab-label");
    newTabLabel.textProperty().bind(srcTabLabel.textProperty());
    newTabLabel.setOnMousePressed(event -> srcTabLabel.getOnMousePressed().handle(event));
    newTabLabel.setOnMouseDragged(event -> srcTabLabel.getOnMouseDragged().handle(event));
    Tab newTab = new Tab();
    newTab.getStyleClass().add("dock-tab");
    newTab.setClosable(srcTab.isClosable());
    newTab.setGraphic(newTabLabel);
    newTab.setContent(srcTabContent);
    srcTab.setContent(null);

    // Link the new tab to the old tab
    srcTab.setUserData(newTab);
    return newTab;
  }

  /* On Undock Action */

  private void removeTabFromDocker(Tab tab) {
    for (SplitPane splitPane : splitPanes) {
      for (Node node : splitPane.getItems()) {
        if (node instanceof TabPane tabPane && tabPane.getTabs().contains(tab)) {
          tabPane.getTabs().remove(tab);
          return;
        }
      }
    }
  }

  private void removeTabPaneFromDocker(TabPane tabPane) {
    for (SplitPane splitPane : splitPanes) {
      if (!splitPane.getItems().contains(tabPane)) continue;

      double[] originalPositions = splitPane.getDividerPositions();
      int originalCount = splitPane.getItems().size();
      int removedIndex = splitPane.getItems().indexOf(tabPane);

      splitPane.getItems().remove(tabPane);

      int single = 1;
      if (originalCount > single) {
        double[] newPositions = calculateNewDividerPositions(originalPositions, removedIndex);
        splitPane.setDividerPositions(newPositions);
      }

      return;
    }
  }

  private double[] calculateNewDividerPositions(double[] originalPositions, int removedIndex) {
    double[] newPositions = new double[originalPositions.length - 1];
    int removedIndexFront = removedIndex - 1;

    for (int i = 0; i < newPositions.length; i++) {
      if (i < removedIndexFront) {
        newPositions[i] = originalPositions[i];
      } else if (i == removedIndexFront) {
        newPositions[i] = (originalPositions[i] + originalPositions[i + 1]) / 2;
      } else {
        newPositions[i] = originalPositions[i + 1];
      }
    }

    return newPositions;
  }

  private void collapseSplitPanes() {
    SplitPane mainSplitPane = (SplitPane) ((BorderPane) mainStage.getScene().getRoot()).getCenter();
    collapseRecursive(mainSplitPane);

    int single = 1;
    if (mainSplitPane.getItems().size() == single && mainSplitPane.getItems().getFirst() instanceof SplitPane childSplitPane) {
      liftUpSingleChild(mainSplitPane, childSplitPane);
    }
  }

  private void collapseRecursive(SplitPane parent) {
    List<SplitPane> toRemove = new ArrayList<>();

    for (int i = 0; i < parent.getItems().size(); i++) {
      SplitPane child = getChildSplitPane(parent.getItems().get(i));
      if (child == null) continue;

      collapseRecursive(child); // recursion

      if (removeIfEmpty(child, toRemove)) continue;
      i = handleIfSingleChild(parent, child, i, toRemove);
    }

    parent.getItems().removeAll(toRemove);
    splitPanes.removeAll(toRemove);
  }

  private SplitPane getChildSplitPane(Node node) {
    return (node instanceof SplitPane child) ? child : null;
  }

  private boolean removeIfEmpty(SplitPane child, List<SplitPane> toRemove) {
    if (child.getItems().isEmpty()) {
      toRemove.add(child);
      return true;
    }
    return false;
  }

  private int handleIfSingleChild(SplitPane parent, SplitPane child, int i, List<SplitPane> toRemove) {
    int single = 1;
    if (child.getItems().size() != single) return i;

    Node onlyChild = child.getItems().getFirst();
    if (canMerge(parent, onlyChild)) {
      mergeNestedSplitPane(parent, (SplitPane) onlyChild, i);
      toRemove.add(child);
      toRemove.add((SplitPane) onlyChild);
      return i + ((SplitPane) onlyChild).getItems().size() - 1;
    }

    replaceWithSingleChild(parent, child, onlyChild, i);
    toRemove.add(child);
    return i;
  }

  private void liftUpSingleChild(SplitPane parent, SplitPane child) {
    parent.setOrientation(child.getOrientation());
    parent.getItems().setAll(child.getItems());
    parent.setDividerPositions(child.getDividerPositions());
    child.getItems().clear();
    splitPanes.remove(child);
  }


  private boolean canMerge(SplitPane parent, Node onlyChild) {
    return onlyChild instanceof SplitPane grandChild &&
        grandChild.getOrientation() == parent.getOrientation();
  }

  private void mergeNestedSplitPane(SplitPane parent, SplitPane grandChild, int index) {
    double[] parentDividers = parent.getDividerPositions();
    double[] grandChildDividers = grandChild.getDividerPositions();

    int leftIndex = index - 1;
    double leftPos = (leftIndex >= 0) ? parentDividers[leftIndex] : 0.0;
    double rightPos = (index < parentDividers.length) ? parentDividers[leftIndex + 1] : 1.0;
    double totalSize = rightPos - leftPos;

    parent.getItems().remove(index);
    parent.getItems().addAll(index, grandChild.getItems());

    List<Double> newDividers = new ArrayList<>();
    for (int i = 0; i < index; i++) {
      newDividers.add(parentDividers[i]);
    }
    for (double divider : grandChildDividers) {
      newDividers.add(leftPos + divider * totalSize);
    }
    for (int i = index; i < parentDividers.length; i++) {
      newDividers.add(parentDividers[i]);
    }

    parent.setDividerPositions(newDividers.stream().mapToDouble(Double::doubleValue).toArray());
  }

  private void replaceWithSingleChild(SplitPane parent, SplitPane child, Node onlyChild, int index) {
    double[] parentDividers = parent.getDividerPositions();
    child.getItems().remove(onlyChild);
    parent.getItems().set(index, onlyChild);
    parent.setDividerPositions(parentDividers);
  }

  /* HELPER METHODS */

  private void initializeSplitPane() {
    SplitPane splitPane = new SplitPane();
    splitPane.setOrientation(Orientation.HORIZONTAL);
    splitPane.getStyleClass().add(DOCK_SPLIT_PANE);
    splitPanes.add(splitPane);
  }

  private void initializeScene() {
    Scene mainScene = new Scene(new BorderPane(new SplitPane()), mainStage.getWidth(), mainStage.getHeight());
    mainStage.setScene(mainScene);
  }

  private void initializeEventHandlers() {
    mainStage.setOnShown(event -> showFloatingWindows());
    mainStage.setOnCloseRequest(event -> closeFloatingWindows());
    mainStage.setOnHidden(event -> floatingWindows.forEach(floatingWindow -> floatingWindow.getFloatingStage().hide()));
    initializeUndockEvents();
  }

  private void initializeUndockEvents() {
    mainStage.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
      if (undockNewWindow != null) {
        undockNewWindow.onTabUndockedDragged(event);
      }
    });
    mainStage.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
      if (undockNewWindow != null) {
        undockNewWindow.onTabDropped(event);
        undockNewWindow = null;
      }
    });
  }

  private void showFloatingWindows() {
    for (DWindow floatingWindow : floatingWindows) {
      if (!floatingWindow.getIsDocked()) {
        floatingWindow.getFloatingStage().show();
        Platform.runLater(floatingWindow.getFloatingStage()::toFront);
      }
    }
  }

  private void closeFloatingWindows() {
    for (DWindow floatingWindow : floatingWindows) {
      floatingWindow.getFloatingStage().close();
    }
    dockIndicator.getIndicatorStage().close();
    floatingWindows.clear();
  }

  private void hideFloatingWindow(DWindow dWindow) {
    dWindow.setIsDocked(true);
    dWindow.getFloatingStage().setOpacity(0);
    dWindow.getFloatingStage().hide();
  }

  private void setupFloatingWindow(TabPane floatingTabPane, Stage floatingWindow, TabPane targetTabPane) {
    if (floatingTabPane.getScene() == null) {
      Scene newScene = new Scene(floatingTabPane);
      newScene.getStylesheets().setAll(mainStage.getScene().getStylesheets());
      floatingWindow.setScene(newScene);
    }
    floatingWindow.setWidth(targetTabPane.getWidth());
    floatingWindow.setHeight(targetTabPane.getHeight());
    floatingWindow.setOpacity(1);
    floatingWindow.show();
  }

  private TabPane createTabPane(StringProperty title, Node content) {
    TabPane floatingTabPane = new TabPane();
    floatingTabPane.getStyleClass().add("dock-tab-pane");
    Label tabLabel = new Label();
    tabLabel.getStyleClass().add("dock-tab-label");
    tabLabel.textProperty().bind(title);
    Tab tab = new Tab();
    tab.setGraphic(tabLabel);
    tab.getStyleClass().add("dock-tab");
    tab.setClosable(false);
    tab.setContent(content);
    floatingTabPane.getTabs().add(tab);
    return floatingTabPane;
  }

  private SplitPane findParentSplitPane(Node child) {
    for (SplitPane parent : splitPanes) {
      if (parent.getItems().contains(child)) {
        return parent;
      }
    }
    return null;
  }
}

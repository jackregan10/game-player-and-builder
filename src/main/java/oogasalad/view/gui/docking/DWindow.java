package oogasalad.view.gui.docking;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * The floating window of the docking system.
 * Users can modify the floating window's size, position, and content.
 * The UI style of the floating window is corresponding to the default style of the docker's mainStage.
 *
 * @author Hsuan-Kai Liao
 */
public class DWindow {
  // Constants
  private static final double DEFAULT_FLOATING_WIDTH = 250;
  private static final double DEFAULT_FLOATING_HEIGHT = 200;
  private static final double DEFAULT_FLOATING_OPACITY = 0.5;
  private static final double FULL_OPACITY = 1.0;
  private static final int UNDOCK_MINIMUM_DISTANCE = 20;

  // Dragging attributes
  private double xOffset = 0;
  private double yOffset = 0;
  private Point2D dragStartPoint = null;

  // Docking attributes
  private final Docker docker;
  private final Stage floatingStage;
  private final TabPane floatingTabPane;
  private boolean isDocked = false;

  // Listeners
  private EventHandler<WindowEvent> onCloseEvent;
  private EventHandler<ActionEvent> onDockEvent;
  private EventHandler<ActionEvent> onUndockEvent;

  // Settings
  private boolean isDockOnClose = true;

  /**
   * Constructs a floating window with the given stage and tab pane.
   * @param floatingStage the stage of the floating window
   * @param floatingTabPane the tab pane of the floating window that contains the content
   */
  DWindow(Stage floatingStage, TabPane floatingTabPane, Docker docker) {
    this.docker = docker;
    this.floatingStage = floatingStage;
    this.floatingTabPane = floatingTabPane;

    initializeWindowSize();
    initializeScene();
    initializeTabEvents();
  }

  /* PACKAGE GETTER */

  /**
   * Returns the TabPane used for floating (undocked) tabs.
   */
  TabPane getFloatingTabPane() {
    return floatingTabPane;
  }

  /**
   * Retrieves the event handler that is triggered when a tab is docked.
   */
  EventHandler<ActionEvent> getOnDockEvent() {
    return onDockEvent;
  }

  /**
   * Retrieves the event handler that is triggered when a tab is undocked.
   */
  EventHandler<ActionEvent> getOnUndockEvent() {
    return onUndockEvent;
  }

  /**
   * Sets whether the tab is currently docked or undocked.
   *
   * @param isDocked true if the tab is docked, false if floating
   */
  void setIsDocked(boolean isDocked) {
    this.isDocked = isDocked;
  }


  /* API BELOW */

  /**
   * Gets whether the tab is currently docked or undocked.
   */
  public boolean getIsDocked() {
    return isDocked;
  }

  /**
   * Get the floating Stage of this DWindow object.
   */
  public Stage getFloatingStage() {
    return floatingStage;
  }

  /**
   * Retrieves the width of the floating stage.
   *
   * @return the width of the floating stage as a double
   */
  public double getWidth() { return floatingStage.getWidth(); }
  /**
   * Retrieves the height of the floating stage window.
   *
   * @return the height of the floating stage as a double
   */
  public double getHeight() { return floatingStage.getHeight(); }

  /**
   * Sets the width of the floating stage window.
   *
   * @param width the desired width of the floating stage in pixels
   */
  public void setWidth(double width) { floatingStage.setWidth(width); }
  /**
   * Sets the height of the floating stage window.
   *
   * @param height the new height to set for the floating stage
   */
  public void setHeight(double height) { floatingStage.setHeight(height); }

  /**
   * Retrieves the current X-coordinate of the floating stage.
   *
   * @return the X-coordinate of the floating stage as a double
   */
  public double getX() { return floatingStage.getX(); }
  /**
   * Retrieves the current Y-coordinate of the floating stage.
   *
   * @return the Y-coordinate of the floating stage as a double
   */
  public double getY() { return floatingStage.getY(); }

  /**
   * Sets the x-coordinate position of the floating stage.
   *
   * @param x the new x-coordinate position to set
   */
  public void setX(double x) { floatingStage.setX(x); }
  /**
   * Sets the Y-coordinate position of the floating stage.
   *
   * @param y the new Y-coordinate position to set for the floating stage
   */
  public void setY(double y) { floatingStage.setY(y); }

  /**
   * Retrieves the content of the first tab in the floating tab pane.
   *
   * @return the content node of the first tab
   */
  public Node getContent() { return floatingTabPane.getTabs().getFirst().getContent(); }
  /**
   * Sets the content of the first tab in the floating tab pane.
   *
   * @param content the Node to be set as the content of the first tab
   */
  public void setContent(Node content) { floatingTabPane.getTabs().getFirst().setContent(content); }

  /**
   * Sets the event handler to be executed when the window is closed.
   *
   * @param event the event handler to handle the window close event
   */
  public void setOnClose(EventHandler<WindowEvent> event) { this.onCloseEvent = event; }
  /**
   * Sets the event handler to be executed when a dock event occurs.
   *
   * @param onDockEvent the event handler to handle dock events
   */
  public void setOnDockEvent(EventHandler<ActionEvent> onDockEvent) { this.onDockEvent = onDockEvent; }
  /**
   * Sets the event handler to be executed when the undock action is triggered.
   *
   * @param onUndockEvent the event handler to handle the undock action
   */
  public void setOnUndockEvent(EventHandler<ActionEvent> onUndockEvent) { this.onUndockEvent = onUndockEvent; }

  /**
   * Sets whether the window should dock when it is closed.
   *
   * @param isDockOnClose true if the window should dock on close, false otherwise
   */
  public void setDockOnClose(boolean isDockOnClose) { this.isDockOnClose = isDockOnClose; }
  /**
   * Determines whether the window should dock when it is closed.
   *
   * @return true if the window should dock on close, false otherwise
   */
  public boolean isDockOnClose() { return isDockOnClose; }

  /* CALLBACKS BELOW */

  void onTabDropped(MouseEvent event) {
    double mouseX = event.getScreenX();
    double mouseY = event.getScreenY();

    if (docker.getDockIndicator().isMouseInsideIndicator(mouseX, mouseY)) {
      dockTabIfNecessary(mouseX, mouseY);
    }

    docker.getDockIndicator().hideDockIndicator();
    floatingStage.setOpacity(1);
  }

  void onTabUndockedDragged(MouseEvent event) {
    double mouseX = event.getScreenX();
    double mouseY = event.getScreenY();
    double decorationBarHeight = floatingStage.getHeight() - floatingStage.getScene().getHeight();

    floatingStage.setX(mouseX - xOffset);
    floatingStage.setY(mouseY - yOffset - decorationBarHeight);

    docker.getDockIndicator().showDockIndicator(mouseX, mouseY);
    updateWindowOpacityOnDrag();
  }

  private void onTabDockedDragged(MouseEvent event) {
    double mouseX = event.getScreenX();
    double mouseY = event.getScreenY();

    double dragDistance = calculateDragDistance(mouseX, mouseY);
    double decorationBarHeight = docker.getMainStage().getHeight() - docker.getMainStage().getScene().getHeight();

    floatingStage.setX(mouseX - xOffset);
    floatingStage.setY(mouseY - yOffset - decorationBarHeight);

    if (dragDistance > UNDOCK_MINIMUM_DISTANCE) {
      docker.undockTab(this);
      updateWindowOpacityOnDrag();
    }
  }

  private void onTabPressed(MouseEvent event) {
    double mouseX = event.getScreenX();
    double mouseY = event.getScreenY();

    if (isDocked) {
      calculateTabOffset(mouseX, mouseY);
    } else {
      xOffset = event.getSceneX();
      yOffset = event.getSceneY();
    }
    dragStartPoint = new Point2D(mouseX, mouseY);
  }

  private void onFloatingClose(WindowEvent event) {
    if (!isDockOnClose) {
      handleCustomCloseEvent(event);
      docker.removeFloatingWindow(this);
      return;
    }

    Docker.DockPosition nearestSide = calculateNearestDockPosition();
    docker.dockTab(this, null, nearestSide);
    event.consume();
  }

  /* HELPER METHODS */

  private void initializeWindowSize() {
    floatingStage.setWidth(DEFAULT_FLOATING_WIDTH);
    floatingStage.setHeight(DEFAULT_FLOATING_HEIGHT);
  }

  private void initializeScene() {
    Scene floatingScene = new Scene(floatingTabPane, DEFAULT_FLOATING_WIDTH, DEFAULT_FLOATING_HEIGHT);
    floatingStage.setScene(floatingScene);
    floatingScene.getRoot().applyCss();
  }

  private void initializeTabEvents() {
    Tab tab = floatingTabPane.getTabs().getFirst();
    Node tabHeaderArea = tab.getGraphic();
    if (tabHeaderArea != null) {
      tabHeaderArea.setOnMousePressed(this::onTabPressed);
      tabHeaderArea.setOnMouseDragged(event -> {
        if (isDocked) {
          onTabDockedDragged(event);
        } else {
          onTabUndockedDragged(event);
        }
      });
    }

    floatingStage.setOnCloseRequest(this::onFloatingClose);
    floatingTabPane.setOnMouseReleased(this::onTabDropped);
  }

  private void dockTabIfNecessary(double mouseX, double mouseY) {
    TabPane targetTabPane = docker.findTabPaneUnderMouse(mouseX, mouseY);
    floatingTabPane.setMouseTransparent(true);

    if (targetTabPane != null) {
      docker.dockTab(this, targetTabPane, docker.getDockIndicator().getIndicatorPosition());
    } else if (!docker.isMouseInsideMainScene(mouseX, mouseY)) {
      docker.dockTab(this, null, docker.getDockIndicator().getIndicatorPosition());
    }

    Platform.runLater(() -> floatingTabPane.setMouseTransparent(false));
  }

  private void updateWindowOpacityOnDrag() {
    if (!docker.getWindowOpaqueOnDragging() && floatingStage.getOpacity() == FULL_OPACITY) {
      floatingStage.setOpacity(DEFAULT_FLOATING_OPACITY);
    }
  }

  private double calculateDragDistance(double mouseX, double mouseY) {
    double dragOffsetX = mouseX - dragStartPoint.getX();
    double dragOffsetY = mouseY - dragStartPoint.getY();
    return Math.sqrt(dragOffsetX * dragOffsetX + dragOffsetY * dragOffsetY);
  }

  private void calculateTabOffset(double mouseX, double mouseY) {
    Tab targetTab = (Tab) floatingTabPane.getTabs().getFirst().getUserData();
    TabPane targetTabPane = targetTab.getTabPane();
    Region targetTabHeaderArea = (Region) targetTabPane.lookup(".tab-header-area");

    Insets targetTabHeaderAreaPadding = targetTabHeaderArea.getPadding();
    Point2D targetTabOffset = targetTab.getGraphic().screenToLocal(mouseX, mouseY);

    double tabOffsetX = targetTabOffset.getX() + targetTabHeaderAreaPadding.getLeft();
    double headerOffsetX = targetTabHeaderArea.screenToLocal(mouseX, mouseY).getX();

    xOffset = Math.min(tabOffsetX, headerOffsetX);
    yOffset = targetTabOffset.getY() + targetTabHeaderAreaPadding.getTop();
  }

  private Docker.DockPosition calculateNearestDockPosition() {
    double floatingX = floatingStage.getX();
    double floatingY = floatingStage.getY();
    double floatingWidth = floatingStage.getWidth();
    double floatingHeight = floatingStage.getHeight();
    Stage mainStage = docker.getMainStage();

    double floatingCenterX = floatingX + floatingWidth / 2;
    double floatingCenterY = floatingY + floatingHeight / 2;

    double distanceToLeft = floatingCenterX - mainStage.getX();
    double distanceToRight = mainStage.getX() + mainStage.getWidth() - floatingCenterX;
    double distanceToTop = floatingCenterY - mainStage.getY();
    double distanceToBottom = mainStage.getY() + mainStage.getHeight() - floatingCenterY;

    Docker.DockPosition nearestSide = Docker.DockPosition.TOP;
    double minDistance = distanceToTop;

    if (minDistance > distanceToLeft) {
      minDistance = distanceToLeft;
      nearestSide = Docker.DockPosition.LEFT;
    }

    if (minDistance > distanceToRight) {
      minDistance = distanceToRight;
      nearestSide = Docker.DockPosition.RIGHT;
    }

    if (minDistance > distanceToBottom) {
      nearestSide = Docker.DockPosition.BOTTOM;
    }

    return nearestSide;
  }

  private void handleCustomCloseEvent(WindowEvent event) {
    if (onCloseEvent != null) {
      onCloseEvent.handle(event);
    }
  }
}

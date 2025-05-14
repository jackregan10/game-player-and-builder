package oogasalad.view.gui.panel;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * This the panel UI widget that holds the draggable and zoom-able panel with coordinate system
 * relative to the input Content.
 *
 * @author Hsuan-Kai Liao
 */
public class CoordinatePanel extends Pane {

  private static final double DEFAULT_ZOOM = 0.5;
  private static final double MAX_ZOOM = 5.0;
  private static final double MIN_ZOOM = 0.1;
  private static final double DEFAULT_ZOOM_START_X = -1400;
  private static final double DEFAULT_ZOOM_START_Y = -200;
  private static final double GRID_BASE_SIZE = 50;
  private static final double GRID_X_COORDS_OFFSETX = -4;
  private static final double GRID_X_COORDS_OFFSETY = -1;
  private static final double GRID_Y_COORDS_OFFSETX = 5;
  private static final double GRID_Y_COORDS_OFFSETY = 6;

  private final Node myGameCanvas;
  private final Pane canvasHolder;
  private final Canvas gridCanvas;
  private boolean isDragLocked;

  /**
   * Constructor for CoordinatePanel.
   *
   * @param content the main panel to be controlled and tracked of.
   */
  public CoordinatePanel(Node content) {
    this.myGameCanvas = content;
    this.canvasHolder = new Pane(myGameCanvas);
    this.gridCanvas = new Canvas();

    setupGridCanvas(this);
    bindSizes(this);
    bindGridUpdateListeners(this);

    drawGrid();
    setupZoomAndPan(this, canvasHolder);
  }

  /**
   * Set whether the dragging is locked.
   * @param locked the dragging state.
   */
  public void setDragLocked(boolean locked) {
    this.isDragLocked = locked;
  }

  /**
   * Reset the canvas to its original position and zoom factor.
   */
  public void resetTransform() {
    Point2D zoomOrigin = new Point2D(0, 0);
    Point2D zoomOriginInCanvas = canvasHolder.parentToLocal(zoomOrigin);
    zoomCanvas(canvasHolder, DEFAULT_ZOOM, zoomOrigin, zoomOriginInCanvas);
    Point2D canvasTopLeftInContainer = canvasHolder.localToParent(0, 0);

    double deltaX = -canvasTopLeftInContainer.getX();
    double deltaY = -canvasTopLeftInContainer.getY();

    canvasHolder.setTranslateX(canvasHolder.getTranslateX() + deltaX);
    canvasHolder.setTranslateY(canvasHolder.getTranslateY() + deltaY);
  }

  private void setupGridCanvas(Pane container) {
    gridCanvas.setMouseTransparent(true);
    container.getChildren().addAll(gridCanvas, canvasHolder);
  }

  private void bindSizes(Pane container) {
    gridCanvas.widthProperty().bind(container.widthProperty());
    gridCanvas.heightProperty().bind(container.heightProperty());
    canvasHolder.prefWidthProperty().bind(container.widthProperty());
    canvasHolder.prefHeightProperty().bind(container.heightProperty());
  }

  private void bindGridUpdateListeners(Pane container) {
    canvasHolder.translateXProperty().addListener((obs, oldVal, newVal) -> drawGrid());
    canvasHolder.translateYProperty().addListener((obs, oldVal, newVal) -> drawGrid());
    canvasHolder.scaleXProperty().addListener((obs, oldVal, newVal) -> drawGrid());
    canvasHolder.scaleYProperty().addListener((obs, oldVal, newVal) -> drawGrid());
    container.widthProperty().addListener((obs, oldVal, newVal) -> drawGrid());
    container.heightProperty().addListener((obs, oldVal, newVal) -> drawGrid());
  }

  private void drawGrid() {
    GraphicsContext gc = gridCanvas.getGraphicsContext2D();
    double width = gridCanvas.getWidth();
    double height = gridCanvas.getHeight();

    // TODO: change this to CSS
    gc.clearRect(0, 0, width, height);
    gc.setStroke(Color.rgb(229, 229, 229, 0.4));
    gc.setLineWidth(1);
    gc.setFont(Font.font("Monospaced", 10));
    gc.setFill(Color.GRAY);

    double scale = canvasHolder.getScaleX();
    double gridSize = computeAdaptiveGridSize(scale);

    Point2D canvasOriginInScene = myGameCanvas.localToScene(0, 0);
    Point2D origin = gridCanvas.sceneToLocal(canvasOriginInScene);
    double originX = origin.getX();
    double originY = origin.getY();

    drawVerticalGridLines(gc, width, scale, gridSize, originX);
    drawHorizontalGridLines(gc, height, scale, gridSize, originY);
  }

  private void drawVerticalGridLines(GraphicsContext gc, double width, double scale, double gridSize, double originX) {
    double startWorldX = -originX / scale;
    double startGridX = Math.floor(startWorldX / gridSize) * gridSize;
    for (double wx = startGridX; ; wx += gridSize) {
      double x = originX + wx * scale;
      if (x > width) break;
      if (x >= 0) drawVerticalLineWithLabel(gc, x, wx);
    }
  }

  private void drawHorizontalGridLines(GraphicsContext gc, double height, double scale, double gridSize, double originY) {
    double startWorldY = -originY / scale;
    double startGridY = Math.floor(startWorldY / gridSize) * gridSize;
    for (double wy = startGridY; ; wy += gridSize) {
      double y = originY + wy * scale;
      if (y > height) break;
      if (y >= 0) drawHorizontalLineWithLabel(gc, y, wy);
    }
  }

  private void drawVerticalLineWithLabel(GraphicsContext gc, double x, double worldX) {
    gc.strokeLine(x, 0, x, gridCanvas.getHeight());
    gc.save();
    gc.translate(x + 2, 12);
    gc.rotate(45);
    gc.fillText(String.format("%.0f", worldX), GRID_X_COORDS_OFFSETX, GRID_X_COORDS_OFFSETY);
    gc.restore();
  }

  private void drawHorizontalLineWithLabel(GraphicsContext gc, double y, double worldY) {
    gc.strokeLine(0, y, gridCanvas.getWidth(), y);
    gc.save();
    gc.translate(2, y - 2);
    gc.rotate(45);
    gc.fillText(String.format("%.0f", worldY), GRID_Y_COORDS_OFFSETX, GRID_Y_COORDS_OFFSETY);
    gc.restore();
  }

  private double computeAdaptiveGridSize(double scale) {
    double gridSize = 1.0;
    while (gridSize * scale < GRID_BASE_SIZE) {
      gridSize *= 2.0;
    }
    return gridSize;
  }

  private void setupZoomAndPan(Pane container, Pane canvasHolder) {
    final ObjectProperty<Point2D> lastMousePosition = new SimpleObjectProperty<>();

    Point2D startZoomCenter = new Point2D(DEFAULT_ZOOM_START_X, DEFAULT_ZOOM_START_Y);
    Point2D startZoomCenterInGroup = canvasHolder.parentToLocal(startZoomCenter);
    zoomCanvas(canvasHolder, DEFAULT_ZOOM, startZoomCenter, startZoomCenterInGroup);

    container.setOnMousePressed(event -> {
      if (isDragLocked) return;
      lastMousePosition.set(new Point2D(event.getSceneX(), event.getSceneY()));
      container.requestFocus();
    });

    container.setOnMouseDragged(event -> {
      if (isDragLocked) return;
      Point2D currentMousePosition = new Point2D(event.getSceneX(), event.getSceneY());
      Point2D delta = currentMousePosition.subtract(lastMousePosition.get());

      canvasHolder.setTranslateX(canvasHolder.getTranslateX() + delta.getX());
      canvasHolder.setTranslateY(canvasHolder.getTranslateY() + delta.getY());

      lastMousePosition.set(currentMousePosition);
    });

    container.setOnScroll(event -> {
      double zoomFactor = 1.05;
      double deltaY = event.getDeltaY();
      double oldScale = canvasHolder.getScaleX();
      double scale = (deltaY > 0) ? oldScale * zoomFactor : oldScale / zoomFactor;

      Point2D mousePoint = new Point2D(event.getX(), event.getY());
      Point2D mouseInGroup = canvasHolder.parentToLocal(mousePoint);

      zoomCanvas(canvasHolder, scale, mousePoint, mouseInGroup);
      event.consume();
    });

    container.setOnZoom(event -> {
      double oldScale = canvasHolder.getScaleX();
      double scale = oldScale * event.getZoomFactor();

      Point2D zoomCenter = new Point2D(event.getX(), event.getY());
      Point2D zoomCenterInGroup = canvasHolder.parentToLocal(zoomCenter);

      zoomCanvas(canvasHolder, scale, zoomCenter, zoomCenterInGroup);
      event.consume();
    });
  }

  private void zoomCanvas(Pane canvasHolder, double scale, Point2D zoomPoint,
      Point2D pointInGroup) {
    scale = Math.min(Math.max(scale, MIN_ZOOM), MAX_ZOOM);
    canvasHolder.setScaleX(scale);
    canvasHolder.setScaleY(scale);

    Point2D newMouseInGroup = canvasHolder.parentToLocal(zoomPoint);

    Point2D delta = newMouseInGroup.subtract(pointInGroup);
    canvasHolder.setTranslateX(canvasHolder.getTranslateX() + delta.getX() * scale);
    canvasHolder.setTranslateY(canvasHolder.getTranslateY() + delta.getY() * scale);
  }
}

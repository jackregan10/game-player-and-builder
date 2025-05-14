package oogasalad.view.scene.builder;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import oogasalad.model.builder.Builder;
import oogasalad.model.builder.StateMonitors.TransformState;
import oogasalad.model.builder.UndoRedoManager;
import oogasalad.model.builder.actions.BuilderMoveObjectAction;
import oogasalad.model.builder.actions.BuilderResizeObjectAction;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.Transform;
import java.util.ArrayList;
import java.util.List;

/**
 * ObjectDragger manages mouse drag and drop for Builder pages
 *
 * @author Reyan Shariff, Calvin Chen
 */
public class ObjectDragger {

  public static final double HANDLE_SIZE = 8;

  private static final int MIN_VALUE_BUILDER_WIDTH = 1;

  private final Pane canvas;
  private final Map<ResizeHandlePosition, BiConsumer<Double, Double>> enumMap;
  private EventHandler<MouseEvent> mouseMoveHandler;
  private final Builder builder;
  private ResizeHandlePosition activeHandle;

  private double oldX, oldY;
  private double startX, startY;
  private double offsetX, offsetY;
  private double newX, newY, newW, newH;
  private double resizeStartX, resizeStartY, resizeStartW, resizeStartH;

  private boolean isResizing = false;
  private boolean isMoving = false;

  private Runnable onComplete;

  /**
   * Enums to show handles on selected image.
   */
  private enum ResizeHandlePosition {
    NONE,
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    MID_RIGHT,
    BOTTOM_RIGHT,
    BOTTOM_CENTER,
    BOTTOM_LEFT,
    MID_LEFT
  }

  /**
   * Constructor for ObjectDragger
   *
   * @param canvas is the canvas to draw on, represented by a Pane object
   */
  public ObjectDragger(Builder builder, Pane canvas) {
    this.builder = builder;
    this.canvas = canvas;
    this.enumMap = new HashMap<>();
    this.activeHandle = ResizeHandlePosition.NONE;

    // Setup Listeners
    setUpListeners();
  }

  /**
   * Set the callback for the event after all the updates of the transform change of the object is
   * complete.
   *
   * @param onComplete the callback to be subscribed
   */
  public void setOnComplete(Runnable onComplete) {
    this.onComplete = onComplete;
  }

  private void setUpListeners() {
    setCanvasSceneListener();
    setMouseMoveHandler();
    setCanvasChildrenListener();
  }

  private void setCanvasSceneListener() {
    this.canvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
      if (newScene != null) {
        registerCanvasEventHandlers();
      }
    });
  }

  private void registerCanvasEventHandlers() {
    Node parent = this.canvas.getParent().getParent();
    parent.addEventFilter(MouseEvent.MOUSE_PRESSED, this::handlePressed);
    parent.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::handleDragged);
    parent.addEventFilter(MouseEvent.MOUSE_RELEASED, this::handleReleased);
    parent.addEventFilter(MouseEvent.MOUSE_MOVED, this.mouseMoveHandler);
  }

  private void setMouseMoveHandler() {
    this.mouseMoveHandler = e -> {
      Point2D worldPt = getWorldCoords(e);
      boolean hovering = isHoveringOverResizeHandle(worldPt);
      this.canvas.setCursor(hovering ? Cursor.HAND : Cursor.DEFAULT);
    };
  }

  private void setCanvasChildrenListener() {
    this.canvas.getChildren().addListener((ListChangeListener<Node>) change -> {
      while (change.next()) {
        if (change.wasAdded()) {
          handleAddedNodes((List<Node>) change.getAddedSubList());
        }
      }
    });
  }

  private void handleAddedNodes(List<Node> addedNodes) {
    for (Node node : addedNodes) {
      node.setMouseTransparent(true);
    }
  }

  /* PRESS */

  private void handlePressed(MouseEvent e) {
    Point2D worldPt = getWorldCoords(e);
    oldX = worldPt.getX();
    oldY = worldPt.getY();
    if (builder.objectIsSelected())
    {
      Transform t = builder.getSelectedObject().getComponent(Transform.class);
      startX = t.getX();
      startY = t.getY();
    }
    GameObject selectedObject = builder.getSelectedObject();
    if (processSelectedObject(selectedObject, worldPt)) {
      return;
    }
    List<GameObject> objects = applyObjectsRenderOrder(
        new ArrayList<>(builder.getCurrentScene().getActiveObjects())
    );
    handleDeselection(processOtherObjects(objects, selectedObject, worldPt));
  }

  private boolean processSelectedObject(GameObject selectedObject, Point2D worldPt) {
    if (selectedObject == null) {
      return false;
    }
    Transform transform = selectedObject.getComponent(Transform.class);
    return processInteraction(worldPt, selectedObject, transform);
  }

  private boolean processOtherObjects(List<GameObject> objects, GameObject selectedObject,
      Point2D worldPt) {
    for (GameObject obj : objects) {
      if (obj.equals(selectedObject)) {
        continue;
      }
      Transform transform = obj.getComponent(Transform.class);
      if (processInteraction(worldPt, obj, transform)) {
        return true;
      }
    }
    return false;
  }

  private boolean processInteraction(Point2D worldPt, GameObject obj, Transform t) {
    if (isHoveringOverResizeHandle(worldPt)) {
      startResizing(worldPt, t);
      return true;
    }

    if (isInsideBoundingBox(worldPt, t)) {
      startMoving(obj, worldPt, t);
      return true;
    }

    return false;
  }

  private void startMoving(GameObject obj, Point2D worldCoords, Transform t) {
    builder.selectExistingObject(obj);

    oldX = worldCoords.getX();
    oldY = worldCoords.getY();
    offsetX = oldX - t.getX();
    offsetY = oldY - t.getY();

    isMoving = true;
    isResizing = false;
  }

  private boolean isInsideBoundingBox(Point2D coords, Transform t) {
    double x = coords.getX();
    double y = coords.getY();
    double tX = t.getX();
    double tY = t.getY();
    double width = t.getScaleX();
    double height = t.getScaleY();

    return x >= tX && x <= tX + width &&
        y >= tY && y <= tY + height;
  }

  private void startResizing(Point2D worldCoords, Transform t) {
    resizeStartX = t.getX();
    resizeStartY = t.getY();
    resizeStartW = t.getScaleX();
    resizeStartH = t.getScaleY();

    offsetX = oldX - resizeStartX;
    offsetY = oldY - resizeStartY;
    oldX = worldCoords.getX();
    oldY = worldCoords.getY();

    isResizing = true;
    isMoving = false;
  }

  private void handleDeselection(boolean clickedOnObject) {
    if (builder.objectIsSelected() && !clickedOnObject) {
      recordResizing();
      builder.deselect();
      activeHandle = ResizeHandlePosition.NONE;
      isResizing = false;
      isMoving = false;
    }
  }

  /* DRAG */

  private void handleDragged(MouseEvent e) {
    Point2D worldPt = getWorldCoords(e);

    if (!builder.objectIsSelected()) {
      return;
    }

    if (isResizing) {
      double dx = worldPt.getX() - oldX;
      double dy = worldPt.getY() - oldY;
      oldX = worldPt.getX();
      oldY = worldPt.getY();
      resizeFromHandle(builder.getSelectedObject(), dx, dy);
    } else if (isMoving) {
      double x = worldPt.getX() - offsetX;
      double y = worldPt.getY() - offsetY;
      builder.moveObject(x, y);
    }
  }

  private boolean isHoveringOverResizeHandle(Point2D worldCoords) {
    GameObject selected = builder.getSelectedObject();
    if (selected == null) {
      return false;
    }
    Transform t = selected.getComponent(Transform.class);
    if (t == null) {
      return false;
    }

    double w = t.getScaleX();
    double h = t.getScaleY();

    List<Point2D> resizeHandlePositions = List.of(
        new Point2D(t.getX(), t.getY()),
        new Point2D(t.getX() + w / 2, t.getY()),
        new Point2D(t.getX() + w, t.getY()),
        new Point2D(t.getX() + w, t.getY() + h / 2),
        new Point2D(t.getX() + w, t.getY() + h),
        new Point2D(t.getX() + w / 2, t.getY() + h),
        new Point2D(t.getX(), t.getY() + h),
        new Point2D(t.getX(), t.getY() + h / 2)
    );

    double scaleFactor = 1;
    if (canvas.getParent() instanceof Pane holder) {
      scaleFactor = holder.getScaleX();
    }

    int one = 1;
    int two = 2;
    for (int i = one; i <= resizeHandlePositions.size(); i++) {
      if (worldCoords.distance(resizeHandlePositions.get(i - one))
          <= HANDLE_SIZE /two  / scaleFactor) {
        activeHandle = ResizeHandlePosition.values()[i];
        return true;
      }
    }

    return false;
  }

  private void resizeFromHandle(GameObject obj, double dx, double dy) {
    Transform t = obj.getComponent(Transform.class);

    double x = t.getX();
    double y = t.getY();
    double w = t.getScaleX();
    double h = t.getScaleY();

    initEnumMap(x, y, w, h);
    BiConsumer<Double, Double> handleMapping = enumMap.get(activeHandle);
    if (handleMapping != null) {
      handleMapping.accept(dx, dy);
    }

    if (newW > MIN_VALUE_BUILDER_WIDTH && newH > MIN_VALUE_BUILDER_WIDTH) {
      builder.resizeObject(newX, newY, newW, newH);
    }
  }

  private void initEnumMap(double resizeStartX, double resizeStartY, double resizeStartW, double resizeStartH) {
    enumMap.put(ResizeHandlePosition.TOP_LEFT, (dx, dy) -> { newX = resizeStartX + dx; newY = resizeStartY + dy; newW = resizeStartW - dx; newH = resizeStartH - dy; });
    enumMap.put(ResizeHandlePosition.TOP_CENTER, (dx, dy) -> { newX = resizeStartX; newY = resizeStartY + dy; newW = resizeStartW; newH = resizeStartH - dy; });
    enumMap.put(ResizeHandlePosition.TOP_RIGHT, (dx, dy) -> { newX = resizeStartX; newY = resizeStartY + dy; newW = resizeStartW + dx; newH = resizeStartH - dy; });
    enumMap.put(ResizeHandlePosition.MID_RIGHT, (dx, dy) -> { newX = resizeStartX; newY = resizeStartY; newW = resizeStartW + dx; newH = resizeStartH; });
    enumMap.put(ResizeHandlePosition.BOTTOM_RIGHT, (dx, dy) -> { newX = resizeStartX; newY = resizeStartY; newW = resizeStartW + dx; newH = resizeStartH + dy; });
    enumMap.put(ResizeHandlePosition.BOTTOM_CENTER, (dx, dy) -> { newX = resizeStartX; newY = resizeStartY; newW = resizeStartW; newH = resizeStartH + dy; });
    enumMap.put(ResizeHandlePosition.BOTTOM_LEFT, (dx, dy) -> { newX = resizeStartX + dx; newY = resizeStartY; newW = resizeStartW - dx; newH = resizeStartH + dy; });
    enumMap.put(ResizeHandlePosition.MID_LEFT, (dx, dy) -> { newX = resizeStartX + dx; newY = resizeStartY; newW = resizeStartW - dx; newH = resizeStartH; });
  }

  /* RELEASE */

  private void handleReleased(MouseEvent e) {
    if (!builder.objectIsSelected()) {
      return;
    }

    if (isMoving && !isResizing) {
      handleMoveReleased(e);
    } else if (isResizing) {
      recordResizing();
    }

    resetDraggingState();
  }

  private void handleMoveReleased(MouseEvent e) {

    Transform t = builder.getSelectedObject().getComponent(Transform.class);
    double finalX = t.getX();
    double finalY = t.getY();

    builder.placeObject(finalX, finalY);
    if (!(oldX == finalX && oldY == finalY)) {
      UndoRedoManager.addAction(
          new BuilderMoveObjectAction(builder.getSelectedObject(), startX, startY, finalX, finalY));
    }
  }

  private void resetDraggingState() {
    isMoving = false;
    isResizing = false;
    activeHandle = ResizeHandlePosition.NONE;

    if (onComplete != null) {
      onComplete.run();
    }
  }

  private void recordResizing() {
    Transform t = builder.getSelectedObject().getComponent(Transform.class);
    TransformState next = new TransformState(t.getX(), t.getY(), t.getScaleX(), t.getScaleY());
    TransformState prev = new TransformState(resizeStartX, resizeStartY, resizeStartW,
        resizeStartH);
    BuilderResizeObjectAction resizeAction = new BuilderResizeObjectAction(
        builder.getSelectedObject(), prev, next
    );
    if (!(resizeStartX == 0 && resizeStartY == 0 && resizeStartW == 0 && resizeStartH == 0)) {
      UndoRedoManager.addAction(resizeAction);
    }
  }

  /* HELPER */

  private Point2D getWorldCoords(MouseEvent e) {
    return canvas.getParent().sceneToLocal(e.getSceneX(), e.getSceneY());
  }

  private List<GameObject> applyObjectsRenderOrder(List<GameObject> objects) {
    return objects.stream()
        .map(obj -> obj.getComponent(Transform.class))
        .sorted((t1, t2) -> Integer.compare(t2.getZIndex(), t1.getZIndex())) // reverseOrder
        .map(GameComponent::getParent)
        .toList();
  }
}

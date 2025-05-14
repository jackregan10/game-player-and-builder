package oogasalad.view.renderer.componentRenderer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.component.Renderable;
import oogasalad.model.serialization.serializable.TypeRef;
import oogasalad.model.serialization.serializable.TypeReferenceException;
import org.reflections.Reflections;

/**
 * Base class for rendering Renderable components onto either a Pane or a Canvas.
 * Subclasses must implement how the component is drawn in both editor (Pane) and canvas (GraphicsContext) modes.
 *
 * @author Hsuan-Kai Liao
 */
public abstract class ComponentRenderer<T extends Renderable> {

  private static final String ERROR_CREATE_RENDERER_MESSAGE = "errorCreateRenderer";
  private static final String COMPONENT_RENDERER_PACKAGE = ComponentRenderer.class.getPackage().getName();
  private static final Map<Type, ComponentRenderer<?>> rendererPool = new HashMap<>();

  static {
    Reflections reflections = new Reflections(COMPONENT_RENDERER_PACKAGE);
    for (Class<? extends ComponentRenderer> clazz : reflections.getSubTypesOf(ComponentRenderer.class)) {
      if (!Modifier.isAbstract(clazz.getModifiers())) {
        try {
          ComponentRenderer<?> instance = clazz.getDeclaredConstructor().newInstance();
          rendererPool.put(TypeRef.findGenericTypeArgument(clazz), instance);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
          throw new TypeReferenceException(GameConfig.getText(ERROR_CREATE_RENDERER_MESSAGE, clazz.getName()), e);
        }
      }
    }
  }

  /**
   * Renders a Renderable component onto a Pane.
   * Finds the appropriate ComponentRenderer based on the component's type.
   *
   * @param component the component to render
   * @param graphicPane the Pane to render onto
   */
  @SuppressWarnings("unchecked")
  public static void render(Renderable component, Pane graphicPane, double cameraX, double cameraY) {
    ComponentRenderer<Renderable> renderer = (ComponentRenderer<Renderable>) rendererPool.get(component.getClass());
    if (renderer != null) {
      renderer.onEditorRender(component, graphicPane, cameraX, cameraY);
    } else {
      throw new TypeReferenceException(GameConfig.getText(ERROR_CREATE_RENDERER_MESSAGE, component.getClass().getName()));
    }
  }

  /**
   * Renders a Renderable component onto a GraphicsContext (canvas).
   * Finds the appropriate ComponentRenderer based on the component's type.
   *
   * @param component the component to render
   * @param graphicsContext the GraphicsContext to render onto
   */
  @SuppressWarnings("unchecked")
  public static void render(Renderable component, GraphicsContext graphicsContext, double cameraX, double cameraY) {
    ComponentRenderer<Renderable> renderer = (ComponentRenderer<Renderable>) rendererPool.get(component.getClass());
    if (renderer != null) {
      renderer.onCanvasRender(component, graphicsContext, cameraX, cameraY);
    } else {
      throw new TypeReferenceException(GameConfig.getText(ERROR_CREATE_RENDERER_MESSAGE, component.getClass().getName()));
    }
  }

  /**
   * Defines how the component should be drawn onto a Pane (typically in an editor).
   *
   * @param component the component to render
   * @param graphicPane the Pane to render onto
   */
  protected abstract void onEditorRender(T component, Pane graphicPane, double cameraX, double cameraY);

  /**
   * Defines how the component should be drawn onto a GraphicsContext (typically in a game canvas).
   *
   * @param component the component to render
   * @param graphicsContext the GraphicsContext to render onto
   */
  protected abstract void onCanvasRender(T component, GraphicsContext graphicsContext, double cameraX, double cameraY);
}

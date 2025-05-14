package oogasalad.model.engine.architecture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import oogasalad.model.engine.component.ComponentTag;
import oogasalad.model.engine.component.Transform;
import org.junit.jupiter.api.Test;

public class GameComponentTest {

  @Test
  public void componentTag_getComponentUpdateOrder_returnsNone() {
    GameComponent component = new GameComponent() {
      @Override
      public ComponentTag componentTag() {
        return ComponentTag.NONE;
      }
    };
    assertEquals(ComponentTag.NONE, component.componentTag());
  }

  @Test
  public void getParent_getParentObject_returnsNull() {
    GameComponent component = new GameComponent() {
      @Override
      public ComponentTag componentTag() {
        return ComponentTag.NONE;
      }
    };
    assertNull(component.getParent());
  }

  @Test
  public void setParent_getParentObject_returnsParent() {
    GameObject parent = new GameObject("Parent", "Tag");
    GameComponent component = new GameComponent() {
      @Override
      public ComponentTag componentTag() {
        return ComponentTag.NONE;
      }
    };
    component.setParent(parent);
    assertEquals(parent, component.getParent());
  }

  @Test
  public void getComponent_getParentComponent_throwException() {
    GameComponent component = new GameComponent() {
      @Override
      public ComponentTag componentTag() {
        return ComponentTag.NONE;
      }
    };
    GameObject parent = new GameObject("Parent", "Tag");
    component.setParent(parent);
    assertThrows(IllegalArgumentException.class, () -> component.getComponent(GameComponent.class));
  }

  @Test
  public void getComponent_getParentComponent_getTransform() {
    GameComponent component = new GameComponent() {
      @Override
      public ComponentTag componentTag() {
        return ComponentTag.NONE;
      }
    };
    GameObject parent = new GameObject("Parent", "Tag");
    parent.addComponent(Transform.class);
    component.setParent(parent);
    assertNotNull(component.getComponent(Transform.class));
  }

}

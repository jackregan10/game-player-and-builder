package oogasalad.model.engine.architecture;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import oogasalad.model.engine.component.Transform;
import oogasalad.model.engine.component.Follower;

public class GameObjectTest {
    
    @Test
    public void GameObject_SimpleObjectConstruction_returnsTrue() {
        GameObject object = new GameObject("Object", "Tag");
        assertEquals("Object", object.getName());
        assertEquals("Tag", object.getTag());
        assertNotNull(object.getId());
    }

    @Test
    public void getComponent_retrievePresentComponent_returnsTrue() {
        GameObject object = new GameObject("Object", "Tag");
        object.addComponent(Transform.class);
        assertNotNull(object.getComponent(Transform.class));
    }

    @Test
    public void getComponent_retrieveAbsentComponent_throwsException() {
        GameObject object = new GameObject("Object", "Tag");
        assertThrows(IllegalArgumentException.class, () -> object.getComponent(Follower.class));
    }

    @Test
    public void hasComponent_checkPresentComponent_returnsTrue() {
        GameObject object = new GameObject("Object", "Tag");
        object.addComponent(new Transform());
        assertTrue(object.hasComponent(Transform.class));
    }

    @Test void hasComponent_checkAbsentComponent_returnsFalse() {
        GameObject object = new GameObject("Object", "Tag");
        assertFalse(object.hasComponent(Follower.class));
    }

    @Test
    public void addComponent_addComponentToObjectByClass_returnsTrue() {
        GameObject object = new GameObject("Object", "Tag");
        object.addComponent(Follower.class);
        assertNotNull(object.getComponent(Follower.class));
    }

    @Test
    public void addComponent_addExistingComponentByClass_throwsException() {
        GameObject object = new GameObject("Object", "Tag");
        object.addComponent(Follower.class);
        assertThrows(IllegalArgumentException.class, () -> object.addComponent(Follower.class));
    }

    @Test
    public void addComponent_addComponentObject_returnsTrue() {
        GameObject object = new GameObject("Object", "Tag");
        Follower follower = new Follower();
        object.addComponent(follower);
        assertEquals(follower, object.getComponent(Follower.class));
    }

    @Test
    public void addComponent_addExistingComponentObject_throwsException() {
        GameObject object = new GameObject("Object", "Tag");
        Follower follower = new Follower();
        object.addComponent(follower);
        assertThrows(IllegalArgumentException.class, () -> object.addComponent(follower));
    }

    @Test
    public void getAllComponents_getAllComponentsFromObject_returnsTrue() {
        GameObject object = new GameObject("Object", "Tag");
        object.addComponent(Transform.class);
        object.addComponent(Follower.class);
        assertEquals(2, object.getAllComponents().size());
    }

    @Test
    public void removeComponent_removeComponentFromObject_returnsTrue() {
        GameObject object = new GameObject("Object", "Tag");
        Follower follower = new Follower();
        object.addComponent(follower);
        assertEquals(follower, object.getComponent(Follower.class));
        assertEquals(follower.getParent(), object);
        object.removeComponent(Follower.class);
        assertNull(follower.getParent());
        assertThrows(IllegalArgumentException.class, () -> object.getComponent(Follower.class));
    }

    @Test
    public void removeComponent_removeTransform_throwsException(){
        GameObject object = new GameObject("Object", "Tag");
        assertThrows(IllegalArgumentException.class, () -> object.removeComponent(Transform.class));
    }
}

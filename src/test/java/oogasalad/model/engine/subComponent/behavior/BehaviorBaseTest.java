package oogasalad.model.engine.subComponent.behavior;

import com.google.cloud.Timestamp;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import oogasalad.model.config.GameConfig;
import oogasalad.model.config.PasswordHashingException;
import oogasalad.model.profile.Password;
import oogasalad.model.profile.PlayerData;
import oogasalad.model.profile.SessionManagement;
import oogasalad.view.scene.menu.MainMenuScene;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.framework.junit5.ApplicationTest;
import javafx.stage.Stage;
import java.awt.Dimension;
import oogasalad.model.engine.architecture.GameObject;
import oogasalad.model.engine.component.AnimationController;
import oogasalad.model.engine.component.BehaviorController;
import oogasalad.model.engine.component.Camera;
import oogasalad.model.engine.component.Collider;
import oogasalad.model.engine.component.Follower;
import oogasalad.model.engine.component.Transform;
import oogasalad.model.engine.subComponent.animation.Animation;
import oogasalad.view.scene.MainViewManager;
import oogasalad.model.engine.architecture.GameScene;
import oogasalad.model.engine.architecture.Game;
import oogasalad.model.engine.architecture.GameInfo;
import javafx.application.Platform;
import java.util.concurrent.CountDownLatch;

public abstract class BehaviorBaseTest extends ApplicationTest {

    private Game game;
    private GameScene scene1;
    private GameScene scene2;
    private GameObject obj1;
    private GameObject obj2;
    private GameObject camera;
    private MainViewManager viewManager;
    private Behavior behavior1;
    private Behavior behavior2;

    @Override
    public void start(Stage stage) throws PasswordHashingException, IOException {
        MainViewManager viewManager = MainViewManager.getInstance();
        viewManager.addViewScene(MainMenuScene.class, GameConfig.getText("defaultScene"));

        String username = "justin1";
        Password password = Password.fromPlaintext("justin1");
        String fullName = "justin";

        SessionManagement.getInstance().login((new PlayerData(username, fullName, password, Timestamp.now(), null, null, null)), false);
        SessionManagement.getInstance().tryAutoLogin();
        viewManager.switchToMainMenu();
    }

    @BeforeEach
    public void generalSetUp()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        obj1 = new GameObject("Object1");
        obj2 = new GameObject("Object2");
        Transform transform1 = obj1.addComponent(Transform.class);
        Transform transform2 = obj2.addComponent(Transform.class);
        transform1.setScaleX(100);
        transform1.setScaleY(100);
        transform2.setScaleX(100);
        transform2.setScaleY(100);
        obj1.addComponent(BehaviorController.class);
        obj1.setName("Object1");
        obj2.addComponent(BehaviorController.class);
        obj1.addComponent(Collider.class);
        obj2.addComponent(Collider.class);
        behavior1 = new Behavior("Behavior1");
        behavior2 = new Behavior("Behavior2");
        getBehaviors(obj1).add(behavior1);
        getBehaviors(obj2).add(behavior2);
        behavior1.setBehaviorController(obj1.getComponent(BehaviorController.class));
        behavior2.setBehaviorController(obj2.getComponent(BehaviorController.class));


        List<Behavior> behaviors1 = (List<Behavior>) obj1.getComponent(BehaviorController.class).getSerializedField("behaviors").getValue();
        List<Behavior> behaviors2 = (List<Behavior>) obj2.getComponent(BehaviorController.class).getSerializedField("behaviors").getValue();
        behaviors1.add(behavior1);
        behaviors2.add(behavior2);

        scene1 = new GameScene("Scene1");
        scene2 = new GameScene("Scene2");
        scene1.registerObject(obj1);
        scene1.registerObject(obj2);
        setupViewManager();
        camera = new GameObject("Camera");
        Transform cameraTransform = camera.addComponent(Transform.class);
        camera.addComponent(Camera.class);
        camera.addComponent(Follower.class).setFollowObjectName("Object1");;
        cameraTransform.setScaleX(10000);
        cameraTransform.setScaleY(10000);
        scene1.registerObject(camera);
        scene1.setMainCamera(camera.getComponent(Camera.class));
        game = new Game();
        game.setGameInfo(new GameInfo("", "", "", new Dimension(10000, 10000)));
        game.addScene(scene1);
        game.addScene(scene2);

        customSetUp();

        behavior1.setBehaviorController(obj1.getComponent(BehaviorController.class));
        behavior2.setBehaviorController(obj2.getComponent(BehaviorController.class));
        behavior1.awake();
        behavior2.awake();
    }

    private void setupViewManager() {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            viewManager = MainViewManager.getInstance();
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    @AfterEach
    public void cleanUp() {
        SessionManagement.getInstance().logout();
    }

    public abstract void customSetUp();

    protected GameObject getObj1() {
        return obj1;
    }

    protected GameObject getObj2() {
        return obj2;
    }

    protected GameObject getCamera() {
        return camera;
    }

    protected Behavior getBehavior1() {
        return behavior1;
    }

    protected Behavior getBehavior2() {
        return behavior2;
    }

    protected Game getGame() {
        return game;
    }

    protected GameScene getScene1() {
        return scene1;
    }

    protected GameScene getScene2() {
        return scene2;
    }

    protected MainViewManager getViewManager() {
        return viewManager;
    }

    protected void step() {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                game.getCurrentScene().step(1);
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void addAction(Behavior behavior, BehaviorAction<?> action) {
        getActions(behavior).add(action);
        action.setBehavior(behavior);
    }

    public void addConstraint(Behavior behavior, BehaviorConstraint<?> constraint) {
        getConstraints(behavior).add(constraint);
        constraint.setBehavior(behavior);
    }

    public void addCollidableTag(Collider collider, String tag) {
        getCollidableTags(collider).add(tag);
    }

    public void addAnimation(AnimationController controller, Animation animation) {
        getAnimations(controller).add(animation);
    }
    
    public List<Animation> getAnimations(AnimationController controller) {
        return (List<Animation>) controller.getSerializedField("animations").getValue();
    }

    public List<String> getCollidableTags(Collider collider) {
        return (List<String>) collider.getSerializedField("collidableTags").getValue();
    }

    public List<BehaviorAction<?>> getActions(Behavior behavior) {
        return (List<BehaviorAction<?>>) behavior.getSerializedField("actions").getValue();
    }

    public List<BehaviorConstraint<?>> getConstraints(Behavior behavior) {
        return (List<BehaviorConstraint<?>>) behavior.getSerializedField("constraints").getValue();
    }

    public List<Behavior> getBehaviors(GameObject obj) {
        return (List<Behavior>) obj.getComponent(BehaviorController.class).getSerializedField("behaviors").getValue();
    }

    public void removeAction(Behavior behavior, BehaviorAction<?> action) {
        getActions(behavior).remove(action);
    }

    public void removeConstraint(Behavior behavior, BehaviorConstraint<?> constraint) {
        getConstraints(behavior).remove(constraint);
    }
}

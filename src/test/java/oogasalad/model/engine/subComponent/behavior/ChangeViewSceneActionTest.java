package oogasalad.model.engine.subComponent.behavior;

import static org.junit.jupiter.api.Assertions.*;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import oogasalad.model.engine.subComponent.behavior.action.ChangeViewSceneAction;
import oogasalad.view.scene.MainViewManager;
import oogasalad.view.scene.ViewScene;

import java.util.concurrent.CountDownLatch;

public class ChangeViewSceneActionTest extends BehaviorBaseTest {

    private static final String TEST_SCENE_1 = "Scene1";
    private static final String TEST_SCENE_2 = "Scene2";
    private ChangeViewSceneAction action;

    @Override
    public void customSetUp() {
        action = new ChangeViewSceneAction();
        addAction(getBehavior1(), action);
    }

    @BeforeEach
    void registerTestScenes() {
        Platform.runLater(() -> {
            MainViewManager vm = getViewManager();

            vm.addViewScene(DummyScene.class, TEST_SCENE_1);
            vm.addViewScene(DummyScene.class, TEST_SCENE_2);
        });
    }

    @Test
    void perform_switchesSceneSuccessfully() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                getViewManager().switchTo(TEST_SCENE_1);
                getViewManager().switchTo(TEST_SCENE_2);
                assertEquals(TEST_SCENE_2, getViewManager().getCurrentSceneName());

                action.onPerform(null);
                assertEquals(TEST_SCENE_1, getViewManager().getCurrentSceneName());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    /**
     * A dummy scene for switching tests
     */
    private static class DummyScene extends ViewScene {
        DummyScene(Stage stage) {
            super(stage);
        }
    }
}
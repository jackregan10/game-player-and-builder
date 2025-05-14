package oogasalad.model.engine.subComponent.behavior.action;


import oogasalad.model.engine.architecture.GameObject;

/**
 * The SpawnObjectAction class is a behavior action that spawns a new prefabricated object in the scene.
 * It takes the name of the object inside the prefab scene to spawn as a parameter.
 * 
 * @author Christian Bepler, Hsuan-Kai Liao
 */

public class SpawnObjectAction extends SpawnObjectGeneralAction {

    @Override
    protected void perform(String parameter) {
        GameObject parent = getBehavior().getController().getParent();
        spawn(parent, parameter);
    }
}

package oogasalad.model.engine.subComponent.behavior.action;

import java.util.Set;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.component.PhysicsHandler;

import java.util.function.BiConsumer;

/**
 * VelocityXSetAction is a class that extends SetComponentValueAction and is used to set the X
 * velocity of the object.
 *
 * @author Hsuan-Kai Liao, Christian Bepler
 */
public class VelocityXSetAction extends SetComponentValueAction<Double, PhysicsHandler> {

  @Override
  protected PhysicsHandler supplyComponent() {
    return getComponent(PhysicsHandler.class);
  }


  @Override
  protected BiConsumer<PhysicsHandler, Double> provideSetter() {
    return PhysicsHandler::setVelocityX;
  }

  @Override
  protected Double defaultParameter() {
    return 0.0;
  }

  @Override
  public Set<Class<? extends GameComponent>> requiredComponents() {
    return Set.of(
        PhysicsHandler.class
    );
  }
}

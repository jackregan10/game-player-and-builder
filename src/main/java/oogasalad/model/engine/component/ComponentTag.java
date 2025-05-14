package oogasalad.model.engine.component;

/**
 * The type that specifies the order of the update of the component. The more the Component is in
 * the front, the earlier it gets updated.
 *
 * @author Hsuan-Kai Liao
 */
public enum ComponentTag {
  NONE,
  INPUT,                                             // INPUTS
  TRANSFORM, PHYSICS, COLLISION, FOLLOWER, BEHAVIOR, // LOGICS
  ANIMATION, SPRITE, TEXT, CAMERA                    // RENDERS
}

package oogasalad.model.engine.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import oogasalad.model.config.GameConfig;
import oogasalad.model.engine.architecture.GameComponent;
import oogasalad.model.engine.subComponent.animation.Animation;
import oogasalad.model.serialization.serializable.SerializableField;

/**
 * A component that manages the animation of a GameObject, including the list of animation names,
 * the current animation, and the time to switch between animations.
 * 
 * @author Christian Bepler
 */

public class AnimationController extends GameComponent {
    @SerializableField
    private List<Animation> animations;
    @SerializableField
    private String initialAnimation;

    private Map<String, Animation> animationMap;
    private Animation currentAnimation;
    private Double switchTime;
    private Double countDown;
    private SpriteRenderer spriteRenderer;

    @Override
    public ComponentTag componentTag() {
        return ComponentTag.ANIMATION;
    }

    @Override
    public void awake() {
        super.awake();
        if (animations == null) {
            animations = new ArrayList<>();
        }
        animationMap = animations.stream()
                .collect(Collectors.toMap(Animation::getName, animation -> animation));
        spriteRenderer = getParent().getComponent(SpriteRenderer.class);
        if (initialAnimation != null) {
            setCurrentAnimation(initialAnimation);
        }
    }

    /**
     * Sets the current animation to the specified animation name.
     *
     * @param animationName the name of the animation to set
     * @throws IllegalArgumentException if the animation name is not found
     */
    public void setCurrentAnimation(String animationName) {
        currentAnimation = animationMap.get(animationName);
        if (currentAnimation == null) {
            throw new IllegalArgumentException(GameConfig.getText("animationNotFound", animationName));
        }
        switchTime = currentAnimation.getAnimationLength() / currentAnimation.getSize();
        countDown = switchTime;
        currentAnimation.reset();
        spriteRenderer.setImagePath(currentAnimation.getNextPath());
    }

    /**
     * Cancels the current animation, stopping any further updates to the sprite renderer.
     */
    public void cancelAnimation() {
        currentAnimation = null;
    }

    @Override
    public void update(double deltaTime) {
        if (currentAnimation != null) {
            countDown -= deltaTime;
            if (countDown <= 0) {
                spriteRenderer.setImagePath(currentAnimation.getNextPath());
                countDown = switchTime;
            }
        }
    }

    /**
     * Returns the current animation name.
     *
     * @return the name of the current animation
     */
    public String getCurrentAnimation() {
        if (currentAnimation == null) {
            return "";
        }
        return currentAnimation.getName();
    }

    /**
     * Get the list of animations.
     * 
     * @return the list of animations
     */
    public List<Animation> getAnimations() {
        if (animations == null) {
            animations = new ArrayList<>();
        }
        return animations;
    }
}

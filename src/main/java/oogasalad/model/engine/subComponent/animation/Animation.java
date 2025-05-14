package oogasalad.model.engine.subComponent.animation;

import java.util.ArrayList;
import java.util.List;

import oogasalad.model.resource.ResourcePath;
import oogasalad.model.serialization.serializable.SerializableField;
import oogasalad.model.serialization.serializable.Serializable;

/**
 * The animation component defines a single animation for a GameObject, including the file paths
 * for the animation frames and the length of the animation.
 * 
 * @author Christian Bepler
 */

public class Animation implements Serializable {
    @SerializableField
    private String name = "";
    @SerializableField
    private List<ResourcePath> filePaths = new ArrayList<>();
    @SerializableField
    private Double animationLength = 0.0;
    @SerializableField
    private Boolean loop = false;

    private Integer pathIdx;

    /**
     * Returns the length of the animation in seconds.
     * @return the length of the animation
     */
    public Double getAnimationLength() {
        return animationLength;
    }

    /**
     * Returns the name of the animation.
     * @return the name of the animation
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the number of frames in the animation.
     * @return the number of frames
     */
    public Integer getSize() {
        return filePaths.size();
    }

    /**
     * Returns whether the animation should loop.
     * @return true if the animation should loop, false otherwise
     */
    public Boolean getLoop() {
        return loop;
    }

    /**
     * Resets the animation to the first frame.
     * This method is typically called when the animation is first started or when it needs to be
     * restarted.
     */
    public void reset() {
        pathIdx = 0;
    }

    /**
     * Moves to the next frame in the animation and returns the file path for that frame.
     * @return the file path for the next frame
     */
    public ResourcePath getNextPath() {
        ResourcePath path = filePaths.get(pathIdx);
        pathIdx++;
        if (pathIdx >= filePaths.size()) {
            if(loop) {
                pathIdx = 0;
            } else {
                pathIdx = filePaths.size() - 1;
            }
        }
        return path;
    }

    /**
     * Returns the list of file paths for the animation frames.
     * @return the list of file paths
     */
    public List<ResourcePath> getFilePaths() {
        return filePaths;
    }

    /**
     * Sets the list of file paths for the animation frames.
     * @param filePaths the list of file paths
     */
    public void setFilePaths(List<ResourcePath> filePaths) {
        this.filePaths = filePaths;
    }

    /**
     * Sets the length of the animation.
     * @param animationLength the length of the animation
     */
    public void setAnimationLength(Double animationLength) {
        this.animationLength = animationLength;
    }

    /**
     * Sets the name of the animation.
     * @param name the name of the animation
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets whether the animation should loop.
     * @param loop true if the animation should loop, false otherwise
     */
    public void setLoop(Boolean loop) {
        this.loop = loop;
    }
}

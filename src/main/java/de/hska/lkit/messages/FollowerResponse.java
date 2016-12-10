package de.hska.lkit.messages;

/**
 * Created by bob on 10.12.2016.
 */
public class FollowerResponse {

    private boolean isFollower;

    public FollowerResponse() {
    }

    public FollowerResponse(boolean isFollower) {
        this.isFollower = isFollower;
    }

    public boolean getIsFollower() {
        return isFollower;
    }

    public void setIsFollower(boolean follower) {
        isFollower = follower;
    }
}

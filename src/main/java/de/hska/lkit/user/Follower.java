package de.hska.lkit.user;

/**
 *
 * @author essigt
 *
 */
public class Follower {

    private String username;
    private boolean following;

    public Follower() {

    }

    /**
     * @param username
     * @param following
     */
    public Follower(String username, boolean following) {
        this.username = username;
        this.following = following;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

}

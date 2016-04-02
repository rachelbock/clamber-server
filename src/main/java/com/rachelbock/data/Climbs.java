package com.rachelbock.data;

/**
 * Class to hold Climb data
 */
public class Climbs {

    protected int id;
    protected int gymRating;
    protected int userRating;
    protected String tapeColor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGymRating() {
        return gymRating;
    }

    public void setGymRating(int gymRating) {
        this.gymRating = gymRating;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public String getTapeColor() {
        return tapeColor;
    }

    public void setTapeColor(String tapeColor) {
        this.tapeColor = tapeColor;
    }
}

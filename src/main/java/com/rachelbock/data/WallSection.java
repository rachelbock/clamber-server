package com.rachelbock.data;

import java.util.Date;

/**
 * Class to hold WallSection data
 */
public class WallSection {

    protected int id;
    protected String name;
    protected Date dateLastUpdated;
    protected boolean isTopOut;
    protected int mainWallId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateLastUpdated() {
        return dateLastUpdated;
    }

    public void setDateLastUpdated(Date dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }

    public boolean isTopOut() {
        return isTopOut;
    }

    public void setTopOut(boolean topOut) {
        isTopOut = topOut;
    }

    public int getMainWallId() {
        return mainWallId;
    }

    public void setMainWallId(int mainWallId) {
        this.mainWallId = mainWallId;
    }
}

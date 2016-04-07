package com.rachelbock.data;

/**
 * Created by rage on 4/4/16.
 */
public class Project {

    protected int projectId;
    protected int climbId;
    protected String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getClimbId() {
        return climbId;
    }

    public void setClimbId(int climbId) {
        this.climbId = climbId;
    }

}

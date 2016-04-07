package com.rachelbock.resources;

import com.rachelbock.data.Climb;
import com.rachelbock.data.Project;
import com.rachelbock.data.User;

import javax.swing.plaf.nimbus.State;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Class to set up the database connection for projects
 */

@Path("/projects/{user_name}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {

    public Connection getConnection() throws SQLException {
        Properties connectionProps = new Properties();
        connectionProps.put("user", "root");
        connectionProps.put("password", "root");

        String dbms = "mysql";
        String serverName = "localhost";
        int portNumber = 3306;
        String dbName = "clamber";

        String connectionString = "jdbc:" + dbms + "://" + serverName + ":" + portNumber + "/" + dbName;
        Connection conn = DriverManager.getConnection(connectionString, connectionProps);
        return conn;
    }

    @GET
    public ArrayList<Climb> getProjectsForUser (@PathParam("user_name") String userName) {

        ArrayList<Climb> climbs = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery("SELECT * FROM projects INNER JOIN climbs ON projects.climb_id = climbs.climb_id LEFT OUTER JOIN completed_climbs ON completed_climbs.climb_id = climbs.climb_id AND completed_climbs.user_name = projects.user_name WHERE projects.user_name = '" + userName + "'")) {

            while (resultSet.next()) {
                Climb climb = new Climb();
                climb.setId(resultSet.getInt("climb_id"));
                climb.setGymRating(resultSet.getInt("gym_rating"));
                climb.setUserRating(resultSet.getInt("user_rating"));
                climb.setTapeColor(resultSet.getString("tape_color"));
                climb.setWallId(resultSet.getInt("wall_id"));
                climb.setProject(true);
                if (resultSet.getObject("completed_id") != null) {
                    climb.setCompleted(true);
                }
                else {
                    climb.setCompleted(false);
                }
                climb.setType(resultSet.getString("climb_type"));
                climbs.add(climb);

            }
        } catch (SQLException e1) {
            e1.printStackTrace();
            throw new InternalServerErrorException(e1);
        }

        return climbs;
    }

    @POST
    public Project addProjectToDatabase(NewProjectRequest request) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()){

            stmt.execute("INSERT INTO projects (climb_id, user_name) VALUES " +
            "(" + request.getClimbId() + ", '" + request.getUsername() + "')");

            Project project = new Project();
            project.setUserName(request.getUsername());
            project.setClimbId(request.getClimbId());

            return project;

        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        throw new InternalServerErrorException("Could not create new project for user " + request.getUsername());
    }


/**
 * Defines how we expect the json in the body to look
  */
public static class NewProjectRequest {
    protected String username;
    protected int climbId;
    protected int projectId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getClimbId() {
        return climbId;
    }

    public void setClimbId(int climbId) {
        this.climbId = climbId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}

}

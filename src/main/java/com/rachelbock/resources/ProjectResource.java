package com.rachelbock.resources;

import com.rachelbock.data.Climb;
import com.rachelbock.data.Project;
import com.rachelbock.db.ConnectionPool;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Class to set up the database connection for projects
 */

@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {


    public static final String GET_PROJECT_BY_USER_QUERY = "SELECT * FROM projects \n" +
            "INNER JOIN climbs ON projects.climb_id = climbs.climb_id \n" +
            "LEFT OUTER JOIN completed_climbs ON completed_climbs.climb_id = climbs.climb_id \n" +
            "AND completed_climbs.user_name = projects.user_name \n" +
            "WHERE projects.user_name = ? AND completed_climbs.user_name IS NULL \n" +
            "ORDER BY projects.date_long DESC";

    /**
     * Retrieves climb data for all climbs that the given user has marked as being a project. Returns an empty list if
     * that user has no projects, or if the user does not exist. It also will include whether or not the project also
     * exists as a completed climb for the user.
     *
     * @param userName - used in the query to pull projects by user
     * @return - ArrayList of Climbs from the Clamber Database
     */
    @Path("{user_name}")
    @GET
    public ArrayList<Climb> getProjectsForUser(@PathParam("user_name") String userName) {

        ArrayList<Climb> climbs = new ArrayList<>();
        try (Connection conn = ConnectionPool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(GET_PROJECT_BY_USER_QUERY);
            stmt.setString(1, userName);

            ResultSet resultSet = stmt.executeQuery();
//
            while (resultSet.next()) {
                Climb climb = new Climb();
                climb.setClimbId(resultSet.getInt("projects.climb_id"));
                climb.setGymRating(resultSet.getInt("gym_rating"));
                climb.setUserRating(resultSet.getInt("user_rating"));
                climb.setTapeColor(resultSet.getString("tape_color"));
                climb.setWallId(resultSet.getInt("wall_id"));
                climb.setProject(true);
                if (resultSet.getString("completed_climbs.user_name") != null) {
                    climb.setCompleted(true);
                } else {
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

    public static final String ADD_PROJECT_QUERY = "INSERT INTO projects (user_name, climb_id, date_long) \n" +
            "VALUES (?, ?, ?)";

    /**
     * Method to post a project to the Clamber Database. If it is unable to create a project with the provided information
     * it throws an exception
     *
     * @param request - json for project
     * @return - returns a Project which is not used.
     */
    @POST
    public Project addProjectToDatabase(NewProjectRequest request) {
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(ADD_PROJECT_QUERY)) {
            stmt.setString(1, request.getUsername());
            stmt.setInt(2, request.getClimbId());
            stmt.setLong(3, request.getDate());
            stmt.execute();

            Project project = new Project();
            project.setUserName(request.getUsername());
            project.setClimbId(request.getClimbId());
            project.setDate(request.getDate());

            return project;

        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        throw new InternalServerErrorException("Could not create new project for user " + request.getUsername());
    }

    public static final String REMOVE_QUERY = ("DELETE FROM projects WHERE user_name = ? AND climb_id = ?");

    /**
     * Method to delete a project from the database. It returns a boolean indicating whether or not the project
     * was successfully created.
     *
     * @param username - username used in query
     * @param climb_id - climb_id used in query
     * @return - boolean indicating success.
     */
    @Path("{username}/climbs/{climb_id}")
    @DELETE
    public boolean removeProjectFromDatabase(@PathParam("username") String username, @PathParam("climb_id") int climb_id) {
        boolean wasRemoved = false;

        try (Connection conn = ConnectionPool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(REMOVE_QUERY);
            stmt.setString(1, username);
            stmt.setInt(2, climb_id);

            stmt.execute();

            wasRemoved = true;

        } catch (SQLException e) {
            e.printStackTrace();
            wasRemoved = false;
        }

        return wasRemoved;
    }


    /**
     * Defines how we expect the json in the body to look
     */
    public static class NewProjectRequest {
        protected String username;
        protected int climbId;
        protected long date;


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

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }
    }

}

package com.rachelbock.resources;

import com.rachelbock.data.Climb;
import com.rachelbock.db.ConnectionPool;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class to handle database connection for completed climbs
 */

@Path("/completed")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompletedResource {

    public static final String ADD_COMPLETED_CLIMB_QUERY = "INSERT INTO completed_climbs (user_name, climb_id, date_long) \n" +
            "VALUES (?, ?, ?)";

    /**
     * Method to post a completed climb to the Clamber Database. It will return a Boolean indicating whether
     * the climb was successfully created.
     * @param request - json for project
     * @return - boolean indicating success
     */
    @POST
    public boolean addCompletedClimbToDatabase(NewCompletedClimbRequest request){
        boolean wasRemoved = false;
        try(Connection conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(ADD_COMPLETED_CLIMB_QUERY)){
            stmt.setString(1, request.getUsername());
            stmt.setInt(2, request.getClimbId());
            stmt.setLong(3, request.getDate());
            stmt.execute();
            wasRemoved = true;


        } catch (SQLException e) {
            e.printStackTrace();
            wasRemoved = false;
        }
        return wasRemoved;
    }

    public static final String REMOVE_QUERY = ("DELETE FROM completed_climbs WHERE user_name = ? AND climb_id = ?");

    /**
     * Method to delete a completed climb from the database. It returns a boolean indicating whether or not the
     * climb was successfully created.
     * @param username - username used in query
     * @param climb_id - climb id used in query
     * @return - boolean indicating success
     */
    @Path("{username}/climbs/{climb_id}")
    @DELETE
    public boolean removeCompletedClimb(@PathParam("username") String username, @PathParam("climb_id") int climb_id){
        boolean wasRemoved = false;

        try(Connection conn = ConnectionPool.getConnection()){
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

    public static final String HISTORY_QUERY = ("SELECT * FROM climbs\n" +
            "INNER JOIN completed_climbs ON completed_climbs.climb_id = climbs.climb_id\n" +
            "AND completed_climbs.user_name = ?\n" +
            "LEFT OUTER JOIN projects ON projects.climb_id = climbs.climb_id\n" +
            "AND projects.user_name = ?\n" +
            "ORDER BY completed_climbs.date_long DESC");
    @Path("{username}")
    @GET
    public List<Climb> getCompletedHistory(@PathParam("username") String username){
        List<Climb> climbs = new ArrayList<>();

        try(Connection conn = ConnectionPool.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(HISTORY_QUERY);
            stmt.setString(1, username);
            stmt.setString(2, username);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()){
                Climb climb = new Climb();
                climb.setClimbId(resultSet.getInt("climb_id"));
                climb.setGymRating(resultSet.getInt("gym_rating"));
                climb.setWallId(resultSet.getInt("wall_id"));
                climb.setTapeColor(resultSet.getString("tape_color"));
                climb.setType(resultSet.getString("climb_type"));
                climb.setCompleted(true);
                if (resultSet.getBoolean("removed")) {
                    climb.setRemoved(true);
                }
                else {
                    climb.setRemoved(false);
                }

                if (resultSet.getString("projects.user_name") != null){
                    climb.setProject(true);
                }
                else {
                    climb.setProject(false);
                }
                climbs.add(climb);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }

        return climbs;
    }


    /**
     * Defines how we expect the json in the body to look
     */
    public static class NewCompletedClimbRequest {
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

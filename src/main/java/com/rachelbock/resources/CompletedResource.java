package com.rachelbock.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.Properties;

/**
 * Class to handle database connection for completed climbs
 */

@Path("/completed")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompletedResource {

    /**
    * Gets a Connection to the Clamber Database
    * @return - connection
    * @throws SQLException
    */
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

    /**
     * Method to post a completed climb to the Clamber Database. It will return a Boolean indicating whether
     * the climb was successfully created.
     * @param request - json for project
     * @return - boolean indicating success
     */
    @POST
    public boolean addCompletedClimbToDatabase(NewCompletedClimbRequest request){
        boolean wasRemoved = false;
        try(Connection conn = getConnection();
            Statement stmt = conn.createStatement()){

            stmt.execute("INSERT INTO completed_climbs (user_name, climb_id) VALUES ( '" + request.getUsername() + "', " + request.getClimbId() +")");
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

        try(Connection conn = getConnection()){
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
    public static class NewCompletedClimbRequest {
        protected String username;
        protected int climbId;


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

    }

}

package com.rachelbock.resources;

import com.rachelbock.data.Climb;
import com.rachelbock.data.Wall;
import com.rachelbock.data.WallSection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by rage on 3/13/16.
 */
@Path("/user/{username}/walls/{id}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WallsResource {

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
    @Path("wall_sections")
    public List<WallSection> getWallById(@PathParam("id") int id) {
        ArrayList<WallSection> wallSections = new ArrayList<>();
        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM wall_sections WHERE wall_segment = " + id);

            while (resultSet.next()){
                WallSection wallSection = new WallSection();
                wallSection.setId(resultSet.getInt("id"));
                wallSection.setTopOut(resultSet.getBoolean("top_out"));
                wallSection.setDateLastUpdated(resultSet.getDate("date_last_updated"));
                wallSection.setName(resultSet.getString("name"));
                wallSections.add(wallSection);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }
        return wallSections;
    }

    protected static final String WALL_SECTIONS_BY_ID_QUERY = "SELECT * FROM climbs \n" +
            "LEFT OUTER JOIN projects " +
            "   ON projects.climb_id = climbs.climb_id AND projects.user_name = ?\n" +
            "LEFT OUTER JOIN completed_climbs " +
            "   ON completed_climbs.climb_id = climbs.climb_id AND completed_climbs.user_name = ?\n" +
            "WHERE climbs.wall_id = ?";

    @GET
    @Path("wall_sections/{wall_id}/climbs")
    public List<Climb> getWallSectionById(@PathParam("wall_id") int wall_id, @PathParam("username") String username){
        List<Climb> climbs = new ArrayList<>();
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(WALL_SECTIONS_BY_ID_QUERY);
            stmt.setString(1, username);
            stmt.setString(2, username);
            stmt.setInt(3, wall_id);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Climb climb = new Climb();
                climb.setId(resultSet.getInt("climb_id"));
                climb.setGymRating(resultSet.getInt("gym_rating"));
                climb.setTapeColor(resultSet.getString("tape_color"));
                climb.setType(resultSet.getString("climb_type"));
                if (resultSet.getObject("project_id") != null){
                    climb.setProject(true);
                }
                else {
                    climb.setProject(false);
                }
                if (resultSet.getObject("completed_id") != null) {
                    climb.setCompleted(true);
                }
                else {
                    climb.setCompleted(false);
                }
                climbs.add(climb);
            }


        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }
        return climbs;
    }
}

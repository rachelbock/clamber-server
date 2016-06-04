package com.rachelbock.resources;

import com.rachelbock.data.Climb;
import com.rachelbock.data.WallSection;
import com.rachelbock.db.ConnectionPool;
import org.omg.CORBA.INTERNAL;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles networking for Clamber Admin App
 */
@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminResource {


    public static final String CHECK_ADMIN_USER_QUERY = "Select * FROM admin \n" +
            "WHERE username = ?";

    /**
     * Checks the Clamber database for the username provided. Returns true if the username
     * exists in the database, false if not. Used for authentication in Clamber admin app.
     */
    @GET
    @Path("/admin_user/{username}")
    public boolean checkAdminCredentials(@PathParam("username") String username) {
        try (Connection conn = ConnectionPool.getConnection()) {

            PreparedStatement stmt = conn.prepareStatement(CHECK_ADMIN_USER_QUERY);
            stmt.setString(1, username);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static final String GET_WALL_SECTIONS_QUERY = "SELECT * FROM wall_sections WHERE wall_segment = ?";


    /**
     * Retrieves the wall sections on a given mail wall id. Used in Clamber Admin app to choose the
     * appropriate wall section to update.
     */
    @GET
    @Path("/wall/{wall_id}/wall_sections")
    public List<WallSection> getWallSection(@PathParam("wall_id") int id) {
        ArrayList<WallSection> wallSections = new ArrayList<>();
        try (Connection conn = ConnectionPool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(GET_WALL_SECTIONS_QUERY);
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
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

    public static final String REMOVE_CLIMBS_FROM_WALL_QUERY = "UPDATE climbs SET climbs.removed = 1 WHERE climbs.wall_id = ?";


    /**
     * Removes all of the climbs on a given wall section by updating the removed column to 1.
     *
     * @param wallId        - not used
     * @param wallSectionId - the wall section to remove all climbs from.
     * @return - true if successful.
     */
    @POST
    @Path("/wall/{wall_id}/wall_sections/{wall_section_id}/remove_climbs")
    public boolean removeClimbsByWallSection(@PathParam("wall_id") int wallId, @PathParam("wall_section_id") int wallSectionId) {

        try (Connection conn = ConnectionPool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(REMOVE_CLIMBS_FROM_WALL_QUERY);
            stmt.setInt(1, wallSectionId);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }

        return true;
    }

    public static final String REMOVE_SINGLE_CLIMB_QUERY = "UPDATE climbs SET climbs.removed = 1 \n" +
            "WHERE climbs.climb_id = ?";

    /**
     * Removes a single climb given climb Id from the database.
     *
     * @param wall_id       - not used
     * @param wallSectionId - the wall section that contains the climb.
     * @param climbId       - the climbId of the specific climb to remove
     * @return true if successful
     */
    @POST
    @Path("/wall/{wall_id}/wall_sections/{wall_section_id}/climbs/{climb_id}/remove")
    public boolean removeClimb(@PathParam("wall_id") int wall_id, @PathParam("wall_section_id") int wallSectionId, @PathParam("climb_id") int climbId) {

        try (Connection conn = ConnectionPool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(REMOVE_SINGLE_CLIMB_QUERY);
            stmt.setInt(1, climbId);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }
        return true;
    }


    public static final String GET_CLIMBS_BY_WALL_QUERY = "SELECT * FROM climbs WHERE wall_id = ? \n" +
            "AND removed = 0";

    /**
     * Retrieves all of the climbs and their data on a given wall section.
     *
     * @param wallId        - not used
     * @param wallSectionId - wall section to retrieve climbs from
     * @return - list of climbs - empty if there are no climbs on the wall section
     */
    @GET
    @Path("/wall/{wall_id}/wall_sections/{wall_section_id}/climbs")
    public List<Climb> getClimbsByWallSection(@PathParam("wall_id") int wallId, @PathParam("wall_section_id") int wallSectionId) {
        List<Climb> climbs = new ArrayList<>();
        try (Connection conn = ConnectionPool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(GET_CLIMBS_BY_WALL_QUERY);
            stmt.setInt(1, wallSectionId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Climb climb = new Climb();
                climb.setClimbId(resultSet.getInt("climbs.climb_id"));
                climb.setGymRating(resultSet.getInt("gym_rating"));
                climb.setTapeColor(resultSet.getString("tape_color"));
                climb.setType(resultSet.getString("climb_type"));
                climbs.add(climb);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }
        return climbs;
    }


    public static final String ADD_NEW_CLIMB_QUERY = "INSERT INTO climbs \n" +
            "(gym_rating, wall_id, tape_color, climb_type, removed) VALUES \n" +
            "(?, ?, ?, ?, 0)";


    /**
     * Adds a climb to the database with information provided in the Clamber Admin app.
     *
     * @param wallId        - not used
     * @param wallSectionId - the wall section for the climb
     * @param request       - information on the climb to add to the database
     * @return true if successsful.
     */
    @POST
    @Path("/wall/{wall_id}/wall_sections/{wall_section_id}/add_climb")
    public boolean addNewClimb(@PathParam("wall_id") int wallId, @PathParam("wall_section_id") int wallSectionId, NewClimbRequest request) {
        try (Connection conn = ConnectionPool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(ADD_NEW_CLIMB_QUERY);
            stmt.setInt(1, request.getRating());
            stmt.setInt(2, request.getWallId());
            stmt.setString(3, request.getColor());
            stmt.setString(4, request.getType());

            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }
        return true;
    }

    /**
     * Defines the body of the New Climb Request from the Clamber Admin app.
     */
    public static class NewClimbRequest {
        protected int rating;
        protected String type;
        protected String color;
        protected int wallId;

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public int getWallId() {
            return wallId;
        }

        public void setWallId(int wallId) {
            this.wallId = wallId;
        }
    }

}

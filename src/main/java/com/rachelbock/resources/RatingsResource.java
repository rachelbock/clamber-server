package com.rachelbock.resources;

import com.rachelbock.db.ConnectionPool;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Resource for adding and retrieving ratings in the database.
 */

@Path("/ratings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RatingsResource {

    public static final int NO_DATA = -2;


    public static final String AVERAGE_RATING_QUERY = "SELECT AVG (ratings.user_rating) as avg_rating FROM ratings \n" +
            "INNER JOIN climbs ON ratings.climb_id = climbs.climb_id WHERE ratings.climb_id = ?";

    /**
     * Retrieves the average of all of the ratings for a particular climb.
     * @param climbId - the climb that is requested.
     * @return - the average of ratings for a climb or -2 if there is no data.
     */
    @Path("climbs/{climb_id}")
    @GET
    public double getUserAverageRatingForClimb(@PathParam("climb_id") int climbId){
        double rating = NO_DATA;

        try(Connection conn = ConnectionPool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(AVERAGE_RATING_QUERY);
            stmt.setInt(1, climbId);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()){
                if (resultSet.getObject("avg_rating") != null) {
                    rating = resultSet.getDouble("avg_rating");
                }
            }



        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }

        return rating;
    }

    public static final String CLIMB_RATING_BY_USER_QUERY = "SELECT * FROM ratings INNER JOIN climbs \n" +
            "ON ratings.climb_id = climbs.climb_id WHERE ratings.climb_id = ? AND ratings.user_name = ?";

    /**
     * Retrieves the rating for a climb if the user has rated it.
     * @param climbId - the id for the climb that has been rated.
     * @param username - the user
     * @return - if the user has rated the climb it returns the rating. If not, it returns -2.
     */
    @Path("climbs/{climb_id}/user/{username}")
    @GET
    public int getRatingForClimbByUser(@PathParam("climb_id") int climbId, @PathParam("username") String username){
        int rating = NO_DATA;

        try(Connection conn = ConnectionPool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(CLIMB_RATING_BY_USER_QUERY);
            stmt.setInt(1, climbId);
            stmt.setString(2, username);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()){
                rating = resultSet.getInt("user_rating");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }

        return rating;
    }

    public static final String ADD_RATING_QUERY = "INSERT INTO ratings (climb_id, user_name, user_rating) \n" +
            "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE user_rating = ?";

    /**
     * Adds a rating to the ratings table of the database.
     * @param request - the body of the post. contains climb id, user name and rating
     * @return - the user's rating.
     */
    @POST
    public int addUserRating(UserRatingRequest request){
        int rating = NO_DATA;

        try(Connection conn = ConnectionPool.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(ADD_RATING_QUERY);
            stmt.setInt(1, request.getClimbId());
            stmt.setString(2, request.getUsername());
            stmt.setInt(3, request.getRating());
            stmt.setInt(4, request.getRating());
            stmt.execute();

            rating = request.getRating();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }

        return rating;
    }


    /**
     * Defines how the JSON in the body will look
     */
    public static class UserRatingRequest {
        protected String username;
        protected int climbId;
        protected int rating;

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

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }
    }

}


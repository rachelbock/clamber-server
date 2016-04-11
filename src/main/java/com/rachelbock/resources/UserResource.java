package com.rachelbock.resources;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.rachelbock.data.Climb;
import com.rachelbock.data.User;
import com.rachelbock.db.ConnectionPool;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class to set up the database connection for users.
 */
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {


    /**
     * Retrieves User information for a given username. If no User exists with the username a NotFound
     * Exception is thrown.
     * @param userName - used in query to match user by username
     * @return - if there is a matching User, the User is returned.
     */
    @GET
    @Path("/{user_name}")
    public User getUserByUserName(@PathParam("user_name") String userName) {


        try (Connection conn = ConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery("SELECT * FROM user_information WHERE user_name ='" + userName + "'")) {

            if (resultSet.next()) {
                User user = new User();
                user.setUserName(userName);
                user.setName(resultSet.getString("name"));
                user.setHeight(resultSet.getInt("height"));
                user.setSkillLevel(resultSet.getInt("skill_level"));

                return user;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new BadRequestException("Could not find user with username " + userName );
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        throw new NotFoundException("Could not find user with username " + userName);
    }


    /**
     * Method to add a new User to the Clamber Database. If it is unable to add the User, it throws an
     * exception.
     * @param request - json for the User
     * @return - if successful it will return the User.
     */
    @POST
    public User addUserToDatabase(NewUserRequest request) {
        try (Connection conn = ConnectionPool.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("INSERT INTO user_information (user_name, height, skill_level) VALUES " +
                    "('" + request.getUsername() + "', " + request.getHeight() + ", " + request.getSkill() + ")");
            return getUserByUserName(request.getUsername());

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new BadRequestException("Username already exists");
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        throw new InternalServerErrorException("Could not create new user " + request.getUsername());
    }


    public static final String RECOMMENDATIONS_QUERY = "SELECT * FROM climbs\n" +
            "INNER JOIN user_information\n" +
            "ON climbs.gym_rating = user_information.skill_level\n" +
            "OR climbs.gym_rating = user_information.skill_level +1\n" +
            "OR climbs.gym_rating = user_information.skill_level -1\n" +
            "LEFT OUTER JOIN projects\n" +
            "ON projects.climb_id = climbs.climb_id AND projects.user_name = ?\n" +
            "LEFT OUTER JOIN completed_climbs\n" +
            "ON completed_climbs.climb_id = climbs.climb_id AND completed_climbs.user_name = ?\n" +
            "WHERE user_information.user_name = ?";

    /**
     * Method to get recommendations for a user based on the user's skill level.
     * @param username - username for query
     * @return - a list of climbs that meet the criteria for recommendations.
     */
    @GET
    @Path("/{username}/recommendations")
    public List<Climb> getRecommendationsByUser(@PathParam("username") String username) {

        List<Climb> climbs = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(RECOMMENDATIONS_QUERY);
            stmt.setString(1, username);
            stmt.setString(2, username);
            stmt.setString(3, username);


            ResultSet resultSet = stmt.executeQuery();

            while(resultSet.next()){
                Climb climb = new Climb();
                climb.setClimbId(resultSet.getInt("climb_id"));
                climb.setGymRating(resultSet.getInt("gym_rating"));
                climb.setUserRating(resultSet.getInt("user_rating"));
                climb.setTapeColor(resultSet.getString("tape_color"));
                climb.setWallId(resultSet.getInt("wall_id"));
                climb.setType(resultSet.getString("climb_type"));
                if (resultSet.getString("projects.user_name") != null){
                    climb.setProject(true);
                }
                else {
                    climb.setProject(false);
                }
                if (resultSet.getString("completed_climbs.user_name") != null) {
                    climb.setCompleted(true);
                } else {
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

    /**
     * Defines how we expect the json in the body to look
     */
    public static class NewUserRequest {
        protected String username;
        protected int height;
        protected int skill;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getSkill() {
            return skill;
        }

        public void setSkill(int skill) {
            this.skill = skill;
        }
    }

}

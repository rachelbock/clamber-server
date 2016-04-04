package com.rachelbock.resources;

import com.rachelbock.data.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.Properties;

/**
 * Created by rage on 3/18/16.
 */
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

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
    @Path("/{user_name}")
    public User getUserByUserName(@PathParam("user_name") String userName) {


        try (Connection conn = getConnection();
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
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        throw new NotFoundException("Could not find user with username " + userName);
    }


    @POST
    public User addUserToDatabase(NewUserRequest request) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()){

            stmt.execute("INSERT INTO user_information (user_name, height, skill_level) VALUES " +
                    "('" + request.getUsername() + "', " + request.getHeight() + ", " + request.getSkill() + ")");
            return getUserByUserName(request.getUsername());

        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        throw new InternalServerErrorException("Could not create new user " + request.getUsername());
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

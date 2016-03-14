package com.rachelbock.resources;

import com.rachelbock.data.Wall;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.Properties;

/**
 * Created by rage on 3/13/16.
 */
@Path("/walls/{id}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WallsResource {


    @GET
    public Wall getWallById(@PathParam("id") int id) {
        /*
         * Example code to demonstrate simple communication with the database. In real code you would generally
         * not re-create the database connection every time, but for ease of use getting started you can ignore
         * that.
         */
        Properties connectionProps = new Properties();
        connectionProps.put("user", "root");
        connectionProps.put("password", "root");

        String dbms = "mysql";
        String serverName = "localhost";
        int portNumber = 3306;
        String dbName = "clamber";

        String connectionString = "jdbc:" + dbms + "://" + serverName + ":" + portNumber + "/" + dbName;
        // "jdbc:mysql://localhost:3196/clamber";

        try (Connection conn = DriverManager.getConnection(connectionString, connectionProps)) {

            try (Statement stmt = conn.createStatement();
                 ResultSet resultSet = stmt.executeQuery("SELECT * FROM walls WHERE id=" + id)) {

                if (resultSet.next()) {
                    Wall wall = new Wall();
                    wall.setName(resultSet.getString("name"));
                    wall.setId(resultSet.getInt("id"));
                    wall.setLastUpdated(resultSet.getDate("date_last_updated").getTime());

                    return wall;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new NotFoundException("Could not find wall with id " + id);
    }
}

package com.rachelbock.resources;

import com.rachelbock.data.Wall;
import com.rachelbock.data.WallSection;

import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaTray;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.Properties;

/**
 * Created by rage on 3/18/16.
 */

@Path("/wall_section/{id}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class WallSectionResource {

    @GET
    public WallSection getWallSectionById(@PathParam("id") int id) {

        Properties connectionProps = new Properties();
        connectionProps.put("user", "root");
        connectionProps.put("password", "root");

        String dbms = "mysql";
        String serverName = "localhost";
        int portNumber = 3306;
        String dbName = "clamber";

        String connectionString = "jdbc:" + dbms + "://" + serverName + ":" + portNumber + "/" + dbName;


        try (Connection conn = DriverManager.getConnection(connectionString, connectionProps)) {

            try (Statement stmt = conn.createStatement();
                 ResultSet resultSet = stmt.executeQuery("SELECT * FROM wall_sections WHERE id=" + id)) {

                if (resultSet.next()) {
                    WallSection wallSection = new WallSection();
                    wallSection.setName(resultSet.getString("name"));
                    wallSection.setId(resultSet.getInt("id"));
                    wallSection.setDateLastUpdated(resultSet.getDate("date_last_updated"));
                    wallSection.setTopOut(resultSet.getBoolean("top_out"));

                    return wallSection;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new NotFoundException("Could not find wall section with id " + id);
    }

}

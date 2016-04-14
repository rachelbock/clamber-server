package com.rachelbock.resources;

import com.rachelbock.data.Comment;
import com.rachelbock.db.ConnectionPool;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;

import java.util.List;

/**
 * Class to set up the database connection for comments
 */
@Path("/climbs/{climb_id}/comments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommentsResource {

    public static final String GET_COMMENTS_QUERY = "SELECT * FROM comments INNER JOIN climbs \n" +
            "ON comments.climb_id = climbs.climb_id \n" +
            "WHERE climbs.climb_id = ? ORDER BY date_text DESC";


    /**
     * Retrieves comments based on climb id. Returns an empty list if there are no comments for that climb.
     * @param climb_id - the id of the climb that the comments are for.
     * @return - ArrayList of comments
     */
    @GET
    public List<Comment> getCommentsForClimb(@PathParam("climb_id") int climb_id){
        List<Comment> comments = new ArrayList<>();

        try(Connection conn = ConnectionPool.getConnection()){

            PreparedStatement stmt = conn.prepareStatement(GET_COMMENTS_QUERY);
            stmt.setInt(1, climb_id);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()){
                Comment comment = new Comment();
                comment.setClimbId(climb_id);
                comment.setComment(resultSet.getString("comment_text"));
                comment.setDate(resultSet.getLong("date_text"));
                comment.setUserName(resultSet.getString("user_name"));

                comments.add(comment);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return comments;
    }

    public static final String ADD_CLIMB_QUERY = "INSERT INTO comments (comment_text, user_name, date_text, climb_id) \n" +
            "VALUES (?, ?, ?, ?)";

    /**
     * Method to post a comment to the database using the name of the user who submitted the comment and the
     * id of the climb that they are adding the comment to.
     * @param request - json for the comment
     * @return - Boolean indicating success
     */
    @POST
    public boolean addCommentForClimb(NewCommentRequest request){
        boolean addedComment = false;

        try(Connection conn = ConnectionPool.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(ADD_CLIMB_QUERY);
            stmt.setString(1, request.getCommmentText());
            stmt.setString(2, request.getUsername());
            stmt.setLong(3, request.getDateText());
            stmt.setInt(4, request.getClimbId());
            stmt.execute();
            addedComment = true;

        } catch (SQLException e) {
            e.printStackTrace();
            addedComment = false;
        }

        return addedComment;
    }

    /**
     * Defines json for body of request.
     */
    public static class NewCommentRequest {
        protected String username;
        protected int climbId;
        protected String commmentText;
        protected long dateText;

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

        public String getCommmentText() {
            return commmentText;
        }

        public void setCommmentText(String commmentText) {
            this.commmentText = commmentText;
        }

        public long getDateText() {
            return dateText;
        }

        public void setDateText(long dateText) {
            this.dateText = dateText;
        }
    }
}



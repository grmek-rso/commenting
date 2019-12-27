package com.grmek.rso.commenting;

import com.kumuluz.ee.logs.cdi.Log;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("")
@Log
public class CommentResource {

    @Inject
    private ConfigurationProperties cfg;

    @POST
    @Path("users/{userId}/albums/{albumId}/images/{imageId}")
    public Response addNewComment(@PathParam("userId") int userId,
                                  @PathParam("albumId") int albumId,
                                  @PathParam("imageId") int imageId,
                                  Comment comment) {
        try (
            Connection con = DriverManager.getConnection(cfg.getDbUrl(), cfg.getDbUser(), cfg.getDbPassword());
            Statement stmt = con.createStatement();
        ) {
            stmt.executeUpdate("INSERT INTO comments (comment_user_id, comment_text, user_id, album_id, image_id)"
                               + " VALUES ('" + comment.getCommentUserId() + "', '" + comment.getCommentText() + "', '"
                               + userId + "', '" + albumId + "', '" + imageId + "')");
        }
        catch (SQLException e) {
            System.err.println(e);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.noContent().build();
    }
    
    @GET
    @Path("users/{userId}/albums/{albumId}/images/{imageId}")
    public Response getComments(@PathParam("userId") int userId,
                                @PathParam("albumId") int albumId,
                                @PathParam("imageId") int imageId) {
        List<Comment> comments = new LinkedList<Comment>();

        try (
            Connection con = DriverManager.getConnection(cfg.getDbUrl(), cfg.getDbUser(), cfg.getDbPassword());
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM comments WHERE user_id = " + userId
                                             + " AND album_id = " + albumId + " AND image_id = " + imageId);
        ) {
            while (rs.next()) {
                Comment comment = new Comment();
                comment.setId(rs.getString(1));
                comment.setCommentUserId(rs.getString(2));
                comment.setCommentText(rs.getString(3));
                comments.add(comment);
            }
        }
        catch (SQLException e) {
            System.err.println(e);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.ok(comments).build();
    }

    @DELETE
    @Path("users/{userId}")
    public Response deleteCommentsForUser(@PathParam("userId") int userId) {
        try (
            Connection con = DriverManager.getConnection(cfg.getDbUrl(), cfg.getDbUser(), cfg.getDbPassword());
            Statement stmt = con.createStatement();
        ) {
            stmt.executeUpdate("DELETE FROM comments WHERE comment_user_id = " + userId
                               + " OR user_id = " + userId);
        }
        catch (SQLException e) {
            System.err.println(e);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.noContent().build();
    }

    @DELETE
    @Path("albums/{albumId}")
    public Response deleteCommentsForAlbum(@PathParam("albumId") int albumId) {
        try (
                Connection con = DriverManager.getConnection(cfg.getDbUrl(), cfg.getDbUser(), cfg.getDbPassword());
                Statement stmt = con.createStatement();
        ) {
            stmt.executeUpdate("DELETE FROM comments WHERE album_id = " + albumId);
        }
        catch (SQLException e) {
            System.err.println(e);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.noContent().build();
    }

    @DELETE
    @Path("images/{imageId}")
    public Response deleteCommentsForImage(@PathParam("imageId") int imageId) {
        try (
                Connection con = DriverManager.getConnection(cfg.getDbUrl(), cfg.getDbUser(), cfg.getDbPassword());
                Statement stmt = con.createStatement();
        ) {
            stmt.executeUpdate("DELETE FROM comments WHERE image_id = " + imageId);
        }
        catch (SQLException e) {
            System.err.println(e);
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.noContent().build();
    }
}

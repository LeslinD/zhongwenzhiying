package dao;

import SQLTemplate.*;
import entity.Crew;
import entity.Genre;
import entity.GenreMovie;
import entity.Movie;
import util.DBConnector;
import util.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenreDao {
    public static List<Genre> QueryAndResolve(String sql){
        List<Genre> gl = new ArrayList<Genre>();
        Connection conn = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        /** Query. */
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Genre g = new Genre();
                g.setId(rs.getInt(1));
                g.setGenreName(rs.getString(2));
                gl.add(g);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnector.closeConnection(conn, pstmt, rs);
        }

        return gl;
    }

    public List<Genre> SelectAll(){
        return QueryAndResolve(
                new SelectT(TableName.genre_table)
                        .AddOrder(new Genre().Id().attri_name)
                        .toSQL()
        );
    }

    public List<Genre> SelectByMovieId(Integer movie_id) {
        GenreMovie wanted = new GenreMovie();
        wanted.setMovieId(movie_id);
        return QueryAndResolve(
                new SelectT(List.of(TableName.genre_table, TableName.movie_genre_table))
                        .AddColumn(TableName.genre_table, "*")
                        .AddCondition(new Condition(Condition.Opt.E,
                                TableName.genre_table, new Genre().Id().attri_name,
                                TableName.movie_genre_table, new GenreMovie().GenreId().attri_name
                        ))
                        .AddCondition(new Condition(Condition.Opt.E, wanted.MovieId()))
                        .toSQL()
        );
    }
}

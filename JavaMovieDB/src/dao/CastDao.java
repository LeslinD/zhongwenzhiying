package dao;

import SQLTemplate.*;
import entity.Cast;
import util.DBConnector;
import util.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CastDao {

    public static List<Cast> QueryAndResolve(String sql){
        List<Cast> cl = new ArrayList<Cast>();
        Connection conn = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        /** Query. */
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Cast c = new Cast();
                c.setCastId(rs.getInt(1));
                c.setMovieId(rs.getInt(2));
                c.setActorId(rs.getInt(3));
                c.setCharacterName(rs.getString(4));
                c.setOrderOfAppearance(rs.getInt(5));
                cl.add(c);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnector.closeConnection(conn, pstmt, rs);
        }

        return cl;
    }

    public int Insert(Cast c){
        return SQLUtil.Update(
                new InsertT(TableName.cast_table)
                        .AddKeyValuePair(c.MovieId())
                        .AddKeyValuePair(c.CastId())
                        .AddKeyValuePair(c.ActorId())
                        .AddKeyValuePair(c.CharacterName())
                        .AddKeyValuePair(c.OrderOfAppearance())
                        .toSQL()
        );
    }

    public int Update(Cast c){
        return SQLUtil.Update(
                new UpdateT(TableName.cast_table)
                        .AddKeyValuePair(c.ActorId())
                        .AddKeyValuePair(c.CharacterName())
                        .AddKeyValuePair(c.OrderOfAppearance())
                        .AddCondition(new Condition(Condition.Opt.E, c.MovieId()))
                        .AddCondition(new Condition(Condition.Opt.E, c.CastId()))
                        .toSQL()
        );
    }

    public List<Cast> SelectByMovieID(Integer movie_id){
        Cast wanted = new Cast();
        wanted.setMovieId(movie_id);
        return QueryAndResolve(
                new SelectT(TableName.cast_table)
                        .AddCondition(new Condition(Condition.Opt.E, wanted.MovieId()))
                        .toSQL()
        );
    }

}

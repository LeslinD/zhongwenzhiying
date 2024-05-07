package dao;

import SQLTemplate.Condition;
import SQLTemplate.InsertT;
import SQLTemplate.SelectT;
import SQLTemplate.UpdateT;
import entity.Crew;
import util.DBConnector;
import util.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrewDao {

    public static List<Crew> QueryAndResolve(String sql){
        List<Crew> cl = new ArrayList<Crew>();
        Connection conn = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        /** Query. */
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Crew c = new Crew();
                c.setCrewId(rs.getInt(1));
                c.setMovieId(rs.getInt(2));
                c.setCrewMemberId(rs.getInt(3));
                c.setJob(rs.getString(4));
                c.setDepartment(rs.getString(5));
                cl.add(c);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnector.closeConnection(conn, pstmt, rs);
        }

        return cl;
    }

    public int Insert(Crew c){
        return SQLUtil.Update(
                new InsertT(TableName.cast_table)
                        .AddKeyValuePair(c.CrewId())
                        .AddKeyValuePair(c.MovieId())
                        .AddKeyValuePair(c.CrewMemberId())
                        .AddKeyValuePair(c.Job())
                        .AddKeyValuePair(c.Department())
                        .toSQL()
        );
    }

    public int Update(Crew c){
        return SQLUtil.Update(
                new UpdateT(TableName.cast_table)
                        .AddKeyValuePair(c.CrewMemberId())
                        .AddKeyValuePair(c.Job())
                        .AddKeyValuePair(c.Department())
                        .AddCondition(new Condition(Condition.Opt.E, c.CrewId()))
                        .AddCondition(new Condition(Condition.Opt.E, c.MovieId()))
                        .toSQL()
        );
    }
    public List<Crew> SelectByMovieID(Integer movie_id){
        Crew wanted = new Crew();
        wanted.setMovieId(movie_id);
        return QueryAndResolve(
                new SelectT(TableName.cast_table)
                        .AddCondition(new Condition(Condition.Opt.E, wanted.MovieId()))
                        .toSQL()
        );
    }

}

package dao;

import SQLTemplate.*;
import entity.*;
import util.DBConnector;
import util.SQLUtil;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MoviesDao {

    public static List<Movie> QueryAndResolve(String sql) {
        List<Movie> ml = new ArrayList<>();
        Connection conn = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Movie m = new Movie();
                m.setMovieId(rs.getInt(1));
                m.setBudget(rs.getString(2));
                m.setHomepage(rs.getString(3));
                m.setOriginalLanguage(rs.getString(4));
                m.setOriginalTitle(rs.getString(5));
                m.setOverview(rs.getString(6));
                m.setPopularity(rs.getDouble(7));
                m.setReleaseDate(rs.getString(8));
                m.setRevenue(rs.getString(9));
                m.setRuntime(rs.getInt(10));
                m.setStatus(rs.getString(11));
                m.setTagline(rs.getString(12));
                m.setTitle(rs.getString(13));
                m.setVoteAverage(rs.getDouble(14));
                m.setVoteCount(rs.getInt(15));
                ml.add(m);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnector.closeConnection(conn, pstmt, rs);
        }

        return ml;
    }

    public int Insert(Movie m) {
        return SQLUtil.Update(
                new InsertT(TableName.movie_table)
                        .AddKeyValuePair(m.MovieId())
                        .AddKeyValuePair(m.Budget())
                        .AddKeyValuePair(m.Homepage())
                        .AddKeyValuePair(m.OriginalLanguage())
                        .AddKeyValuePair(m.OriginalTitle())
                        .AddKeyValuePair(m.Overview())
                        .AddKeyValuePair(m.ReleaseDate())
                        .AddKeyValuePair(m.Revenue())
                        .AddKeyValuePair(m.Runtime())
                        .AddKeyValuePair(m.Status())
                        .AddKeyValuePair(m.Tagline())
                        .AddKeyValuePair(m.Title())
                        .AddKeyValuePair(m.VoteAverage())
                        .AddKeyValuePair(m.VoteCount())
                        .toSQL()
        );
    }

    public int Update(Movie m) {
        return SQLUtil.Update(
                new UpdateT(TableName.movie_table)
                        .AddKeyValuePair(m.Budget())
                        .AddKeyValuePair(m.Homepage())
                        .AddKeyValuePair(m.OriginalLanguage())
                        .AddKeyValuePair(m.OriginalTitle())
                        .AddKeyValuePair(m.Overview())
                        .AddKeyValuePair(m.ReleaseDate())
                        .AddKeyValuePair(m.Revenue())
                        .AddKeyValuePair(m.Runtime())
                        .AddKeyValuePair(m.Status())
                        .AddKeyValuePair(m.Tagline())
                        .AddKeyValuePair(m.Title())
                        .AddKeyValuePair(m.VoteAverage())
                        .AddKeyValuePair(m.VoteCount())
                        .AddCondition(new Condition(Condition.Opt.E, m.MovieId()))
                        .toSQL()
        );
    }

    public int DeleteById(Integer id) {
        Movie wanted = new Movie();
        wanted.setMovieId(id);

        return SQLUtil.Update(
                new DeleteT(TableName.movie_table)
                        .AddCondition(new Condition(Condition.Opt.E, wanted.MovieId()))
                        .toSQL()
        );
    }

    public List<Movie> TopPopular(Integer page){
        return QueryAndResolve(
                new SelectT(TableName.movie_table)
                        .AddOrder(new Movie().VoteAverage().attri_name, SelectT.OrderType.DESC)
                        .Limit((page-1) * 20, 20)
                        .toSQL()
        );
    }

    public List<Movie> TopLatest(Integer page){
        return QueryAndResolve(
                new SelectT(TableName.movie_table)
                        .AddOrder(new Movie().ReleaseDate().attri_name, SelectT.OrderType.DESC)
                        .Limit((page-1) * 20, 20)
                        .toSQL()
        );
    }

    public List<Movie> getGenre(Integer genre_id, Integer page) {
        Genre wanted = new Genre();
        wanted.setId(genre_id);
        return QueryAndResolve(
                new SelectT(List.of(TableName.movie_table, TableName.genre_table, TableName.movie_genre_table))
                        .AddColumn(TableName.movie_table, "*")
                        .AddOrder(new Movie().VoteAverage().attri_name, SelectT.OrderType.DESC)
                        .AddOrder(new Movie().ReleaseDate().attri_name, SelectT.OrderType.DESC)
                        .Limit((page-1) * 20, 20)
                        .AddCondition(new Condition(Condition.Opt.E,
                                TableName.movie_table, new Movie().MovieId().attri_name,
                                TableName.movie_genre_table, new GenreMovie().MovieId().attri_name))
                        .AddCondition(new Condition(Condition.Opt.E,
                                TableName.genre_table, new Genre().Id().attri_name,
                                TableName.movie_genre_table, new GenreMovie().GenreId().attri_name))
                        .AddCondition(new Condition(Condition.Opt.E, TableName.genre_table, wanted.Id()))
                        .toSQL()
        );
    }

    public List<Movie> selectName(String name, Integer page) {
        Movie wanted = new Movie();
        /** Movie title contains {name}. */
        wanted.setTitle("%" + name + "%");
        return QueryAndResolve(
                new SelectT(TableName.movie_table)
                        .AddOrder(wanted.VoteAverage().attri_name, SelectT.OrderType.DESC)
                        .AddOrder(wanted.ReleaseDate().attri_name, SelectT.OrderType.DESC)
                        .Limit((page-1) * 20, 20)
                        .AddCondition(new Condition(Condition.Opt.LI, wanted.Title()))
                        .toSQL()
        );
    }

    public List<Movie> selectByPersonID(Integer personId) {
        Cast wanted = new Cast();
        wanted.setActorId(personId);
        Crew orWanted = new Crew();
        orWanted.setCrewMemberId(personId);
        return QueryAndResolve(
                SelectT.Union(List.of(
                        new SelectT(List.of(TableName.movie_table, TableName.cast_table))
                                .AddColumn(TableName.movie_table, "*")
                                .AddCondition(new Condition(Condition.Opt.E,
                                        TableName.movie_table, new Movie().MovieId().attri_name,
                                        TableName.cast_table, new Cast().MovieId().attri_name))
                                .AddCondition(new Condition(Condition.Opt.E, wanted.ActorId())),
                        new SelectT(List.of(TableName.movie_table, TableName.crew_table))
                                .AddColumn(TableName.movie_table, "*")
                                .AddCondition(new Condition(Condition.Opt.E,
                                        TableName.movie_table, new Movie().MovieId().attri_name,
                                        TableName.crew_table, new Crew().MovieId().attri_name))
                                .AddCondition(new Condition(Condition.Opt.E, orWanted.CrewMemberId()))
                ))
        );
    }

    public Movie selectID(Integer movieId) {
        Movie wanted = new Movie();
        wanted.setMovieId(movieId);
        return QueryAndResolve(
                new SelectT(TableName.movie_table)
                        .AddCondition(new Condition(Condition.Opt.E, wanted.MovieId()))
                        .toSQL()
        ).get(0);
    }

}

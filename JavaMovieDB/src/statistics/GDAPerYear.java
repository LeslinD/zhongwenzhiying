/**
 * This file is to illustrate the trend of movie genres distribution for most recent 10 years in database.
 */
package statistics;

import SQLTemplate.Condition;
import SQLTemplate.SelectT;
import dao.GenreDao;
import dao.MoviesDao;
import dao.TableName;
import entity.Genre;
import entity.GenreMovie;
import entity.Movie;
import exce.XDimensionParameterException;
import util.SQLUtil;

import java.text.SimpleDateFormat;
import java.util.*;


public class GDAPerYear {
    public static final int xDimension = 10;
    public List<String> xLabels = new ArrayList<>();
    public List<String> yLabels = new ArrayList<>();

    public GDAPerYear() {
        Set<String> xSet = new HashSet<>();
        Set<String> ySet = new HashSet<>();

        MoviesDao.QueryAndResolve(
                new SelectT(TableName.movie_table).toSQL()
        ).forEach( movie -> xSet.add(new SimpleDateFormat("yyyy").format(movie.release_date)));
        this.xLabels.addAll(xSet);
        this.xLabels.sort(Collections.reverseOrder());
        try {
            if (this.xLabels.size() < xDimension)
                throw new XDimensionParameterException();
        } catch (XDimensionParameterException xdpe) {
            xdpe.printStackTrace();
        }

        GenreDao.QueryAndResolve(
                new SelectT(TableName.genre_table).toSQL()
        ).forEach( genre -> ySet.add(genre.genre_name));
        this.yLabels.addAll(ySet);
        Collections.sort(this.yLabels);
    }
    public List<List<String>> GenresDistributionByYear() {
        List<List<String>> lines = new ArrayList<>();
        List<String> title = new ArrayList<>();
        title.add("");
        title.addAll(this.xLabels.subList(0, xDimension));
        lines.add(title);

        for (String label : this.yLabels) {
            List<String> line = new ArrayList<>();
            line.add(label);
            for (int i = 0; i < xDimension; i ++){
                line.add(SQLUtil.CountAndResolve(
                        new SelectT(List.of(TableName.movie_table, TableName.genre_table, TableName.movie_genre_table))
                                .AddGroup(
                                        TableName.movie_table,
                                        new Movie().MovieId().attri_name,
                                        SelectT.GroupFunc.COUNT,
                                        "YEAR(" + new Movie().ReleaseDate().attri_name + ")"
                                )
                                .AddGroup(new Genre().GenreName().attri_name)
                                .AddColumn("YEAR(" + new Movie().ReleaseDate().attri_name + ")")
                                .AddColumn(new Genre().GenreName().attri_name)
                                .AddCondition(new Condition(Condition.Opt.E,
                                        TableName.movie_table, new Movie().MovieId().attri_name,
                                        TableName.movie_genre_table, new GenreMovie().MovieId().attri_name))
                                .AddCondition(new Condition(Condition.Opt.E,
                                        TableName.genre_table, new Genre().Id().attri_name,
                                        TableName.movie_genre_table, new GenreMovie().GenreId().attri_name))
                                .AddCondition(new Condition(Condition.Opt.E, new Genre().GenreName().attri_name, '\'' + label + '\''))
                                .AddCondition(new Condition(Condition.Opt.E, "YEAR(" + new Movie().ReleaseDate().attri_name + ")", this.xLabels.get(i)))
                                .toSQL()
                ).get(0));
            }
            lines.add(line);
        }
        return lines;
    }
}

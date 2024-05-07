/**
 * This file is to illustrate the trend of movie original language distribution for most recent 20 years in database.
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

public class OLAPerYear {
    public static final int xDimension = 20;
    public List<String> xLabels = new ArrayList<>();
    public List<String> yLabels = new ArrayList<>();

    public OLAPerYear() {
        Set<String> xSet = new HashSet<>();
        Set<String> ySet = new HashSet<>();

        MoviesDao.QueryAndResolve(
                new SelectT(TableName.movie_table).toSQL()
        ).forEach( movie -> {
            xSet.add(new SimpleDateFormat("yyyy").format(movie.release_date));
            ySet.add(movie.original_language);
        });

        this.xLabels.addAll(xSet);
        this.xLabels.sort(Collections.reverseOrder());
        this.yLabels.addAll(ySet);
        Collections.sort(this.yLabels);

        try {
            if (this.xLabels.size() < xDimension)
                throw new XDimensionParameterException();
        } catch (XDimensionParameterException xdpe) {
            xdpe.printStackTrace();
        }
    }

    public List<List<String>> OriginalLanguagePerYear() {
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
                        new SelectT(TableName.movie_table)
                                .AddGroup(
                                        TableName.movie_table,
                                        new Movie().MovieId().attri_name,
                                        SelectT.GroupFunc.COUNT,
                                        "YEAR(" + new Movie().ReleaseDate().attri_name + ")"
                                )
                                .AddGroup(new Movie().OriginalLanguage().attri_name)
                                .AddColumn("YEAR(" + new Movie().ReleaseDate().attri_name + ")")
                                .AddColumn(new Movie().OriginalLanguage().attri_name)
                                .AddCondition(new Condition(Condition.Opt.E, new Movie().OriginalLanguage().attri_name, '\'' + label + '\''))
                                .AddCondition(new Condition(Condition.Opt.E, "YEAR(" + new Movie().ReleaseDate().attri_name + ")", this.xLabels.get(i)))
                                .toSQL()
                ).get(0));
            }
            lines.add(line);
        }
        return lines;
    }
}

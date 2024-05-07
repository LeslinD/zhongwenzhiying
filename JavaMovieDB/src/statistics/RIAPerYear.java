/**
 * This file is to illustrate the runtime intervals distribution per year.
 */
package statistics;

import SQLTemplate.Condition;
import SQLTemplate.SelectT;
import dao.MoviesDao;
import dao.TableName;
import entity.Movie;
import exce.XDimensionParameterException;
import util.SQLUtil;

import java.text.SimpleDateFormat;
import java.util.*;

public class RIAPerYear {
    public static final int xDimension = 30;
    public List<String> xLabels = new ArrayList<>();
    public List<String> yLabels = new ArrayList<>();

    public RIAPerYear() {
        Set<String> xSet = new HashSet<>();
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

        this.yLabels = List.of("< 60", ">= 60 & < 120", ">= 120 & < 180", ">= 180 & < 240", ">= 240");
    }

    public List<List<String>> RuntimeIntervalsByYear() {
        List<List<String>> lines = new ArrayList<>();
        List<String> title = new ArrayList<>();
        title.add("");
        title.addAll(this.xLabels.subList(0, xDimension));
        lines.add(title);

        for (int ii = 0; ii < 5; ii ++) {
            List<String> line = new ArrayList<>();
            line.add(this.yLabels.get(ii));
            for (int i = 0; i < xDimension; i ++){
                line.add(SQLUtil.CountAndResolve(
                        new SelectT(TableName.movie_table)
                                .AddGroup(
                                        TableName.movie_table,
                                        new Movie().MovieId().attri_name,
                                        SelectT.GroupFunc.COUNT,
                                        "YEAR(" + new Movie().ReleaseDate().attri_name + ")"
                                )
                                .AddColumn("YEAR(" + new Movie().ReleaseDate().attri_name + ")")
                                .AddCondition(new Condition(Condition.Opt.NL, new Movie().Runtime().attri_name, String.valueOf(ii * 60)))
                                .AddCondition(new Condition(Condition.Opt.L, new Movie().Runtime().attri_name, String.valueOf((ii+1) * 60)))
                                .AddCondition(new Condition(Condition.Opt.E, "YEAR(" + new Movie().ReleaseDate().attri_name + ")", this.xLabels.get(i)))
                                .toSQL()
                ).get(0));
            }
            lines.add(line);
        }
        return lines;
    }

}

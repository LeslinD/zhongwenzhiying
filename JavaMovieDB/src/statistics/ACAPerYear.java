/**
 * This file is to illustrate the contribution of movies of most devoted actor per year.
 */
package statistics;

import SQLTemplate.Condition;
import SQLTemplate.SelectT;
import dao.GenreDao;
import dao.MoviesDao;
import dao.PersonDao;
import dao.TableName;
import entity.*;
import exce.XDimensionParameterException;
import util.SQLUtil;

import java.text.SimpleDateFormat;
import java.util.*;


public class ACAPerYear {
    public static final int xDimension = 20;
    public List<String> xLabels = new ArrayList<>();
    public List<String> yLabels = new ArrayList<>();

    public ACAPerYear() {
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

        PersonDao.QueryAndResolve(
                new SelectT(List.of(TableName.movie_table, TableName.cast_table, TableName.person_table))
                        .AddColumn(TableName.person_table, "*")
                        .AddGroup(new Cast().ActorId().attri_name)
                        .AddCondition(new Condition(Condition.Opt.E,
                                TableName.movie_table, new Movie().MovieId().attri_name,
                                TableName.cast_table, new Cast().MovieId().attri_name))
                        .AddCondition(new Condition(Condition.Opt.E,
                                TableName.cast_table, new Cast().ActorId().attri_name,
                                TableName.person_table, new Person().Id().attri_name))
                        .AddOrder(SelectT.GroupFunc.COUNT, TableName.movie_table, new Movie().MovieId().attri_name, SelectT.OrderType.DESC)
                        .Limit(100)
                        .toSQL()
        ).forEach( person -> this.yLabels.add(person.name));
    }

    public List<List<String>> ActorContributionByYear() {
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
                        new SelectT(List.of(TableName.movie_table, TableName.cast_table, TableName.person_table))
                                .AddGroup(
                                        TableName.movie_table,
                                        new Movie().MovieId().attri_name,
                                        SelectT.GroupFunc.COUNT,
                                        "YEAR(" + new Movie().ReleaseDate().attri_name + ")"
                                )
                                .AddGroup(new Cast().ActorId().attri_name)
                                .AddColumn("YEAR(" + new Movie().ReleaseDate().attri_name + ")")
                                .AddColumn(new Cast().ActorId().attri_name)
                                .AddCondition(new Condition(Condition.Opt.E,
                                        TableName.movie_table, new Movie().MovieId().attri_name,
                                        TableName.cast_table, new Cast().MovieId().attri_name))
                                .AddCondition(new Condition(Condition.Opt.E,
                                        TableName.cast_table, new Cast().ActorId().attri_name,
                                        TableName.person_table, new Person().Id().attri_name))
                                .AddCondition(new Condition(Condition.Opt.E, new Person().Name().attri_name, '\'' + label + '\''))
                                .AddCondition(new Condition(Condition.Opt.E, "YEAR(" + new Movie().ReleaseDate().attri_name + ")", this.xLabels.get(i)))
                                .toSQL()
                ).get(0));
            }
            lines.add(line);
        }
        return lines;
    }

}

package SQLTemplate;

import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectT extends SQLT{
    public enum OrderType {
        ASC, DESC
    }
    public enum GroupFunc {
        COUNT, MAX, MIN, SUM, AVG
    }
    public StringBuffer selectSQL;
    public List<String> columns = new ArrayList<>();
    public List<String> tables = new ArrayList<>();
    public List<String> orders = new ArrayList<>();
    public List<String> group = new ArrayList<>();
    public String limit = null;

    public SelectT(List<String> tables) {
        this.tables.addAll(tables);
        this.selectSQL = new StringBuffer("SELECT ");
    }
    public SelectT(String table) {
        this.table = table;
        this.selectSQL = new StringBuffer("SELECT ");
    }

    public SelectT AddCondition(Condition condition) {
        this.conditions.add(condition.toString());
        return this;
    }

    public SelectT AddCondition(String condition) {
        this.conditions.add(condition);
        return this;
    }
    public SelectT AddColumn(String column) {
        this.columns.add(column);
        return this;
    }
    public SelectT AddColumn(String table, String column) {
        this.columns.add(table + '.' + column);
        return this;
    }

    public SelectT AddOrder(String attri) {
        this.orders.add(attri);
        return this;
    }
    public SelectT AddOrder(String attri, OrderType ot) {
        this.orders.add(attri + ' ' + switch (ot) {
            case ASC -> "ASC";
            case DESC -> "DESC";
        });
        return this;
    }
    public SelectT AddOrder(GroupFunc groupFunc, String attri) {
        this.orders.add(Group(groupFunc, attri));
        return this;
    }
    public SelectT AddOrder(GroupFunc groupFunc, String attri, OrderType ot) {
        this.orders.add(Group(groupFunc, attri)+ switch (ot) {
                   case ASC -> "ASC";
                   case DESC -> "DESC";
        });
        return this;
    }
    public SelectT AddOrder(GroupFunc groupFunc, String table, String attri, OrderType ot) {
        this.orders.add(Group(groupFunc, table + '.' + attri) + switch (ot) {
                    case ASC -> "ASC";
                    case DESC -> "DESC";
                });
        return this;
    }

    public SelectT AddGroup(String columnToGroup) {
        this.group.add(columnToGroup);
        return this;
    }
    public SelectT AddGroup(String groupBy, GroupFunc groupFunc, String columnToGroup) {
        this.group.add(columnToGroup);
        this.columns.add(Group(groupFunc, groupBy));
        return this;
    }
    public SelectT AddGroup(String table, String groupBy, GroupFunc groupFunc, String columnToGroup) {
        this.group.add(columnToGroup);
        this.columns.add(Group(groupFunc, table + '.' + groupBy));
        return this;
    }

    public SelectT Limit(Integer limit) {
        this.limit = String.valueOf(limit);
        return this;
    }
    public SelectT Limit(Integer begin, Integer limit) {
        this.limit = begin + ", " + limit;
        return this;
    }

    @Override
    public String toSQL() {
        if (this.columns.isEmpty()) {
            this.selectSQL.append("*");
        } else {
            this.selectSQL.append(String.join(", ", this.columns));
        }
        this.selectSQL.append(" \nFROM ");
        if (this.tables.isEmpty()) {
            this.selectSQL.append(this.table);
        } else {
            this.selectSQL.append(String.join(", ", this.tables));
        }
        if (! this.conditions.isEmpty()) {
            this.selectSQL.append(" \nWHERE ").append(String.join(" AND ", this.conditions));
        }
        if (! this.group.isEmpty()) {
            this.selectSQL.append(" \nGROUP BY ").append(String.join(", ", this.group));
        }
        if (! this.orders.isEmpty()) {
            this.selectSQL.append(" \nORDER BY ").append(String.join(", ", this.orders));
        }
        if (this.limit != null) {
            this.selectSQL.append(" \nLIMIT ").append(this.limit);
        }
        return this.selectSQL.append(';').toString();
    }

    public static String Union(List<SelectT> stl) {
        List<String> sqls = new ArrayList<>();
        for (SelectT st : stl) {
            StringBuffer sql = new StringBuffer("(\nSELECT ");
            if (st.columns.isEmpty()) {
                sql.append("*");
            } else {
                sql.append(String.join(", ", st.columns));
            }
            sql.append(" \nFROM ");
            if (st.tables.isEmpty()) {
                sql.append(st.table);
            } else {
                sql.append(String.join(", ", st.tables));
            }
            if (! st.conditions.isEmpty()) {
                sql.append(" \nWHERE ").append(String.join(" AND ", st.conditions));
            }
            if (! st.group.isEmpty()) {
                sql.append(" \nGROUP BY").append(String.join(", ", st.group));
            }
            if (! st.orders.isEmpty()) {
                sql.append(" \nORDER BY ").append(String.join(", ", st.orders));
            }
            if (st.limit != null) {
                sql.append(" \nLIMIT ").append(st.limit);
            }
            sql.append("\n)");
            sqls.add(sql.toString());
        }

        return String.join("\nUNION\n", sqls) + ';';
    }

    private static String Group(GroupFunc groupFunc, String groupBy) {
        return switch (groupFunc) {
            case COUNT -> "COUNT(";
            case MAX -> "MAX(";
            case MIN -> "MIN(";
            case SUM -> "SUM(";
            case AVG -> "AVG(";
        } + groupBy + ")";
    }
}

package SQLTemplate;

import java.util.List;

public class DeleteT extends SQLT{
    public StringBuffer deleteSQL;
    public DeleteT(String table) {
        this.table = table;
        this.deleteSQL = new StringBuffer("DELETE FROM \n").append(table).append(" \n");
    }

    @Override
    public String toSQL() {
        if (! conditions.isEmpty()) {
            this.deleteSQL.append("WHERE ").append(String.join(" AND ", this.conditions));
        }
        return this.deleteSQL.append(';').toString();
    }
}

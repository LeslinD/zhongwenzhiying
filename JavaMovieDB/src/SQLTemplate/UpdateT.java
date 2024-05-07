package SQLTemplate;

public class UpdateT extends SQLT{
    public StringBuffer updateSQL;

    public UpdateT(String table) {
        this.table = table;
        this.updateSQL = new StringBuffer("UPDATE ").append(table).append(" \n");
    }

    @Override
    public String toSQL() {
        this.updateSQL.append("SET ");
        for (int i = 0; i < keys.size(); i ++) {
            this.updateSQL.append(this.keys.get(i)).append(" = ").append(this.values.get(i));
            if (i < keys.size()-1) {
                this.updateSQL.append(", ");
            } else {
                this.updateSQL.append(" \n");
            }
        }
        if (! conditions.isEmpty()) {
            this.updateSQL.append("WHERE ").append(String.join(" AND ", this.conditions));
        }
        return this.updateSQL.append(';').toString();
    }

}

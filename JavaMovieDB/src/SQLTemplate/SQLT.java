package SQLTemplate;

import util.SQLUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class SQLT {
    /** An abstract SQL Template without SQL syntax checking. */
    public String table;
    public List<String> keys = new ArrayList<>();
    public List<String> values = new ArrayList<>();
    public List<String> conditions = new ArrayList<>();

    public SQLT AddKeyValuePair(String key, String value) {
        this.keys.add(key);
        this.values.add(value);
        return this;
    }

    public SQLT AddKeyValuePair(SQLUtil.DataInfo di) {
        this.keys.add(di.attri_name);
        this.values.add(SQLUtil.DataParser(di));
        return this;
    }

    public SQLT AddCondition(Condition condition) {
        this.conditions.add(condition.toString());
        return this;
    }

    public SQLT AddCondition(String condition) {
        this.conditions.add(condition);
        return this;
    }

    public abstract String toSQL();
}

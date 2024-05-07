package SQLTemplate;

import java.util.Arrays;
import java.util.Map;

public class QueryBuilder {
    private String[] properties = {"popularity", "release_date", "runtime", "vote_average","vote_count","character_name","name","original_title"};
    private Map<String, String> dic;

    public QueryBuilder(Map<String, String> dic) {
        this.dic = dic;
    }

    public String buildQuery() {
        StringBuilder SQL = new StringBuilder("");
        dic.put("idtype", "");
        dic.put("usernametype", "\'");
        dic.put("passwordtype", "\'");
        dic.put("emailtype", "\'");

        SQL.append("SELECT " + String.join(", ", Arrays.asList(properties)) + " \nFROM t_user");

        int condition = 0;
        for (String property : properties) {
            if (!dic.get(property + "Query").isEmpty()) {
                if (condition == 0) {
                    SQL.append("\nWHERE ");
                } else {
                    SQL.append(", ");
                }
                SQL.append(property + "=" + dic.get(property + "type") + dic.get(property + "Query") + dic.get(property + "type"));
                condition++;
            }
        }

        condition = 0;
        for (String property : properties) {
            if (dic.get(property + "Query").isEmpty() && !"default".equals(dic.get(property + "Sort"))) {
                if (condition == 0) {
                    SQL.append("\nORDER BY ");
                } else {
                    SQL.append(", ");
                }
                SQL.append(property + " " + dic.get(property + "Sort"));
                condition++;
            }
        }

        SQL.append(";");
        return SQL.toString();
    }
}

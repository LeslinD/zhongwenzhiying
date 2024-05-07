package entity;

import util.SQLUtil;

public class Keyword {
    public Integer id = 0;
    public String keyword_name = "";

    /** Getters which return the attribute name, attribute type in MySQL, and the exact value. */
    public SQLUtil.DataInfo Id() {
        return new SQLUtil.DataInfo("id", SQLUtil.DataType.BIGINT, this.id);
    }
    public SQLUtil.DataInfo KeywordName() {
        return new SQLUtil.DataInfo("keyword_name", SQLUtil.DataType.BIGINT, this.keyword_name);
    }

    /** Setters. */
    public void setId(Integer id) {
        this.id = id;
    }
    public void setKeywordName(String keyword_name) {
        this.keyword_name=keyword_name;
    }
}

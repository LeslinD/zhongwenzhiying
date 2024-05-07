package entity;

import util.SQLUtil;

public class Genre {
    public Integer id = 0;
    public String genre_name = "";

    /** Getters which return the attribute name, attribute type in MySQL, and the exact value. */
    public SQLUtil.DataInfo Id() {
        return new SQLUtil.DataInfo("id", SQLUtil.DataType.BIGINT, this.id);
    }
    public SQLUtil.DataInfo GenreName() {
        return new SQLUtil.DataInfo("genre_name", SQLUtil.DataType.VARCHAR, this.genre_name);
    }

    /** Setters. */
    public void setId(Integer id) {
        this.id = id;
    }
    public void setGenreName(String genre_name) {
        this.genre_name = genre_name;
    }
}

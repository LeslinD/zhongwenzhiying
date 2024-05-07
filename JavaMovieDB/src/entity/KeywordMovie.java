package entity;

import util.SQLUtil;

public class KeywordMovie {
    public Integer movie_id = 0;
    public Integer keyword_id = 0;

    /** Getters which return the attribute name, attribute type in MySQL, and the exact value. */
    public SQLUtil.DataInfo MovieId() {
        return new SQLUtil.DataInfo("movie_id", SQLUtil.DataType.BIGINT, this.movie_id);
    }
    public SQLUtil.DataInfo KeywordId() {
        return new SQLUtil.DataInfo("keyword_id", SQLUtil.DataType.BIGINT, this.keyword_id);
    }

    /** Setters. */
    public void setMovieId(Integer movie_id) {
        this.movie_id = movie_id;
    }
    public void setKeywordId(Integer keyword_id) {
        this.keyword_id = keyword_id;
    }
}

package entity;

import util.SQLUtil;

public class Cast {
    public int cast_id = 0;
    public Integer movie_id = 0;
    public Integer actor_id = 0;
    public String character_name = "";
    public int order_of_appearance = 0;

    /** Getters which return the attribute name, attribute type in MySQL, and the exact value. */
    public SQLUtil.DataInfo CastId() {
        return new SQLUtil.DataInfo("cast_id", SQLUtil.DataType.INT, this.cast_id);
    }
    public SQLUtil.DataInfo MovieId() {
        return new SQLUtil.DataInfo("movie_id", SQLUtil.DataType.BIGINT, this.movie_id);
    }
    public SQLUtil.DataInfo ActorId() {
        return new SQLUtil.DataInfo("actor_id", SQLUtil.DataType.BIGINT, this.actor_id);
    }
    public SQLUtil.DataInfo CharacterName() {
        return new SQLUtil.DataInfo("actor_id", SQLUtil.DataType.VARCHAR, this.character_name);
    }
    public SQLUtil.DataInfo OrderOfAppearance() {
        return new SQLUtil.DataInfo("actor_id", SQLUtil.DataType.INT, this.order_of_appearance);
    }



    /** Setters. */
    public void setCastId(int cast_id) {
        this.cast_id = cast_id;
    }
    public void setMovieId(Integer movie_id) {
        this.movie_id = movie_id;
    }
    public void setActorId(Integer actor_id) {
        this.actor_id = actor_id;
    }
    public void setCharacterName(String character_name) {
        this.character_name = character_name;
    }
    public void setOrderOfAppearance(int order_of_appearance) {
        this.order_of_appearance = order_of_appearance;
    }
}

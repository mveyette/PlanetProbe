package org.veyette.planetprobe.helper;


public class Player {
    private String player_name;
    private int current_science_score;
    private int overall_science_score;
    private int science_level;

    public Player(String name, int current_sci, int overall_sci, int sci_lvl){
        player_name = name;
        current_science_score = current_sci;
        overall_science_score = overall_sci;
        science_level = sci_lvl;
    }

    public Player(String name){
        player_name = name;
        current_science_score = 0;
        overall_science_score = 0;
        science_level = 0;
    }

    public String getPlayer_name() {
        return player_name;
    }

    public void setPlayer_name(String player_name) {
        this.player_name = player_name;
    }

    public int getCurrent_science_score() {
        return current_science_score;
    }

    public void setCurrent_science_score(int current_science_score) {
        this.current_science_score = current_science_score;
    }

    public int getOverall_science_score() {
        return overall_science_score;
    }

    public void setOverall_science_score(int overall_science_score) {
        this.overall_science_score = overall_science_score;
    }

    public int getScience_level() {
        return science_level;
    }

    public void setScience_level(int science_level) {
        this.science_level = science_level;
    }
}

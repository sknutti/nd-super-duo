package barqsoft.footballscores.model;

import com.google.gson.annotations.Expose;

/**
 * Created by sknutti on 9/16/15.
 */
public class Result {
    @Expose
    private Integer goalsHomeTeam;
    @Expose
    private Integer goalsAwayTeam;

    public Integer getGoalsHomeTeam() {
        return goalsHomeTeam;
    }

    public void setGoalsHomeTeam(Integer goalsHomeTeam) {
        this.goalsHomeTeam = goalsHomeTeam;
    }

    public Integer getGoalsAwayTeam() {
        return goalsAwayTeam;
    }

    public void setGoalsAwayTeam(Integer goalsAwayTeam) {
        this.goalsAwayTeam = goalsAwayTeam;
    }
}

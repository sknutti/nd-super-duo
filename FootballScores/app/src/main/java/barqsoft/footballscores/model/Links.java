package barqsoft.footballscores.model;

import com.google.gson.annotations.Expose;

/**
 * Created by sknutti on 9/16/15.
 */
public class Links {
    @Expose
    private Self self;
    @Expose
    private Soccerseason soccerseason;
    @Expose
    private HomeTeam homeTeam;
    @Expose
    private AwayTeam awayTeam;

    public Self getSelf() {
        return self;
    }

    public void setSelf(Self self) {
        this.self = self;
    }

    public Soccerseason getSoccerseason() {
        return soccerseason;
    }

    public void setSoccerseason(Soccerseason soccerseason) {
        this.soccerseason = soccerseason;
    }

    public HomeTeam getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(HomeTeam homeTeam) {
        this.homeTeam = homeTeam;
    }

    public AwayTeam getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(AwayTeam awayTeam) {
        this.awayTeam = awayTeam;
    }
}

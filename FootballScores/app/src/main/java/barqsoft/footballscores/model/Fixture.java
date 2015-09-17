package barqsoft.footballscores.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sknutti on 9/16/15.
 */
public class Fixture {
    @SerializedName("_links")
    @Expose
    private Links Links;
    @Expose
    private String date;
    @Expose
    private String status;
    @Expose
    private Integer matchday;
    @Expose
    private String homeTeamName;
    @Expose
    private String awayTeamName;
    @Expose
    private Result result;

    public barqsoft.footballscores.model.Links getLinks() {
        return Links;
    }

    public void setLinks(barqsoft.footballscores.model.Links links) {
        Links = links;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getMatchday() {
        return matchday;
    }

    public void setMatchday(Integer matchday) {
        this.matchday = matchday;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}

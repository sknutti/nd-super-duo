package barqsoft.footballscores.model;

import com.google.gson.annotations.Expose;

/**
 * Created by sknutti on 9/16/15.
 */
public class Self {
    @Expose
    private String href;

    /**
     *
     * @return
     * The href
     */
    public String getHref() {
        return href;
    }

    /**
     *
     * @param href
     * The href
     */
    public void setHref(String href) {
        this.href = href;
    }
}

package it.jaschke.alexandria.model;

import com.google.gson.annotations.Expose;

/**
 * Created by sknutti on 9/10/15.
 */
public class IndustryIdentifier {
    @Expose
    private String type;
    @Expose
    private String identifier;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}

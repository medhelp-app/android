package com.medhelp.medhelp.model;

import java.io.Serializable;

public class Opinion implements Serializable {

    private String generalRating;
    private String comment;

    public String getGeneralRating() {
        return generalRating;
    }

    public void setGeneralRating(String generalRating) {
        this.generalRating = generalRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}

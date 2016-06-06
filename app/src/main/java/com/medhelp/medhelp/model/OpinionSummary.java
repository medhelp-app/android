package com.medhelp.medhelp.model;

import java.io.Serializable;

public class OpinionSummary implements Serializable {

    private String generalRating;
    private String punctualityRating;
    private String attentionRating;
    private String installationRating;
    private String numberOfEvaluations;

    public String getGeneralRating() {
        return generalRating;
    }

    public void setGeneralRating(String generalRating) {
        this.generalRating = generalRating;
    }

    public String getPunctualityRating() {
        return punctualityRating;
    }

    public void setPunctualityRating(String punctualityRating) {
        this.punctualityRating = punctualityRating;
    }

    public String getAttentionRating() {
        return attentionRating;
    }

    public void setAttentionRating(String attentionRating) {
        this.attentionRating = attentionRating;
    }

    public String getInstallationRating() {
        return installationRating;
    }

    public void setInstallationRating(String installationRating) {
        this.installationRating = installationRating;
    }

    public String getNumberOfEvaluations() {
        return numberOfEvaluations;
    }

    public void setNumberOfEvaluations(String numberOfEvaluations) {
        this.numberOfEvaluations = numberOfEvaluations;
    }
}

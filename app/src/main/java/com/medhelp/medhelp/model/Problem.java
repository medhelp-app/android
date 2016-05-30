package com.medhelp.medhelp.model;

public class Problem {

    String problem;
    String severity;
    String description;
    String occurredDate;

    // POJO
    public Problem() {
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOccurredDate() {
        return occurredDate;
    }

    public void setOccurredDate(String occurredDate) {
        this.occurredDate = occurredDate;
    }
}

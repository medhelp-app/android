package com.medhelp.medhelp.model;

import java.util.List;

public class BodyPart {

    private String part;
    private List<Problem> problems;

    // POJO
    public BodyPart() {
    }

    public BodyPart(String part, List<Problem> problems) {
        this.part = part;
        this.problems = problems;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public List<Problem> getProblems() {
        return problems;
    }

    public void setProblems(List<Problem> problems) {
        this.problems = problems;
    }
}

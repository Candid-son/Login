package com.example.login;

public class Report {
    private final int challenge;
    private final String recommendation;
    private final String classId;
    private final String moduleId;
    private final String weekId;

    public Report(int challenge, String recommendation, String classId, String moduleId, String weekId) {
        this.challenge = challenge;
        this.recommendation = recommendation;
        this.classId = classId;
        this.moduleId = moduleId;
        this.weekId = weekId;
    }

    // Getter methods
    public int getChallenge() {
        return challenge;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public String getClassId() {
        return classId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public String getWeekId() {
        return weekId;
    }

    @Override
    public String toString() {
        return "Report{" +
                "challenge=" + challenge +
                ", recommendation='" + recommendation + '\'' +
                ", classId='" + classId + '\'' +
                ", moduleId='" + moduleId + '\'' +
                ", weekId='" + weekId + '\'' +
                '}';
    }
}

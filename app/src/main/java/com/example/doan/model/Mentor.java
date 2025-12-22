package com.example.doan.model;

import java.io.Serializable;

public class Mentor implements Serializable {
    private int id;
    private String name;
    private String job_title;
    private String company;
    private String education;
    private String achievements;
    private String email;
    private String image_url;

    public Mentor() { }

    public String getName() { return name; }
    public String getJobTitle() { return job_title; }
    public String getCompany() { return company; }
    public String getEducation() { return education; }
    public String getAchievements() { return achievements; }
    public String getEmail() { return email; }
    public String getImageUrl() { return image_url; }
}
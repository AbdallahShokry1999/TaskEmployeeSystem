package org.example.model;

import javax.persistence.*;

@Entity
@Table(name = "projects")
public class Projects {
    @Id
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "project_name", unique = true)
    private String projectName;

    // Getters and setters
    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}

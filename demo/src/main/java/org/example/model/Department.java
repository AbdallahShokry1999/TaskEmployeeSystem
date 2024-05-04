package org.example.model;

import javax.persistence.*;

@Entity

@Table(name = "department")
public class Department {
    @Id
    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "department_name", unique = true)
    private String departmentName;

    // Getters and setters
    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}

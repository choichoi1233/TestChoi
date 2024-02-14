package com.example.choitest1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@Table(name = "TB_STUDY")
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long studySeq;

    private long schoolSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proUserSeq")
    @JsonBackReference(value = "proUser-backref")
    private User proUser; //하나의 교수님은 여러 수업을 만들수있음

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "study_user",
            joinColumns = @JoinColumn(name = "studySeq"),
            inverseJoinColumns = @JoinColumn(name = "userSeq"))
    @JsonBackReference(value = "studentUsers-backref")
    private List<User> studentUsers; // 여러 수업은 여러 학생과 연결됨

    private String studyNm; //강의명

    private long maxstudentCnt; //최대 학생 인원수

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private Timestamp updatedAt;

    @Column(name = "use_yn")
    private String useYn;

    @PrePersist
    protected void onCreate() {
        if (useYn == null) useYn = "Y";
        updatedAt = createdAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }


}

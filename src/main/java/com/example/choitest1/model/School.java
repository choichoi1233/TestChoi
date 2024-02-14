package com.example.choitest1.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "TB_SCHOOL")
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long schoolSeq;

    private String schoolNm;


    private String applyStartDt; //수강 신청 시작일
    private String applyEndDt;   //수강 신청 종료일


    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    private String createUserId; // 데이터 생성자


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

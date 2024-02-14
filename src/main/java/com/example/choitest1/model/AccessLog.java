package com.example.choitest1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "TB_ACCESS LOG")
@AllArgsConstructor
@Builder
public class AccessLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long logId;

    private long userSeq;

    private long schoolSeq;

    private Timestamp createdAt;
    private Timestamp expiredAt;

    @Column(nullable = false)
    private Timestamp updatedAt;

    public AccessLog() {

    }


    public AccessLog(long userSeq, long schoolSeq) {
        this.userSeq = userSeq;
        this.schoolSeq = schoolSeq;
    }

    @PrePersist
    protected void onCreate() {
        updatedAt = createdAt = new Timestamp(System.currentTimeMillis());
        expiredAt = new Timestamp(System.currentTimeMillis() + Constants.USER_ACCESS_TOKEN_TIME);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}

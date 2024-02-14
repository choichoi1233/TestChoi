package com.example.choitest1.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "TB_USER")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userSeq;

    private long schoolSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schoolSeq", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private School school;

    private String userId;

    private String userPwd;

    private String role;

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private Timestamp updatedAt;

    @Column(name = "use_yn")
    private String useYn;

    private String stateCd; // 1:미승인 2:승인

    @OneToMany(mappedBy = "proUser", fetch = FetchType.LAZY)
    @JsonManagedReference(value = "proUser-backref")
    private List<Study> studies; //하나의 학생이 여러 수업을 들을수도있고, 하나의 교수가 여러 수업을 만들수있기에


    @PrePersist
    protected void onCreate() {
        if (useYn == null) useYn = "Y";
        updatedAt = createdAt = new Timestamp(System.currentTimeMillis());
        stateCd = (Objects.equals(role, Constants.USER_ROLE_PRO) ? "2" : "1");
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "User{" +
                "userSeq=" + userSeq +
                ", schoolSeq=" + schoolSeq +
                ", userId='" + userId + '\'' +
                ", userPwd='" + userPwd + '\'' +
                ", role='" + role + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", useYn='" + useYn + '\'' +
                ", stateCd='" + stateCd + '\'' +
                '}';
    }


}

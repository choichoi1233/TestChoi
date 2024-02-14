package com.example.choitest1.repo;

import com.example.choitest1.model.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccessRepo extends JpaRepository<AccessLog, Long> {

    Optional<AccessLog> findByUserSeqAndSchoolSeq(long userSeq, long schoolSeq);

    /**
     * 본인 제외한, 학교에 로그인한 다른 쿠키들 조회. 쿠키의 종료 시각을 확인
     *
     * @param userSeq
     * @param schoolSeq
     * @param timestamp
     * @return
     */
    List<AccessLog> findByUserSeqNotAndSchoolSeqAndExpiredAtAfter(long userSeq, long schoolSeq, Timestamp timestamp);
}

package com.example.choitest1.repo;

import com.example.choitest1.model.WaitingQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WaitingQueueRepo extends JpaRepository<WaitingQueue, Long> {

    // 학교별로 대기열에서 가장 앞에 있는 학생을 찾습니다.
    @Query("select w from WaitingQueue w where w.schoolSeq = ?1 order by w.position asc")
    Optional<WaitingQueue> findFirstInQueueBySchoolSeq(long schoolSeq);

    @Query("select w from WaitingQueue w where w.schoolSeq = ?1 and w.userSeq = ?2 order by w.position asc")
    Optional<WaitingQueue> findFirstInQueueBySchoolSeqAndUserSeq(long schoolSeq, long userSeq);


    // 학교별로 대기열의 크기를 구합니다.
    @Query("select count(w) from WaitingQueue w where w.schoolSeq = ?1")
    int countBySchoolSeq(long schoolSeq);


    // 학교별로 특정 학생의 대기열 위치를 찾습니다.
    @Query("select w.position from WaitingQueue w where w.schoolSeq = ?1 and w.userSeq = ?2")
    Optional<Integer> findPositionBySchoolSeqAndUserSeq(long schoolSeq, long userSeq);
}
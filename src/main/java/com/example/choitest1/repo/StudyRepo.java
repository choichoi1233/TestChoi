package com.example.choitest1.repo;

import com.example.choitest1.model.Study;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyRepo extends JpaRepository<Study, Long> {

    @EntityGraph(attributePaths = {"proUser", "studentUsers"})
    Collection<Study> findByProUserUserSeq(long userSeq);

    Optional<Study> findByStudySeq(long studySeq);

    @Query("select s from Study s join s.studentUsers u where u.userSeq = :userSeq")
    Collection<Study> findStudiesByStudentUserSeq(@Param("userSeq") long userSeq);

    Optional<Study> findByStudySeqAndSchoolSeq(long studySeq, long schoolSeq);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Study s WHERE s.studySeq = :studySeq AND s.schoolSeq = :schoolSeq")
    Optional<Study> findByStudySeqAndSchoolSeqForUpdate(@Param("studySeq") long studySeq, @Param("schoolSeq") long schoolSeq);

    @Query("select s from Study s " +
            "where s.proUser.school.schoolSeq = ?1 " +
            "and s.proUser.role = 'PRO' " +
            "and ?2 between s.proUser.school.applyStartDt and s.proUser.school.applyEndDt")
    List<Study> findAvailableStudiesBySchoolSeq(long schoolSeq, String currentDate);
}

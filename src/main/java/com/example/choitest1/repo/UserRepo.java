package com.example.choitest1.repo;

import com.example.choitest1.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE) //비관적 잠금 로그인 시 최초로 조회할때 사용
    @Query("select u from User u where u.userId = ?1 and u.userPwd = ?2 and u.useYn = ?3 and u.role = ?4 and u.schoolSeq = ?5")
    Optional<User> findByUserIdAndUserPwdAndUseYnAndRoleAndSchoolSeqForUpdate(String userId, String userPwd, String y, String s, long schoolSeq);

    @Lock(LockModeType.PESSIMISTIC_WRITE) //비관적 잠금 회원가입 시 최초로 조회할때 사용
    @Query("select u from User u where u.schoolSeq = ?1 and u.role = ?2")
    Collection<Object> findAllBySchoolSeqAndRoleForUpdate(long schoolSeq, String role);


    Optional<User> findByUserId(String userId);

    Optional<User> findByUserIdAndRole(String userId, String role);

    List<User> findByRole(String role);
}

package com.example.choitest1.service;

import com.example.choitest1.model.*;
import com.example.choitest1.repo.AccessRepo;
import com.example.choitest1.repo.SchoolRepo;
import com.example.choitest1.repo.UserRepo;
import com.example.choitest1.repo.WaitingQueueRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class StudentService {

    private final UserRepo userRepo;

    private final SchoolRepo schoolRepo;

    private final AccessRepo acessRepo;

    private final WaitingQueueRepo waitingRepo;

    /**
     * 학생 로그인시 유저 정보 찾기 메소드
     * 아이디 , 패스워드 , 사용여부, 역활, 학교번호 로 유저를 찾음.
     * findByUserIdAndUserPwdAndUseYnAndRoleAndSchoolSeqForUpdate 로 비관적 잠금 수행.
     * 교수인경우 승인여부까지 판단
     * 학생의 경우 로그인 시, 로그인 및 엑세스 기간 만료를 설정.
     * 엑세스 로그 테이블에서 현재 접속한 유저 정보를 확인 가능.
     * 로그아웃시 바로 기록 삭제
     *
     * @param user
     * @return
     */
    @Transactional
    public User GetUserInfoByLogin(User user) {
        user.setUserPwd(PasswordEncryptionService.encrypt(user.getUserPwd()));//암호화
        user = userRepo.findByUserIdAndUserPwdAndUseYnAndRoleAndSchoolSeqForUpdate(user.getUserId(), user.getUserPwd(), "Y", user.getRole(), user.getSchoolSeq()).orElseThrow(() -> new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "유저 정보를 확인해주세요.")); //비관적 잠금

        switch (user.getRole()) {
            case Constants.USER_RULE_PRO:
                //미승인 교수 예외 처리
                if (user.getStateCd().equals("2"))
                    throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "승인되지 않은 교수님입니다.");
                break;
            case Constants.USER_RULE_STUDENT:
                List<AccessLog> temp = acessRepo.findByUserSeqNotAndSchoolSeqAndExpiredAtAfter(user.getUserSeq(), user.getSchoolSeq(), new Timestamp(System.currentTimeMillis()));
                if (temp.size() >= Constants.LIMIT_STUDENT_ACCESS) {
                    //해당학교에 본인 제외하고 10명 넘게 이미 로그인 완료를 했다.
                    throw new WaitingException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "");
                }
                AccessLog templog = acessRepo.findByUserSeqAndSchoolSeq(user.getUserSeq(), user.getSchoolSeq()).orElse(new AccessLog(user.getUserSeq(), user.getSchoolSeq()));
                acessRepo.save(templog);
                break;
        }
        return user;
    }

    public List<User> userList(User user) {
        return StringUtils.hasText(user.getRole()) ? userRepo.findByRole(user.getRole()) : userRepo.findAll();
    }

    /**
     * 대기열 수정
     * 이미 대기열이 있다면 대기열 순서를 가져옴.
     * 아니면 가장 마지막 대기열을 가져와 맨뒤에 줄을 선다.
     *
     * @param user
     * @return
     */
    @Transactional
    public String waitingIns(User user) {
        user = userRepo.findByUserId(user.getUserId()).get();
        Optional<WaitingQueue> temp = waitingRepo.findFirstInQueueBySchoolSeqAndUserSeq(user.getSchoolSeq(), user.getUserSeq());
        if (temp.isEmpty()) {
            int queueSize = waitingRepo.countBySchoolSeq(user.getSchoolSeq());
            WaitingQueue waitingQueue = new WaitingQueue();
            waitingQueue.setSchoolSeq(user.getSchoolSeq());
            waitingQueue.setUserSeq(user.getUserSeq());
            waitingQueue.setPosition(queueSize + 1);
            waitingRepo.save(waitingQueue);
            return String.valueOf(queueSize + 1);
        } else {
            return String.valueOf(temp.get().getPosition());
        }
    }
}

package com.example.choitest1.service;

import com.example.choitest1.model.Constants;
import com.example.choitest1.model.CustomException;
import com.example.choitest1.model.PasswordEncryptionService;
import com.example.choitest1.model.User;
import com.example.choitest1.repo.SchoolRepo;
import com.example.choitest1.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Log4j2
public class SharedService {
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";


    private final UserRepo userRepo;

    private final SchoolRepo schoolRepo;

    /**
     * 학생 정보 저장 메소드
     * 1. 학생  rule인지 확인.
     * 2. 데이터 유효성 확인. (공통 유저 정보 체크 메소드 사용)
     * 3. 해당 학교가 존재하는지, 본인의 포지션에 맞는 최대 정원이 넘었는지 , 학교당 30명이 넘었는지 등등
     * 학생의 rule은 S인데 S가 아니라면 FE에서 제대로 데이터를 입력하지 않았거나, 중간에 API 요청중 데이터 오염이 일어 난 것임.
     *
     * @param user
     * @return
     */
    @Transactional
    public User studentIns(User user) {


        if (!StringUtils.hasText(user.getRole()) || !user.getRole().equals(Constants.USER_ROLE_STUDENT))
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "학생 유효성 데이터가 입력되지 않았습니다.");
        userCommoninfoCheck(user);
        //회원가입 직전 비관적잠금으로 아래 매소드에서 잠깐 막음
        if (userRepo.findAllBySchoolSeqAndRoleForUpdate(user.getSchoolSeq(), user.getRole()).size() >= Constants.LIMIT_STUDENT_PER_SCHOOL)
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "학교 정원 " + Constants.LIMIT_STUDENT_PER_SCHOOL + "명이 넘었습니다.");


        user.setUserPwd(PasswordEncryptionService.encrypt(user.getUserPwd()));//암호화
        return userRepo.save(user);
    }

    /**
     * 교수 정보 저장 메소드
     * 1. 교수의 rule인지확인 (P)
     * 2. 데이터 유효성 확인. (공통 유저 정보 체크 메소드 사용)
     * 3. 해당 학교가 존재하는지, 본인의 포지션에 맞는 최대 정원이 넘었는지 체크
     *
     * @param user
     * @return
     */
    @Transactional
    public User proIns(User user) {
        if (!StringUtils.hasText(user.getRole()) || !user.getRole().equals(Constants.USER_ROLE_PRO))
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "교수님 유효성 데이터가 입력되지 않았습니다.");

        userCommoninfoCheck(user);

        //회원가입 직전 비관적잠금으로 아래 매소드에서 잠깐 막음
        if (userRepo.findAllBySchoolSeqAndRoleForUpdate(user.getSchoolSeq(), user.getRole()).size() >= Constants.LIMIT_STUDENT_PER_SCHOOL)
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "학교 정원 " + Constants.LIMIT_STUDENT_PER_SCHOOL + "명이 넘었습니다.");

        user.setUserPwd(PasswordEncryptionService.encrypt(user.getUserPwd()));//암호화
        return userRepo.save(user);
    }


    /**
     * 관리자, 학생, 교수등등 User 클래스 입력시의 유효성 체크 공통 메소드
     *
     * @param user
     */
    private void userCommoninfoCheck(User user) {
        if (!StringUtils.hasText(user.getUserId()))
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "유저 ID가 입력되지 않았습니다.");
        if (!mailFormatCheck(user.getUserId()))
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "유저 ID가 이메일포맷인지 확인해주세요.");
        if (!userRepo.findByUserId(user.getUserId()).isEmpty())
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "유저 ID가 중복되었습니다.");
        if (!StringUtils.hasText(user.getUserPwd()))
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "유저 PWD가 입력되지 않았습니다.");
        if (user.getSchoolSeq() == 0)
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "학교 정보가 입력되지 않았습니다.");
        if (schoolRepo.findAllBySchoolSeq(user.getSchoolSeq()) == null)
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "학교정보가 존재하지않습니다.");
    }

    //메일 패턴 체크
    private Boolean mailFormatCheck(String str) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}

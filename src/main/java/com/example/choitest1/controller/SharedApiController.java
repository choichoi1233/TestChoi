package com.example.choitest1.controller;

import com.example.choitest1.model.*;
import com.example.choitest1.service.SharedService;
import com.example.choitest1.service.StudentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 권한 체크없이 누구나 이용이 가능한 api 목록
 * 주로 비회원이 회원가입을 요청하거나,
 * 누구나 접근이 가능한 API들만 모아둠
 * 로그인,회원가입,공지사항 조회 등등...
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SharedApiController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final SharedService sharedService;

    private final StudentService studentService;
    private final JwtService userJwtService = new JwtService();

    /**
     * 학생의 회원 가입
     *
     * @param user
     * @return
     */
    @RequestMapping("/studentInsProc")
    public ResponseEntity<ResultVo<User>> userIns(@RequestBody User user) {
        log.info("{}", user);
        ResultVo<User> result = null;
        try {
            user = sharedService.studentIns(user);
            user.setUserPwd(null);
            result = new ResultVo<>("", "S", user);
        } catch (CustomException e) {
            result = new ResultVo<>(e.getLocalizedMessage(), "F", null);
            return new ResponseEntity<ResultVo<User>>(result, HttpStatus.OK);
        }
        return new ResponseEntity<ResultVo<User>>(result, HttpStatus.CREATED);
    }

    /**
     * 관리자,교수,학생등 모든 최종 유저가 로그인이후 , 엑세스 토큰을 반환받음.
     * userJwtService의 createToken에 user클래스를 넣어 데이터를 직렬화시키고 String으로 받아 토큰으로 바꿈
     * 이후 특정 유저가 필요한 API의 경우 HEADER에 토큰을 넣어서 어느 누가 요청한 API인지 체크 가능.
     *
     * @param user
     * @return
     */
    @RequestMapping("/userLogin")
    public ResponseEntity<ResultVo<String>> userLogin(@RequestBody User user) {
        log.info("{}", user);
        ResultVo<String> result = null;
        try {
            user = studentService.GetUserInfoByLogin(user);
            result = new ResultVo<String>("", "S", userJwtService.createToken(userJwtService.TransformUserToStr(user), Constants.USER_ACCESS_TOKEN_TIME));
        } catch (CustomException e) {
            result = new ResultVo<String>(e.getLocalizedMessage(), "F", null);
            return new ResponseEntity<ResultVo<String>>(result, HttpStatus.OK);
        } catch (JsonProcessingException e2) {
            result = new ResultVo<String>("데이터 암호화중 에러가 발생했습니다.", "F", null);
            return new ResponseEntity<ResultVo<String>>(result, HttpStatus.OK);
        } catch (WaitingException e3) {
            result = new ResultVo<String>("현재 학교에 대기가 있습니다 [" + studentService.waitingIns(user) + "]명", "F", null);
            return new ResponseEntity<ResultVo<String>>(result, HttpStatus.OK);
        }
        return new ResponseEntity<ResultVo<String>>(result, HttpStatus.OK);
    }

    /**
     * 교수 정보 입력. 교수 회원 가입
     *
     * @param user
     * @return
     */
    @RequestMapping("/proInsProc")
    public ResponseEntity<ResultVo<User>> proIns(@RequestBody User user) {
        log.info("{}", user);
        ResultVo<User> result = null;
        try {
            user = sharedService.proIns(user);
            user.setUserPwd(null);
            result = new ResultVo<User>("", "S", user);
        } catch (CustomException e) {
            result = new ResultVo<User>(e.getLocalizedMessage(), "F", null);
            return new ResponseEntity<ResultVo<User>>(result, HttpStatus.OK);
        }
        return new ResponseEntity<ResultVo<User>>(result, HttpStatus.CREATED);
    }


}

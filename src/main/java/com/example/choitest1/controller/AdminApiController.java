package com.example.choitest1.controller;

import com.example.choitest1.model.*;
import com.example.choitest1.service.SchoolService;
import com.example.choitest1.service.StudentService;
import com.example.choitest1.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 관리자 전용 API 컨트롤러.
 * "/api/adm/**" API 요청시 interface에서 관리자 인지 권한 체크 후 api 통신 가능
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/adm")
public class AdminApiController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final SchoolService schoolService;
    private final StudentService studentService;
    private final StudyService studyService;


    /**
     * 학교 생성 API
     *
     * @param school
     * @param user
     * @return
     */
    @PostMapping("/insschoolproc")
    public ResponseEntity<ResultVo<School>> insschoolproc(@RequestBody School school, @RequestAttribute("user") User user) {
        ResultVo<School> result = null;
        school.setCreateUserId(user.getUserId());
        school = schoolService.insSchoolProc(school);
        result = new ResultVo<School>("", "S", school);
        return new ResponseEntity<ResultVo<School>>(result, HttpStatus.CREATED);
    }

    /**
     * 2. 교수님의 사용승인 수정
     * user 데이터중 useYn 데이터를 받아서 교수님의 필드 수정.
     * 교수님은 로그인시 useYn값까지 확인함.
     *
     * @param user
     * @return
     */
    @PostMapping("/changeProStatus")
    public ResponseEntity<ResultVo<String>> changeProStatus(@RequestBody User user) {
        ResultVo<String> result = null;
        schoolService.changeProStatus(user);
        result = new ResultVo<String>("", "S", "정상적으로 처리되었습니다.");
        return new ResponseEntity<ResultVo<String>>(result, HttpStatus.CREATED);
    }

    /**
     * 학교 수강신청 기간 수정 메소드
     *
     * @param school
     * @return
     * @param에 학교의 수강신청시작 ~ 수강신청종료 일 지정
     */
    @PostMapping("/changeSchoolApplyDt")
    public ResponseEntity<ResultVo<String>> changeSchoolApplyDt(@RequestBody School school) {
        ResultVo<String> result = null;
        schoolService.changeSchoolApplyDt(school);
        result = new ResultVo<String>("", "S", "정상적으로 처리되었습니다.");
        return new ResponseEntity<ResultVo<String>>(result, HttpStatus.CREATED);
    }

    /**
     * 관리자는 등록된
     * 학교를 검색할수있음.
     *
     * @return
     */
    @PostMapping("/schoolList")
    public ResponseEntity<ResultVo<List<School>>> schoolList() {
        ResultVo<List<School>> result = null;
        result = new ResultVo<List<School>>("", "S", schoolService.schoolList());
        return new ResponseEntity<ResultVo<List<School>>>(result, HttpStatus.CREATED);
    }

    /**
     * 관리자는 등록된
     * 교수 및 학생을 검색할수있음.
     * user의 role에 의해 교수 혹은 학생을 선택 조회함
     * 공백 일시 모두 조회함
     *
     * @return
     */
    @PostMapping("/userList")
    public ResponseEntity<ResultVo<List<User>>> userList(@RequestBody User user) {
        ResultVo<List<User>> result = null;
        result = new ResultVo<List<User>>("", "S", studentService.userList(user));
        return new ResponseEntity<ResultVo<List<User>>>(result, HttpStatus.CREATED);
    }

    /**
     * 관리자는 등록된 모든 수업을조회할수있어야함.
     *
     * @return
     */
    @PostMapping("/studyList")
    public ResponseEntity<ResultVo<List<Study>>> studyList() {
        ResultVo<List<Study>> result = null;
        result = new ResultVo<List<Study>>("", "S", studyService.studyList());
        return new ResponseEntity<ResultVo<List<Study>>>(result, HttpStatus.CREATED);
    }
}


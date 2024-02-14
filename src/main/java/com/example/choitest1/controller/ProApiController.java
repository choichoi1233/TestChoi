package com.example.choitest1.controller;

import com.example.choitest1.model.CustomException;
import com.example.choitest1.model.ResultVo;
import com.example.choitest1.model.Study;
import com.example.choitest1.model.User;
import com.example.choitest1.service.SchoolService;
import com.example.choitest1.service.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 교수 전용 API 컨트롤러.
 * "/api/adm/**" API 요청시 interface에서 교수 인지 권한 체크 후 api 통신 가능
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pro")
@Log4j2
public class ProApiController {

    private final SchoolService schoolService;
    private final StudyService studyService;

    /**
     * 1. 교수의 수업 생성 api
     *
     * @param study
     * @param user
     * @return
     */
    @PostMapping("/insStudyproc")
    public ResponseEntity<ResultVo<Study>> insStudyproc(@RequestBody Study study, @RequestAttribute("user") User user) {
        log.info("{}", study);
        ResultVo<Study> result = null;
        result = new ResultVo<Study>("", "S", studyService.insStudyproc(study, user));
        return new ResponseEntity<ResultVo<Study>>(result, HttpStatus.CREATED);
    }

    /**
     * 교수의 본인의 강의에 수강 신청한 학생들 목록 조회
     *
     * @param user
     * @return
     */
    @PostMapping("/studentList")
    public ResponseEntity<ResultVo<List<User>>> studentList(@RequestAttribute("user") User user) {
        log.info("{}", user);
        ResultVo<List<User>> result = null;
        List<Study> studies = studyService.findByProUserUserSeq(user);
        List<User> students = new ArrayList<>();
        for (Study study : studies) {
            students.addAll(study.getStudentUsers());
        }
        result = new ResultVo<List<User>>("", "S", students);
        return new ResponseEntity<ResultVo<List<User>>>(result, HttpStatus.OK);
    }

    /**
     * 교수가 본인이 개설한 수업 리스트를 조회한다.
     *
     * @param user
     * @return
     */
    @PostMapping("/studyList")
    public ResponseEntity<ResultVo<List<Study>>> studyList(@RequestAttribute("user") User user) {
        log.info("{}", user);
        ResultVo<List<Study>> result = null;
        result = new ResultVo<List<Study>>("", "S", studyService.getStudyList(user));
        return new ResponseEntity<ResultVo<List<Study>>>(result, HttpStatus.OK);
    }
}

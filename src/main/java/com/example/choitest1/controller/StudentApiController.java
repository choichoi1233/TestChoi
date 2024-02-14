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

import java.util.List;


/**
 * 학생 전용 API 컨트롤러.
 * 수강 신청 & 철회 & 본인이 수강 신청한 수업 목록 조회 가능
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student")
@Log4j2
public class StudentApiController {
    private final SchoolService schoolService;
    private final StudyService studyService;

    /**
     * 학생이 수업을 신청하는 컨트롤러
     *
     * @param study
     * @param user
     * @return
     */
    @PostMapping("/insStudyApplyProc")
    public ResponseEntity<ResultVo<String>> insStudyApplyProc(@RequestBody Study study, @RequestAttribute("user") User user) {
        log.info("{}", study);
        ResultVo<String> result = null;
        studyService.insStudyApplyProc(study, user);
        result = new ResultVo<String>("", "S", "정상적으로 처리되었습니다.");
        return new ResponseEntity<ResultVo<String>>(result, HttpStatus.CREATED);
    }

    /**
     * 학생이 본인이 신청한 수업을 취소하는 api
     *
     * @param study
     * @param user
     * @return
     */
    @PostMapping("/insStudyApplyCancelProc")
    public ResponseEntity<ResultVo<String>> insStudyApplyCancelProc(@RequestBody Study study, @RequestAttribute("user") User user) {
        log.info("{}", study);
        ResultVo<String> result = null;
        studyService.insStudyApplyCancelProc(study, user);
        result = new ResultVo<String>("", "S", "정상적으로 처리되었습니다.");
        return new ResponseEntity<ResultVo<String>>(result, HttpStatus.CREATED);
    }

    /**
     * 학생 본인이 신청한 수업 리스트
     *
     * @param user 를 통해 본인 인증이 완료된 학생이 바로 본인의 seq를 사용해서 강의 조회
     * @param user
     * @return
     */
    @PostMapping("/myStudyList")
    public ResponseEntity<ResultVo<List<Study>>> myStudyList(@RequestAttribute("user") User user) {
        log.info("{}", user);
        ResultVo<List<Study>> result = null;
        result = new ResultVo<List<Study>>("", "S", studyService.GetMyStudyList(user));
        return new ResponseEntity<ResultVo<List<Study>>>(result, HttpStatus.CREATED);
    }

    /**
     * 학생이 본인 학교에서 진행중인 수업들을 조회.
     * 신청이 가능한 수업들 조회
     * 수강인원에 의해서 조회안되는 경우는 제외.
     * 대기열을 넣어야하기때문
     *
     * @param user
     * @return
     */
    @PostMapping("/studyList")
    public ResponseEntity<ResultVo<List<Study>>> studyList(@RequestAttribute("user") User user) {
        log.info("{}", user);
        ResultVo<List<Study>> result = null;
        result = new ResultVo<List<Study>>("", "S", studyService.GetSchoolStudyList(user));
        return new ResponseEntity<ResultVo<List<Study>>>(result, HttpStatus.CREATED);
    }


}

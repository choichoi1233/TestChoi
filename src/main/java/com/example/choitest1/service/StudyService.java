package com.example.choitest1.service;

import com.example.choitest1.model.*;
import com.example.choitest1.repo.SchoolRepo;
import com.example.choitest1.repo.StudyRepo;
import com.example.choitest1.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class StudyService {

    private final SchoolRepo schoolRepo;
    private final UserRepo userRepo;
    private final StudyRepo studyRepo;

    /**
     * 교수의 강의 등록
     * 본인의 학교가 현재 수업을 등록할수 있는지,
     * 본인이 수업을 등록할수있는 상황인지 등등 조건 확인
     * 강의의 수강인원을 제한할 수 있기 때문에 FE에서 maxstudentCnt값을 받아온다.
     *
     * @param study
     * @param user
     * @return
     */
    @Transactional
    public Study insStudyproc(Study study, User user) {
        //해당 학교의 수강 신청기간 - 2주 부터 강의 생성가능.
        School school = schoolRepo.findAllBySchoolSeq(user.getSchoolSeq());

        if (!StringUtils.hasText(school.getApplyStartDt()) || !isVaildDate(school.getApplyStartDt(), school.getApplyEndDt())) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "강의 등록 기간이 아닙니다.");
        }

        if (studyRepo.findByProUserUserSeq(user.getUserSeq()).size() >= 2)
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "강의를 동시에 두 개 까지 개설할 수 있습니다.");

        //제한인원은 30명을 초과할 수 없습니다.
        study.setMaxstudentCnt(study.getMaxstudentCnt() > Constants.MAX_LIMIT_STUDENT_PER_STUDENT ? Constants.MAX_LIMIT_STUDENT_PER_STUDENT : study.getMaxstudentCnt());
        study.setSchoolSeq(user.getSchoolSeq());
        study.setProUser(user);
        return studyRepo.save(study);
    }

    private boolean isVaildDate(String inputDateStr, String inputDateStr2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate inputDate = LocalDate.parse(inputDateStr, formatter);
        LocalDate inputDate2 = LocalDate.parse(inputDateStr2, formatter);
        LocalDate twoWeeksBefore = inputDate.minusWeeks(2);
        LocalDate today = LocalDate.now();
        return today.isAfter(twoWeeksBefore) && today.isBefore(inputDate2);
    }

    /**
     * 교수들이 본인 수업에 신청한 학생 리스트 조회.
     *
     * @param user
     * @return
     */
    public List<Study> findByProUserUserSeq(User user) {

        //교수는 수강신청 기간 이후에 수강을 신청한 학생들을 열람할 수 있어야 합니다.
        School school = schoolRepo.findAllBySchoolSeq(user.getSchoolSeq());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate inputDate = LocalDate.parse(school.getApplyEndDt(), formatter);
        LocalDate today = LocalDate.now();
        if (today.isBefore(inputDate) || today.equals(inputDate))
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "수강신청 기간 이후에 학생들을 열람할수있습니다.");

        return (List<Study>) studyRepo.findByProUserUserSeq(user.getUserSeq());
    }

    /**
     * 교수는 본인의 수업 리스트를 조회할수있다.
     *
     * @param user
     * @return
     */
    public List<Study> getStudyList(User user) {
        return (List<Study>) studyRepo.findByProUserUserSeq(user.getUserSeq());
    }

    /**
     * 학생의 수강 신청 메소드
     * 1. 해당 강의가 정상강의인지 확인 (여기서 비관적 잠금 동시성 제어)
     * 2. 해당 학교가 현재 수강신청 기간인지.
     * 3. 학생이 수업 3개를 듣고 있는지 확인
     * 4. 혹시 이미 수강하고 있는 데이터인지 확인
     *
     * @param study
     * @param user
     */
    @Transactional
    public void insStudyApplyProc(Study study, User user) {

        //1. 해당 강의가 정상강의인지 확인 (여기서 비관적 잠금 동시성 제어)
        study = studyRepo.findByStudySeqAndSchoolSeqForUpdate(study.getStudySeq(), user.getSchoolSeq()).orElseThrow(() -> new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "정상적인 수업데이터가 아닙니다."));

        //2. 해당 학교가 현재 수강신청 기간인지.
        School school = schoolRepo.findAllBySchoolSeq(user.getSchoolSeq());
        if (!StringUtils.hasText(school.getApplyStartDt()) || !StringUtils.hasText(school.getApplyEndDt()) || !isTodayWithin(school.getApplyStartDt(), school.getApplyEndDt()))
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "학교는 현재 수강신청 기간이 아닙니다.");

        //3. 학생이 수업 3개를 듣고 있는지 확인
        List<Study> studies = (List<Study>) studyRepo.findStudiesByStudentUserSeq(user.getUserSeq());
        if (studies.size() >= Constants.LIMIT_STUDENT_PER_STUDY)
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "동시에 3개까지 수강할 수 있습니다..");

        //4. 혹시 이미 수강하고 있는 데이터인지 확인
        for (Study item : studies) {
            if (item.getStudySeq() == study.getStudySeq())
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "이미 수강 신청한 강의입니다.");
        }

        study.getStudentUsers().add(user);
        studyRepo.save(study);
    }

    private boolean isTodayWithin(String date1, String date2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(date1, formatter);
        LocalDate endDate = LocalDate.parse(date2, formatter);
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public List<Study> GetMyStudyList(User user) {
        return (List<Study>) studyRepo.findStudiesByStudentUserSeq(user.getUserSeq());
    }

    /**
     * 학생이 본인이 신청한 수업을 취소.
     * 취소 가능한 수업인이 확인하고 삭제 처리
     *
     * @param study
     * @param user
     */
    @Transactional
    public void insStudyApplyCancelProc(Study study, User user) {
        study = studyRepo.findById(study.getStudySeq()).orElseThrow(() -> new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "존재하지 않는 수업입니다.."));

        if (!study.getStudentUsers().contains(user))
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "해당 수업은 신청중이 아닙니다.");

        study.getStudentUsers().remove(user);
        studyRepo.save(study);
    }

    /**
     * 본인의 학교에 맵핑된 수업들을 조회함
     * 학교가 현재 수강 신청 가능기간인지 체크
     *
     * @param user
     * @return
     */
    public List<Study> GetSchoolStudyList(User user) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return studyRepo.findAvailableStudiesBySchoolSeq(user.getSchoolSeq(), currentDate);
    }

    public List<Study> studyList() {
        return studyRepo.findAll();
    }
}

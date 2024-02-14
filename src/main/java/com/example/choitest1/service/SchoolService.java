package com.example.choitest1.service;

import com.example.choitest1.model.Constants;
import com.example.choitest1.model.CustomException;
import com.example.choitest1.model.School;
import com.example.choitest1.model.User;
import com.example.choitest1.repo.SchoolRepo;
import com.example.choitest1.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Log4j2
public class SchoolService {

    private final SchoolRepo schoolRepo;
    private final UserRepo userRepo;

    /**
     * 학교 생성 메소드
     * 1. 학교 생성에 필요한 데이터 유효성 체크
     *
     * @param school
     * @return
     */
    public School insSchoolProc(School school) {
        //학교 유효성 체크
        if (!StringUtils.hasText(school.getSchoolNm()))
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "학교 이름을 입력해주세요.");

        return schoolRepo.save(school);
    }

    /**
     * 교수의 사용 여부 전환 메소드
     * 교수님의 정보가 맞는지 체크 이후,
     * 교수님의 정보가 맞는경우에 FE에서 온 statusCd값으로 수정처리
     *
     * @param user
     * @return
     */
    public User changeProStatus(User user) {
        if (user.getUserId() == null || user.getStateCd() == null)
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "필수 데이터가 없습니다.");

        String targetStatus = user.getStateCd();
        user = userRepo.findByUserIdAndRole(user.getUserId(), Constants.USER_RULE_PRO).orElseThrow(() -> new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "교수님 정보가 없습니다."));

        user.setStateCd(targetStatus);
        return userRepo.save(user);
    }

    /**
     * 3.학교의 수강 신청및 종료일 셋팅
     * 학교데이터 존재 유효성 체크 및 FE에서 온 데이터 검증
     * 그 이외 추가로 최소 2주 ~ 최대 4주의 수강신청 기간을 등록 가능하고,
     * 관리자는 수강신청 기간을 변경하거나 삭제할 수 있음. (공백을 넣으면)
     *
     * @param school
     */
    @Transactional
    public void changeSchoolApplyDt(School school) {
        if (school.getSchoolSeq() == 0)
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "학교 데이터가 없습니다.");

        if (!isValidFormat("yyyy-MM-dd", school.getApplyStartDt()) && !isValidFormat("yyyy-MM-dd", school.getApplyEndDt()))
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "수강신청일 변경의 데이터 포맷이 다릅니다. [yyyy-MM-dd] 포맷을 확인해주세요.");

        String startDt = school.getApplyStartDt();
        String endDt = school.getApplyEndDt();
        school = schoolRepo.findAllBySchoolSeq(school.getSchoolSeq());

        if (!Objects.equals(startDt, "") && !Objects.equals(endDt, "")) {
            if (school == null)
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "학교데이터가 존재하지 않습니다.");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date1 = LocalDate.parse(startDt, formatter);
            LocalDate date2 = LocalDate.parse(endDt, formatter);

            long daysBetween = ChronoUnit.DAYS.between(date1, date2);
            daysBetween = Math.abs(daysBetween); // 날짜 순서에 상관없이 차이를 계산.

            if (13 >= daysBetween || daysBetween >= 29) {
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "수강 신청일은 최소 2주에서 4주까지 차이가 나야합니다.");
            }

            //데이터 앞과 뒤가 바뀌였을 경우 바꿔서 넣어준다.
            if (date1.isAfter(date2)) {
                String temp = startDt;
                startDt = endDt;
                endDt = temp;
            }
        }

        school.setApplyStartDt(startDt);
        school.setApplyEndDt(endDt);
        schoolRepo.save(school);
    }

    private boolean isValidFormat(String format, String value) {
        if (!StringUtils.hasText(value)) return true; //공백 데이터도 등록가능함.

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        try {
            sdf.parse(value);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * 아무 조건 없이 전체다 조회
     *
     * @return
     */
    public List<School> schoolList() {
        return schoolRepo.findAll();
    }
}

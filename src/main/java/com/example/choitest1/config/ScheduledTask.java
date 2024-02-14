package com.example.choitest1.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 매일 아침 6시,
     * 1. 개설 1주일 전인 학교 조회,
     * 2. 학교와 교수 조회.
     * 3. 수업을 아무것도 개설하지 않은 교수에게 메일 보내기.
     * 개설요청 기간 1주일 전까지 아무런 강의를 개설요청하지 않은 경우에는 교수의 이메일로 경고 메일 보내기.
     */
    @Scheduled(cron = "0 0 6 * * ?")
    public void sandMailToPro() {
        log.info("{}", "==============================교수에게 메일보내기================");
    }

    /**
     * 매일 아침 9시,
     * 1. 수강오픈 1주일이 지난 학교 조회.
     * 2. 아무것도 수강신청하지 않은 학생들에게 메일 보내기
     * 수강신청 기간 1주일 전까지 아무런 강의를 수강신청하지 않은 경우에는 학생의 이메일로 경고 메일 보내기
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sandMailToStudent() {
        log.info("{}", "==============================학생에게 메일보내기================");

    }
}
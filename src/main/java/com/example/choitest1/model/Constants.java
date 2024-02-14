package com.example.choitest1.model;


import lombok.Getter;

@Getter
public final class Constants {

    public static final String USER_RULE_STUDENT = "STUDENT"; // A:관리자, S:학생, P:교수
    public static final String USER_RULE_ADMIN = "ADMIN";
    public static final String USER_RULE_PRO = "PRO";

    public static final int LIMIT_STUDENT_PER_SCHOOL = 30; //학교당 가입할수있는 최대 학생 유저


    public static final int LIMIT_PRO_PER_SCHOOL = 3;    // 학교당 가입할수있는 최대 교수 유저
    public static final int LIMIT_STUDENT_ACCESS = 10;    // 학교당 로그인할수 유저 수

    public static final int LIMIT_STUDENT_PER_STUDY = 3;    // 수업당 신청할 수 있는 유저 수

    public static final int USER_ACCESS_TOKEN_TIME = 1800000 * 420; //엑세스 토큰 유지 숫자

    public static final int MAX_LIMIT_STUDENT_PER_STUDENT = 30; //강의당 최대 학생 유저


}

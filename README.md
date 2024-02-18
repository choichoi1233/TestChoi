## 프로젝트 소개

JAVA : 17    / SpringBoot : 3.2.2  / DB :  Mysql , Jpa 

## 프로젝트 서비스 소개

전제 조건
* 관리자는 학교를 등록할 수 있습니다.   
• 각 최종 사용자는 자신의 계정을 통해 로그인이 가능해야 합니다. 
◦ 계정의 아이디는 이메일 형식이어야 합니다. (메일을 통한 알림을 위해)  
◦ 관리자를 제외한 각 최종 사용자는 스스로 계정을 발급할 수 있어야 합니다. 
◦ 교수의 경우, 관리자의 승인을 통해 최종 가입을 할 수 있습니다. 
◦ 최초 관리자 계정은 admin/admin 으로 생성 해주세요.(id/pw) 
• 각 최종 사용자는 복수의 역할을 보유할 수 없습니다. 
• 최대한 적절한 HTTP Method와 resource별 controller mapping을 활용하도록 해 
주세요.
• 동시 수강신청에 대해 처리할 수 있어야 합니다.

학생
• 학생은 학교 당 총 30명으로 제한됩니다. 
• 학생은 강의를 동시에 3개까지 수강할 수 있습니다.  
◦ 꼭 3개까지 수강할 필요는 없지만, 최소 하나의 강의는 수강해야 합니다.
◦ 수강신청 기간 1주일 전까지 아무런 강의를 수강신청하지 않은 경우에는 학생의 이메일로 경고 메일을 보내야 합니다.
• 학생은 수강신청을 철회할 수 있습니다. 
• 학생은 수강을 신청할 강의의 목록을 조회할 수 있습니다.
• 학생은 수강신청 기간 내에 수강을 신청할 수 있습니다.

교수
• 교수는 학교 당 총 3명으로 제한됩니다. 
• 교수는 강의를 동시에 두 개 까지 개설할 수 있습니다. 
◦ 교수는 최소 하나의 강의를 개설해야 합니다.
◦ 개설요청 기간 1주일 전까지 아무런 강의를 개설요청하지 않은 경우에는 교
수의 이메일로 경고 메일을 보내야 합니다.
• 교수는 강의 개설기간 내에 강의를 개설할 수 있습니다. 
◦ 강의의 수강인원을 제한할 수 있습니다. 
◦ 제한인원은 30명을 초과할 수 없습니다. 
• 교수는 수강신청 기간 이후에 수강을 신청한 학생들을 열람할 수 있어야 합니다. 
• 교수는 개설한 강의를 조회할 수 있습니다. 

관리자
• 관리자는 학교를 추가할 수 있어야 합니다. 
• 관리자는 학교 개별마다 수강신청 기간을 등록할 수 있어야 합니다.  
◦ 최소 2주 ~ 최대 4주의 수강신청 기간을 등록하고 개설할 수 있습니다.  
• 관리자는 교수의 가입을 승인하거나 거절할 수 있어야 합니다. 
• 관리자는 수강신청 기간을 변경하거나 삭제할 수 있습니다. 
• 수강신청 기간을 등록한 경우, 수강신청이 오픈되는 2주일 전 부터 교수는 강의를
개설할 수 있습니다. 
• 학교, 강의, 학생, 교수 등의 정보를 열람할 수 있어야 합니다. 

기술 조건
• 각 학교 당 동시 접속자는 10명으로 제한해야 합니다.
• 동시접속자를 초과할 경우, 대기열을 통해 유저가 현재 대기 N번째임을 확인할
수 있어야 합니다.
• 동시 수강신청 요청이 들어올 경우, 강의의 수강인원 제한치를 넘길 수 없도록 디
자인 되어야 합니다.
◦ 해당 요청은 선착순으로 등록된 순서별로 등록해야 합니다.

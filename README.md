# 모두의 마당
  
> 👉 [서비스 이용하기](https://everyoneyard.shop/)   

## 프로젝트 소개
✅ SpringBoot 기반 WebSocket 라이브러리와 STOMP프로토콜 기반 채팅 webrtc로 화상 채팅 구현한 **개인 프로젝트**입니다.
- 모두가 주인인 마당, 여기서 회의 시작해요
  <br/>
  

## 🔭 목차 | Contents
1️⃣ [개발 기간](#-개발-기간--project-period) <br/>
2️⃣ [프로젝트 목표](#-프로젝트-목표--project-goals) <br/>
3️⃣ [아키텍처](#-아키텍처--architecture) <br/>
4️⃣ [시스템 요구사항](#-시스템-요구사항--systems-requirments) <br/>
5️⃣ [현재 개발된 기능](#-현재-개발된-기능--current-development-function) <br/>
6️⃣ [기술 스택](#-기술-스택--technology-stack) <br/>
7️⃣ [기술적 의사결정](#-기술적-의사결정--technical-decision-making) <br/>
8️⃣ [Junit5 단위 테스트](#-junit5-단위-테스트--junit5-unit-test) <br/>
9️⃣ [트러블 슈팅](#-트러블-슈팅--trouble-shooting) <br/>
🔟 [성능 튜닝](#-성능-튜닝-및-개선--performance-tuning) <br/>
#️⃣ [Api 문서](#-Api-문서--api-docs) <br/> 
#️⃣ [기능 시연 GIF](#-기능-시연-gif--testing) <br/>


## 👬 개발 기간 | Project Period

개발 기간 : 23.10~23.12

## ⭐️ 프로젝트 목표 | Project Goals
1. 테스트 코드 커버리지 **최소 70%** 이상(**junit5**을 이용한 단위테스트)
2. 부하테스트를 통한 성능 측정 후 **최적화** 작업
3. git commit **컨벤션 규칙** 지키도록 노력하기


## 🛠 아키텍처 | Architecture
<img width="782" alt="스크린샷 2023-12-16 오후 2 31 24" src="https://github.com/LminWoo99/EveryOneYard/assets/86508110/fc5bedb0-7f64-4ef6-aeea-75fbc399466c">







## 💻 시스템 요구사항 | System Requirements
체크표시가 현재 개발 완료
- [x] 소셜 로그인
- [x] 채팅방 CRUD
- [x] Websocket&&일반 채팅
- [x] Redis를 통한 채팅 기록 불러오기
- [x] webrtc&&화상 채팅
- [x] Kurento Media Server N:M 화상채팅
- [x] DataChannel 사용하여 화상채팅시 채팅

## 🌱 현재 개발된 기능 | Current Development Function

✔️ **기본 기능**
  - 채팅방 생성
- 채팅방 생성 시 중복검사
- 네이버, 구글 소셜 로그인
- 닉네임 중복 시 임의의 숫자를 더해서 중복 안되도록
- 채팅방 입장 & 퇴장 확인
- 채팅 기능
    - RestAPI 기반 메시지 전송/수신
- 채팅방 유저 리스트 & 유저 숫자 확인

✔️ **채팅방 세부 기능**
  -  Amazon S3 기반으로 하는 채팅방 파일 업로드&다운로드
- 채팅방 삭제
- 채팅방 삭제 시 해당 채팅방 안에 있는 파일들도 S3 에서 함께 삭제
- 채팅방 유저 인원 설정
인원 제한 시 제한 된 인원만 채팅 참여 가능
- 채팅방 인원 , 방이름 , 잠금 상태 수정 기능

✔️ **화상채팅 기능 - WebRTC**
  - WebRTC 화상 채팅(kurento, coturn)
       - P2P 기반 음성&영상 채팅, 화면 공유 기능
      - 화면 공유
       - 화면 좌우 반전
      - DataChannel을 사용한 채팅 기능

  

## ⚙ 기술 스택 | Technology Stack
### Front-End
<div>
  <img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=orange">
  <img src="https://img.shields.io/badge/css3-1572B6?style=for-the-badge&logo=css3&logoColor=orange">
  <img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">
  <br>
  <img src="https://img.shields.io/badge/WEBRTC-333333?style=for-the-badge&logo=WebRTC&logoColor=black">
  <img src="https://img.shields.io/badge/thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=green">
</div>

### Back-End
<div>
  <img src="https://img.shields.io/badge/JAVA-007396?style=for-the-badge&logo=Java&logoColor=white">
  <img src="https://img.shields.io/badge/Jpa-007396?style=for-the-badge&logo=Jpa&logoColor=white">
  <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white">
  <img src="https://img.shields.io/badge/Spring security-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white">
  <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Spring Boot&logoColor=white">
  <br>
  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">
  <img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=for-the-badge&logo=Amazon EC2&logoColor=white">
  <img src="https://img.shields.io/badge/Amazon S3-569A31?style=for-the-badge&logo=Amazon S3&logoColor=white">
  <br>
  <img src="https://img.shields.io/badge/Amazon RDS-527FFF?style=for-the-badge&logo=Amazon RDS&logoColor=white">
  <img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=for-the-badge&logo=GitHub Actions&logoColor=white">
  <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">
  <img src="https://img.shields.io/badge/NGINX-009639?style=for-the-badge&logo=NGINX&logoColor=white">
</div>

### 개발 환경 | Environment
<div>
  <img src="https://img.shields.io/badge/IntelliJ IDEA-000000?style=for-the-badge&logo=IntelliJ IDEA&logoColor=white">
  <img src="https://img.shields.io/badge/Github-181717?style=for-the-badge&logo=Github&logoColor=white">
  <img src="https://img.shields.io/badge/naver-03C75A?style=for-the-badge&logo=naver&logoColor=white">
  <img src="https://img.shields.io/badge/google-4285F4?style=for-the-badge&logo=google&logoColor=white">
</div>


## 📑 기술적 의사결정 | Technical Decision-Making
<table>
  <tbody>
      <tr>
      <td>Nginx</td>
      <td>  

**정적 파일 호스팅** : Nginx는 높은 성능과 효율적인 리소스 관리로 알려져 있으며, 이는 정적 파일을 호스팅하는 데 매우 적합합니다. Nginx를 사용함으로써 웹 페이지의 로딩 속도와 반응성을 향상시키기 위해 사용하였습니다

**HTTPS 지원 및 인증서 관리**: Nginx를 통해 SSL/TLS 인증서를 쉽게 통합하고 관리할 수 있는 기능을 제공합니다. 이를 통해 HTTPS를 사용하여 웹 사이트의 보안을 강화하고, 사용자 데이터의 안전을 보장하였습니다.</td>   </tr>
    <tr>
      <td>Redis</td>
      <td>**채팅 성능 향상**: Redis는 메모리 기반의 데이터 저장소로, 빠른 읽기 및 쓰기 속도를 제공합니다. 이 특성은 실시간 채팅 기능에서 발생하는 대량의 데이터 처리 요구를 효과적으로 수용하기 위해 사용하였습니다.

**Look-aside 캐싱 패턴 적용**: 본 프로젝트에서는 look-aside 캐싱 패턴을 적용하여 데이터베이스의 부하를 감소시켰습니다. 이 패턴에서는 캐시 미스가 발생할 경우 데이터베이스에서 데이터를 조회하고, 결과를 Redis에 저장하여 후속 요청에서 빠르게 응답할 수 있도록 합니다. 이러한 접근 방식은 특히 고빈도 읽기 작업에 최적화된 성능을 제공하기 위해 사용하였습니다
</td>
    </tr>
    <tr>
      <td>Github Actions & Docker</td>
      <td>

**GitHub Actions를 이용한 자동화된 워크플로우 구축** : GitHub Actions를 사용하여 코드 통합, 테스트, 빌드 및 배포 과정을 자동화하였습니다. 이를 통해 코드 변경 사항이 발생할 때마다 자동으로 통합 및 테스트가 수행되어, 코드의 품질을 지속적으로 유지하고 신속한 피드백을 받을 수 있게 되었습니다.

**Docker를 사용한 일관된 환경 구성**: Docker 컨테이너 기술을 활용하여 애플리케이션의 실행 환경을 일관되게 유지하였습니다. Docker를 통해 개발, 테스트, 운영 환경에서 동일한 환경을 재현함으로써 환경에 따른 오류를 최소화하고, 배포 과정을 간소화할 수 있었습니다.</td>
    </tr>
    <tr>
      <td>Let's Encrypt</td>
      <td>화상채팅을 진행하기 위해 https 인증서를 발급받아야함으로, 무료로 HTTPS 인증서를 발급받기 위해 사용하였습니다.</td>
    </tr>
      <tr>
      <td>WebRtc</td>
      <td>실시간 화상 채팅 기능을 제공하기 위해 Peer to Peer 연결을 통해 브라우저간에 직접적인 실시간 데이터 교환을 가능하게 하기 위해 사용하였습니다.</td>
    </tr>
       <tr>
      <td>Kurento</td>
      <td> 화상채팅시 N:M 의 채팅을 구현했을 때 각 클라이언트 간의 연결 편의성과 클라이언트들에게 가해지는 부하 감소를 위해 KMS 사용</td>
    </tr>
  </tbody>
</table>

## 🧪 Junit5 단위 테스트 | Junit5 Unit Test
<img width="556" alt="스크린샷 2023-12-16 오후 1 59 53" src="https://github.com/LminWoo99/EveryOneYard/assets/86508110/5d400744-5123-49a8-998e-bcb308bae1ad">


**junit5** 와 **mockito** 를 사용하여 총 69개의 단위테스트를 진행하였습니다.

<img width="1171" alt="테스트 커버리지" src="https://github.com/LminWoo99/EveryOneYard/assets/86508110/bb2e85d3-f366-45f8-9993-8d20a3f86587">


-  핵심 로직, 컨트롤러, 서비스, 도메인 대상으로 <span style="color:red"> **`85%`** </span> 테스트 커버리지를 달성했습니다
- 프로젝트 목표였던 테스트 커버리지 70% 이상의 결과를 얻게 되었습니다.

## 🛠 트러블 슈팅 | Trouble Shooting
### `트러블슈팅: HTTPS 적용 및 웹소켓 연동 에러 해결`
**문제 상황 및 해결 과정**

**1. SSL 인증서 오류**

   로컬에서는 KeyTool로 SSL 인증서 생성 후 스프링 서버에 적용
EC2+Docker 환경에서는 Let's Encrypt를 통해 SSL 인증서 발급
AWS Route53을 사용하여 도메인과 EC2 인스턴스 연결
ERR TOO MANY REDIRECTS

**2. Nginx와 Spring Boot에서 중복 리다이렉트 설정 문제 발견**

   Spring Boot의 SslConfig에서 HTTP-to-HTTPS 리다이렉트 설정 제거로 문제 해결

**3. 화상채팅 카메라 미작동**

Nginx의 웹소켓 관련 헤더 설정 문제 진단
Nginx 설정에 **proxy_set_header Upgrade $http_upgrade;** 및 **proxy_set_header Connection "upgrade";** 추가하여 해결

#### 트러블 슈팅 과정 자세히 보기
👉[블로그 참고](https://velog.io/@mw310/AWS-ec2%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-https-%EC%9B%B9%EC%86%8C%EC%BC%93-%EC%A0%81%EC%9A%A9%ED%95%98%EB%A9%B4%EC%84%9C-%EC%97%90%EB%9F%AC)   

<hr>

### `트러블슈팅: OAuth2 소셜 로그인과 세션 관리`

#### 문제 상황
프로젝트에서 OAuth2 기반 네이버 및 구글 소셜 로그인 구현 중, **@AuthenticationPrincipal** 어노테이션을 통해 세션 정보에 접근하려 했으나, null 값이 반환되는 문제 발생.

**원인 분석**

Spring Security의 OAuth2 로그인 구현 시, 인증된 사용자 정보(SessionUser)가 SecurityContext에 제대로 저장되지 않아 발생한 문제로 파악.

#### 해결 과정
해결 방법: loadUser 메서드에서 OAuth2 로그인 후 생성된 **SessionUser** 객체를 **SecurityContextHolder**의 Context에 Authentication 객체로 명시적으로 저장함으로써 문제 해결.
```java
Authentication authentication = new UsernamePasswordAuthenticationToken(sessionUser, null, sessionUser.getAuthorities());
SecurityContextHolder.getContext().setAuthentication(authentication);
```
#### 트러블 슈팅 과정 자세히 보기
👉[블로그 참고](https://velog.io/@mw310/AuthenticationPrincipal%EC%97%90-null%EA%B0%92-%EB%93%A4%EC%96%B4%EC%98%A4%EB%8A%94-%EB%AC%B8%EC%A0%9C)   


-------

## 🛠 성능 튜닝 및 개선 | Performance Tuning
### ⚙️성능 튜닝: 스케일 아웃을 통한 트래픽 분산 처리 및 Nginx Consistent Hashing을 통한 채팅 기능 성능 개선
👉[자세한 과정은 블로그 참고](https://velog.io/@mw310/Nginx-%EB%A1%9C%EB%93%9C%EB%B0%B8%EB%9F%B0%EC%8B%B1-%EC%A0%81%EC%9A%A9%EC%9D%84-%ED%86%B5%ED%95%9C-%EC%B1%84%ED%8C%85-%EC%84%9C%EB%B9%84%EC%8A%A4-%EA%B0%9C%EC%84%A0-Consistent-Hashing)   
### Consistent Hashing 적용전
- **100명 유저**
  - 샘플 수: 100
  - 평균 응답 시간: 319ms
  - 최대 응답 시간: 5069ms
  - 평균 처리량: 145.4/sec
    
- **1000명 유저**
  - 샘플 수: 1,000
  - 평균 응답 시간: 1666ms
  - 최대 응답 시간: 20003ms
  - 평균 처리량: .0/hour
    
### Consistent Hashing 적용 후
- **100명 유저**
  - 샘플 수: 100
  - 평균 응답 시간: 108ms
  - 최대 응답 시간: 1305ms
  - 평균 처리량: 309.3/sec
- **1000명 유저**
  - 샘플 수: 1,000
  - 평균 응답 시간: 1097ms
  - 최대 응답 시간: 1832.75ms
  - 평균 처리량: 656.8/sec
### User 100명 비교 분석

- **평균 응답 시간(Average Response Time)**: 최적화 전에는 319ms 였던 반면, 최적화 후에는 108ms로 줄어들었습니다. 이는 약`66.1%`의 개선
- **처리량(Throughput)**: 초당 처리량이 최적화 전 145.4개에서 최적화 후 309.3개로 증가했습니다. 이는 약 `112.7%` 증가
- **오류(Error %)**: 두 경우 모두 0%
- **표준 편차(STD.DEV)**: 최적화 전에는 921.10이었던 표준 편차가 최적화 후에는 255.11로 줄어들었습니다. 이는 약 `72.3%` 감소

### User 1000명 비교 분석

- **평균 응답 시간(Average Response Time)**: 최적화 전에 평균 1666ms 였던 반면, 최적화 후에는 1097ms로 감소하였습니다. 대략 `34.2%`의 개선
- **처리량(Throughput)**: 최적화 전에는 처리량이 0으로 나타났으나, 최적화 후에는 초당 658.8건으로 크게 증가했습니다. 처리량이 0에서 측정 가능한 수치로 변화한 것은 상당한 개선
- **오류(Error %)**:  오류율은 최적화 전 28.62%에서 최적화 후 9.09%로 감소했습니다. 이는 오류율이 `68.25%` 감소
- **표준 편차(STD.DEV)**: 표준 편차는 최적화 전 4341.75에서 최적화 후 1832.75로 감소하여, 약 `57.8%` 줄어듬

### Consistent Hashing nginx conf.d 설정
```nginx
http {
    upstream myapp {
        consistent_hash $request_uri;
        server backend1.example.com;
        server backend2.example.com;
    }

    server {
        listen 80;
        location / {
            proxy_pass http://myapp;
            # 기타 필요한 설정 ...
        }
    }
}
```



### ⚙️성능 튜닝: 채팅 전체 메세지 조회 기능에서 Redis 도입을 통한 성능 개선
👉[자세한 과정은 블로그 참고](https://velog.io/@mw310/WebSocket-Stomp-Redis-%EC%84%B1%EB%8A%A5-%EC%B5%9C%EC%A0%81%ED%99%94#%EC%84%B1%EB%8A%A5-%ED%8A%9C%EB%8B%9D-%EB%B6%84%EC%84%9D)   
### Redis 사용 전:

- **10명 유저, 10번 반복 요청**
  - 샘플 수: 100
  - 평균 응답 시간: 12ms
  - 최대 응답 시간: 36ms
  - 평균 처리량: 94.6/sec
- **100명 유저, 100번 반복 요청**
  - 샘플 수: 10,000
  - 평균 응답 시간: 259ms
  - 최대 응답 시간: 1084ms
  - 평균 처리량: 351.2/sec

### Redis 사용 후:

- **10명 유저, 10번 반복 요청**
  - 샘플 수: 100
  - 평균 응답 시간: 3ms
  - 최대 응답 시간: 8ms
  - 평균 처리량: 106.5/sec

- **100명 유저, 100번 반복 요청**
  - 샘플 수: 10,000
  - 평균 응답 시간: 55ms
  - 최대 응답 시간: 268ms
  - 평균 처리량: 1271.9/sec

### 성능 튜닝 분석

- **응답 시간**: Redis 도입으로 100명의 유저가 요청할 때 평균 응답 시간이 259ms에서 55ms로 감소, **약 4.7배의** 개선
- **처리량(Throughput)**: Redis 사용으로 100명의 유저 요청 시 처리량이 351.2/sec에서 1271.9/sec로 증가, **약 3.6배**의 증가
- **최대 응답 시간**: 1084ms에서 268ms로 감소, **약 4배**의 개선

### Look-aside 전략 관련 코드

https://github.com/LminWoo99/EveryOneYard/blob/f421379ff377145300182a6cb84a64310a364a68/src/main/java/com/example/VideoChatting/service/chat/ChatService.java#L52-L77

## 📜 Api 문서 | Api Docs
👉 [해당 링크 참조](https://everyoneyard.shop/swagger-ui/)


## 🎥 기능 시연 GIF | Testing
- 현재 모든 개발이 완료된 상태가 아니라, 프로젝트 종료 이후에 시연 영상을 올릴 예정입니다 

<!--


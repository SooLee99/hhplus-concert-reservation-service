## 📑 프로젝트 소개
> 💡 **목표**: TDD와 클린 아키텍처 원칙을 적용하여, 다수의 사용자가 동시에 접근할 수 있는 콘서트 좌석 예약 서비스에서 대기열 관리, 동시성 제어, 잔액 충전 및 결제 기능을 안정적으로 구현하고, 이를 통해 실무에 가까운 서버 개발 경험을 쌓는 것을 목표로 합니다.

---
## 📅 프로젝트 전체 일정
![마일스톤](https://github.com/user-attachments/assets/3915be84-832a-42d1-8a31-2a37d17afd27)

## 📅 프로젝트 단계별 진행 상황 (Milestone)
![마일스톤2](https://github.com/user-attachments/assets/a0e95eb4-3f19-4324-a49e-1538eaf32170)

---
아래는 리드미에 포함할 수 있는 시퀀스 다이어그램 섹션 예시입니다. Mermaid를 이용하여 시퀀스 다이어그램을 작성할 수 있으며, 다이어그램 코드를 리드미에 추가하여 프로젝트의 동작 흐름을 시각적으로 설명할 수 있습니다.

---

# 📊 시퀀스 다이어그램

프로젝트의 주요 기능 및 흐름을 이해하기 위해 각 API의 시퀀스 다이어그램을 작성하였습니다. 아래 다이어그램들은 API의 호출 흐름, 사용자 요청 처리, 예외 처리 시나리오를 시각적으로 표현한 것입니다.

## 🏷️ (1) 유저 토큰 발급 API 시퀀스 다이어그램
```mermaid
    sequenceDiagram
    participant User as 사용자
    participant AuthServer as 인증 모듈
    participant UserDB as 사용자 DB
    participant TokenDB as 토큰 DB
    participant QueueDB as 대기열 Queue
    participant ReservationSystem as 예약 모듈
    
    %% 1️⃣ 사용자 대기열 토큰 발급 및 실시간 정보 조회 최적화 시퀀스
    User->>AuthServer: 토큰 발급 요청 (UUID)
    Note over AuthServer: UUID 기반으로 사용자 정보 및 대기열 정보 조회 시작
    
    %% 2️⃣ 사용자 정보 조회
    AuthServer->>UserDB: 사용자 존재 여부 확인 (UUID 검증)
    
    alt 사용자 미존재
    %% 3️⃣ 유효하지 않은 사용자일 경우
    UserDB-->>AuthServer: "사용자 없음" 에러 반환
    AuthServer-->>User: "유효하지 않은 사용자" 에러 반환
    else 사용자 존재
    %% 4️⃣ 사용자가 존재하는 경우, 사용자 정보 반환
    UserDB-->>AuthServer: 사용자 정보 반환 (UUID, 사용자 이름)
    
        %% 5️⃣ 기존 토큰 존재 여부 확인
        AuthServer->>TokenDB: 기존 토큰 존재 여부 확인 (UUID)
        
        alt 기존 토큰 존재
            %% 6️⃣ 기존 토큰 정보 반환 및 에러 처리
            TokenDB-->>AuthServer: 기존 토큰 정보 반환 (토큰, 만료 시간 등)
            AuthServer-->>User: "이미 활성화된 토큰이 존재합니다" 에러 반환
        else 기존 토큰 미존재
            %% 7️⃣ 토큰 생성
            AuthServer->>TokenDB: 새로운 토큰 생성 요청 (UUID, 사용자 정보)
            TokenDB-->>AuthServer: 토큰 생성 완료 (JWT 토큰, 만료 시간 등)
    
            %% 11️⃣ 새로운 대기열 정보 생성
            AuthServer->>QueueDB: 새로운 대기열 정보 생성 (UUID)
            QueueDB-->>AuthServer: 대기열 생성 완료 (대기 순서, 진입 시간 등)
    
            %% 12️⃣ 사용자에게 새로운 대기열 정보 반환
            AuthServer-->>User: 새로운 대기열 정보 반환 (현재 순서, 잔여 대기 시간 등)
        end
    end
    
    %% 추가 시나리오: 대기열 토큰 삭제 시나리오
    par 사용자 자발적 이탈 시
    User->>AuthServer: 대기열 이탈 요청 (UUID)
    AuthServer->>QueueDB: 대기열 정보 삭제 요청 (UUID)
    QueueDB-->>AuthServer: 대기열 삭제 완료
    AuthServer-->>User: 대기열 이탈 완료 메시지 반환
    and 예약 완료 시 대기열 삭제
    ReservationSystem->>AuthServer: 예약 완료 알림 (UUID)
    AuthServer->>QueueDB: 대기열 정보 삭제 요청 (UUID)
    QueueDB-->>AuthServer: 대기열 삭제 완료
    end
    
    %% 추가 시나리오: 대기열 만료 시간 도래 시 대기열 삭제
    critical 대기열 만료 시간 도래
    AuthServer->>AuthServer: @Scheduled(fixedRate = 300000) - 만료된 대기열 정보 확인 및 삭제 (5분 간격 주기적 실행)
    AuthServer->>QueueDB: 만료된 대기열 정보 삭제 요청 (UUID)
    QueueDB-->>AuthServer: 만료된 대기열 삭제 완료
    end
```

## 🏷️ (2) 예약 가능 날짜 조회 API 시퀀스 다이어그램
```mermaid
  sequenceDiagram
  participant User as 사용자
  participant ReservationServer as 예약 모듈
  participant QueueDB as 대기열 Queue
  participant ReservationDB as 예약 DB
  
  %% 1️⃣ 예약 가능 날짜 조회 API
  User->>ReservationServer: 예약 가능 날짜 조회 요청
  Note over ReservationServer: 날짜 정보 조회 시작
  
  %% 대기열 큐에 접근하여 다른 프로세스와 경합 피함
  ReservationServer->>QueueDB: 날짜 조회 요청 큐 대기 (대기열 등록)
  alt 큐 접근 성공
  QueueDB-->>ReservationServer: 큐 진입 성공
  ReservationServer->>ReservationDB: 예약 가능 날짜 목록 조회
  alt 예약 가능한 날짜 없음
  %% 예약 가능한 날짜가 없는 경우
  ReservationDB-->>ReservationServer: "예약 가능한 날짜가 없습니다" 에러 반환
  ReservationServer-->>User: "예약 가능한 날짜가 없습니다" 에러 반환
  QueueDB-->>ReservationServer: 큐 이탈 (작업 종료)
  else 예약 가능한 날짜 존재
  %% 예약 가능한 날짜가 존재하는 경우
  ReservationDB-->>ReservationServer: 예약 가능 날짜 목록 반환
  ReservationServer-->>User: 예약 가능 날짜 정보 반환
  QueueDB-->>ReservationServer: 큐 이탈 (작업 종료)
  end
  else 큐 접근 실패
  %% 큐 접근 실패 시
  QueueDB-->>ReservationServer: "큐 접근 실패" 에러 반환
  ReservationServer-->>User: "서버가 혼잡하여 요청을 처리할 수 없습니다" 에러 반환
  end
  
  %% 예약 가능 날짜 조회 중 DB 접근 실패 시 처리
  alt DB 접근 실패
  ReservationDB-->>ReservationServer: "DB 접근 실패" 에러 반환
  ReservationServer-->>User: "서버 오류로 인해 예약 가능 날짜를 조회할 수 없습니다" 에러 반환
  end
```

## 🏷️ (3) 특정 날짜 좌석 정보 조회 API 시퀀스 다이어그램
```mermaid
  sequenceDiagram
  participant User as 사용자
  participant ReservationServer as 예약 모듈
  participant QueueDB as 대기열 Queue
  participant ReservationDB as 예약 DB
  
  %% 2️⃣ 특정 날짜 좌석 정보 조회 API
  User->>ReservationServer: 특정 날짜의 좌석 정보 조회 요청 (날짜 입력)
  Note over ReservationServer: 좌석 정보 조회 시작
  
  %% 대기열 큐에 접근하여 경합 방지
  ReservationServer->>QueueDB: 좌석 정보 조회 요청 큐 대기 (날짜 기반 대기열 등록)
  alt 큐 접근 성공
  QueueDB-->>ReservationServer: 큐 진입 성공
  ReservationServer->>ReservationDB: 해당 날짜의 좌석 상태 조회 (1 ~ 50번 좌석)
  
      alt 좌석 정보 존재
          %% 좌석 정보가 존재하는 경우
          ReservationDB-->>ReservationServer: 좌석 상태 반환 (예: 좌석 번호 및 예약 여부)
          
          alt 모든 좌석이 예약된 경우
              ReservationServer-->>User: "모든 좌석이 이미 예약되었습니다" 메시지 반환
              QueueDB-->>ReservationServer: 큐 이탈 (작업 종료)
          else 일부 좌석이 예약 가능
              %% 사용자에게 좌석 정보 반환
              ReservationServer-->>User: 좌석 정보 반환 (예: 1번 예약, 2번 예약 가능 등)
              QueueDB-->>ReservationServer: 큐 이탈 (작업 종료)
          end
      else 좌석 정보 미존재
          %% 좌석 정보가 존재하지 않는 경우
          ReservationDB-->>ReservationServer: "해당 날짜에 대한 좌석 정보가 존재하지 않습니다" 에러 반환
          ReservationServer-->>User: "해당 날짜에 대한 좌석 정보가 존재하지 않습니다" 에러 반환
          QueueDB-->>ReservationServer: 큐 이탈 (작업 종료)
      end
  else 큐 접근 실패
  %% 큐 접근 실패 시
  QueueDB-->>ReservationServer: "큐 접근 실패" 에러 반환
  ReservationServer-->>User: "서버가 혼잡하여 요청을 처리할 수 없습니다" 에러 반환
  end
  
  %% 예약 가능한 좌석 조회 중 DB 접근 실패 시 처리
  alt DB 접근 실패
  ReservationDB-->>ReservationServer: "DB 접근 실패" 에러 반환
  ReservationServer-->>User: "서버 오류로 인해 좌석 정보를 조회할 수 없습니다" 에러 반환
  end
  
  %% 큐 이탈 실패 처리
  alt 큐 이탈 실패
  QueueDB-->>ReservationServer: "큐 이탈 실패" 에러 반환
  ReservationServer-->>User: "좌석 정보 조회 성공" (정상 응답)
  end
```

## 🏷️ (4) 좌석 예약 요청 API 시퀀스 다이어그램
```mermaid
  sequenceDiagram
  participant User as 사용자
  participant AuthServer as 인증 모듈
  participant TokenDB as 토큰 DB
  participant ReservationServer as 예약 모듈
  participant QueueSystem as 대기열 Queue
  participant ReservationDB as 예약 DB
  participant PaymentServer as 결제 모듈
  
  %% 1️⃣ 사용자 좌석 예약 요청 시나리오
  User->>AuthServer: 좌석 예약 요청 (날짜, 좌석 번호, 토큰)
  Note over AuthServer: 좌석 예약 요청 수신 및 토큰 유효성 검증 시작
  
  %% 2️⃣ 토큰 유효성 검증
  AuthServer->>TokenDB: 토큰 유효성 확인 (토큰)
  TokenDB-->>AuthServer: 토큰 유효성 결과 반환 (유효/무효)
  
  alt 유효하지 않은 토큰
  %% 3️⃣ 유효하지 않은 토큰일 경우 오류 반환
  AuthServer-->>User: "유효하지 않은 토큰" 오류 반환
  else 유효한 토큰
  %% 4️⃣ 예약 서버로 유효한 요청 전달
  AuthServer->>ReservationServer: 좌석 예약 요청 (날짜, 좌석 번호, 사용자 ID)
  Note over ReservationServer: 대기열 큐를 통한 좌석 예약 처리 시작
  
      %% 5️⃣ 대기열 큐에 접근하여 자원 경합 방지
      ReservationServer->>QueueSystem: 좌석 예약 요청 큐 대기 (날짜 및 좌석 번호)
      
      alt 큐 접근 성공
          QueueSystem-->>ReservationServer: 큐 진입 성공
          
          %% 6️⃣ 좌석 예약 상태 확인 및 Pessimistic Lock 획득
          ReservationServer->>ReservationDB: 좌석 예약 상태 확인 및 락 요청 (날짜, 좌석 번호)
          ReservationDB-->>ReservationServer: 좌석 상태 반환 (예약 가능 여부)
  
          alt 예약 불가 상태 (이미 예약됨)
              %% 7️⃣ 좌석이 이미 임시 예약된 경우
              ReservationServer-->>User: 좌석 예약 실패 ("해당 좌석은 이미 예약되었습니다")
          else 예약 가능 상태
              %% 8️⃣ 좌석 임시 예약 처리
              ReservationServer->>ReservationDB: 좌석 임시 예약 요청 (날짜, 좌석 번호, 사용자 ID, 예약 만료 시간: +5분)
              ReservationDB-->>ReservationServer: 임시 예약 성공 (임시 예약 만료 시간)
  
              %% 9️⃣ 사용자에게 임시 예약 성공 응답
              ReservationServer-->>User: 좌석 임시 예약 성공 ("임시 예약 성공 - 만료 시간: +5분")
  
              %% 🔟 결제 요청 발생 시 처리
              User->>PaymentServer: 좌석 결제 요청 (날짜, 좌석 번호, 사용자 ID)
              PaymentServer-->>ReservationServer: 결제 성공 알림 (날짜, 좌석 번호)
              
              alt 결제 성공
                  ReservationServer->>ReservationDB: 좌석 예약 확정 (날짜, 좌석 번호)
                  ReservationDB-->>ReservationServer: 예약 확정 성공
                  ReservationServer-->>User: 좌석 예약 확정 ("예약이 완료되었습니다")
              else 결제 실패
                  ReservationServer-->>User: 좌석 결제 실패 ("결제 중 오류 발생")
              end
  
              %% 🔟 스케줄러로 임시 예약 만료 처리
              Note over ReservationServer: 스케줄러 시작 (5분 후 예약 해제)
  
              %% 11️⃣ 자기 자신에게 예약 해제 메시지 전달
              ReservationServer->>ReservationServer: 좌석 예약 만료 처리 (날짜, 좌석 번호, 사용자 ID)
  
              critical 임시 예약 만료 시
                  ReservationServer->>ReservationDB: 임시 예약 해제 요청 (날짜, 좌석 번호, 사용자 ID)
                  ReservationDB-->>ReservationServer: 좌석 임시 예약 해제 성공
                  ReservationServer-->>User: "임시 예약이 만료되었습니다"
              option 해제 실패 시
                  ReservationServer->>ReservationServer: 예약 해제 실패 처리 (DB 오류 등)
                  ReservationServer-->>User: "임시 예약 해제 중 오류가 발생했습니다"
              end
          end
      else 큐 접근 실패
          %% 12️⃣ 큐 접근 실패 시 오류 반환
          QueueSystem-->>ReservationServer: 큐 접근 실패
          ReservationServer-->>User: "서버가 혼잡하여 요청을 처리할 수 없습니다"
      end
  end
```

## 🏷️ (5) 잔액 충전 API 시퀀스 다이어그램
```mermaid
  sequenceDiagram
  participant User as 사용자
  participant AuthServer as 인증 모듈
  participant TokenDB as 토큰 DB
  participant ReservationServer as 결제 모듈
  participant BalanceDB as 잔액 DB
  
  %% 1️⃣ 사용자 잔액 충전 요청 시나리오
  User->>AuthServer: 잔액 충전 요청 (토큰, 충전 금액)
  Note over AuthServer: 토큰 유효성 검증 시작
  
  %% 2️⃣ 토큰 유효성 검증
  AuthServer->>TokenDB: 토큰 유효성 확인 (토큰)
  TokenDB-->>AuthServer: 토큰 유효성 결과 반환 (유효/무효)
  
  alt 유효하지 않은 토큰
  %% 3️⃣ 유효하지 않은 토큰일 경우
  AuthServer-->>User: "유효하지 않은 토큰" 오류 반환
  else 유효한 토큰
  %% 4️⃣ 예약 서버로 유효한 요청 전달
  AuthServer->>ReservationServer: 잔액 충전 요청 (사용자 ID, 충전 금액)
  Note over ReservationServer: 충전 요청 수신 후 유효성 검증 시작
  
      %% 5️⃣ 충전 금액 유효성 검증
      alt 금액 유효성 오류
          ReservationServer-->>User: "유효하지 않은 금액입니다" 오류 반환
      else 유효한 금액
          %% 6️⃣ 잔액 DB에 충전 요청
          ReservationServer->>BalanceDB: 잔액 충전 (사용자 ID, 충전 금액)
          alt DB 접근 오류
              %% 7️⃣ 충전 중 DB 접근 오류 발생
              BalanceDB-->>ReservationServer: 잔액 충전 실패 (DB 오류)
              ReservationServer-->>User: "서버 오류로 인해 충전이 실패했습니다"
          else
              %% 8️⃣ 충전 성공
              BalanceDB-->>ReservationServer: 잔액 충전 성공 (잔액 업데이트)
              ReservationServer-->>User: "충전 성공 - 현재 잔액: [잔액 정보]"
          end
      end
  end
```

## 🏷️ (6) 사용자 잔액 조회 요청 API 시퀀스 다이어그램
```mermaid
  sequenceDiagram
  %% 참가자 정의
  participant User as 사용자
  participant AuthServer as 인증 모듈
  participant TokenDB as 토큰 DB
  participant ReservationServer as 결제 모듈
  participant BalanceDB as 잔액 DB
  
  %% 1️⃣ 사용자 잔액 조회 요청 시나리오
  User->>AuthServer: 잔액 조회 요청 (토큰)
  Note over AuthServer: 토큰 유효성 검증 시작
  
  %% 2️⃣ 토큰 유효성 검증
  AuthServer->>TokenDB: 토큰 유효성 확인 (토큰)
  TokenDB-->>AuthServer: 토큰 유효성 결과 반환 (유효/무효)
  
  alt 유효하지 않은 토큰
  %% 3️⃣ 유효하지 않은 토큰일 경우
  AuthServer-->>User: "유효하지 않은 토큰" 오류 반환
  else 유효한 토큰
  %% 4️⃣ 예약 서버로 유효한 요청 전달
  AuthServer->>ReservationServer: 잔액 조회 요청 (사용자 ID)
  
      %% 5️⃣ 잔액 조회 요청 처리
      ReservationServer->>BalanceDB: 잔액 조회 요청 (사용자 ID)
      alt DB 접근 오류
          %% 6️⃣ 잔액 조회 중 DB 접근 오류 발생
          BalanceDB-->>ReservationServer: 잔액 조회 실패 (DB 오류)
          ReservationServer-->>User: "서버 오류로 인해 잔액 조회가 실패했습니다"
      else
          %% 7️⃣ 잔액 조회 성공
          BalanceDB-->>ReservationServer: 잔액 정보 반환 (잔액)
          ReservationServer-->>User: "잔액 조회 성공 - 현재 잔액: [잔액 정보]"
      end
  end
```

## 🏷️ (7) 결제 API 시퀀스 다이어그램
```mermaid
  sequenceDiagram
    participant User as 사용자
    participant ReservationServer as 예약 모듈
    participant ReservationDB as 예약 DB
    participant PaymentServer as 결제 모듈
    participant PaymentDB as 결제 DB
    participant QueueSystem as 대기열 Queue
    
    %% 1️⃣ 사용자 결제 요청 시나리오
    User->>ReservationServer: 결제 요청 (날짜, 좌석 번호, 사용자 ID)
    ReservationServer->>PaymentServer: 결제 요청 전달 (날짜, 좌석 번호, 사용자 ID)
    
    %% 2️⃣ 결제 처리 및 결제 내역 생성
    PaymentServer->>PaymentDB: 결제 내역 생성 (사용자 ID, 결제 금액, 날짜, 좌석 번호)
    
    alt 결제 DB 접근 오류
        PaymentDB-->>PaymentServer: 결제 내역 생성 실패 (DB 오류)
        PaymentServer-->>ReservationServer: 결제 실패 알림
        ReservationServer-->>User: "결제 중 오류가 발생했습니다"
    else
        PaymentDB-->>PaymentServer: 결제 내역 생성 성공
        PaymentServer-->>ReservationServer: 결제 성공 알림 (날짜, 좌석 번호)

        %% 3️⃣ 좌석 소유권 배정 처리
        ReservationServer->>ReservationDB: 좌석 소유권 배정 (날짜, 좌석 번호, 사용자 ID)
        
        alt 좌석 소유권 배정 실패
            ReservationDB-->>ReservationServer: 소유권 배정 실패 (DB 오류)
            ReservationServer-->>User: "결제는 완료되었으나 좌석 소유권 배정에 실패했습니다."
        else 
            ReservationDB-->>ReservationServer: 소유권 배정 성공
            ReservationServer-->>User: "결제가 완료되었으며 좌석 소유권이 배정되었습니다."

            %% 4️⃣ 대기열 토큰 만료 처리
            ReservationServer->>QueueSystem: 대기열 토큰 만료 요청 (사용자 ID)
            
            alt 대기열 토큰 만료 실패
                QueueSystem-->>ReservationServer: 대기열 토큰 만료 실패
                ReservationServer-->>User: "결제는 완료되었으나 대기열 토큰 만료에 실패했습니다."
            else
                QueueSystem-->>ReservationServer: 대기열 토큰 만료 성공
                ReservationServer-->>User: "결제 및 좌석 소유권 배정, 대기열 토큰 만료 처리가 모두 완료되었습니다."
            end
        end
    end
```

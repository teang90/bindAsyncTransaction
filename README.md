# 비동기 트랜잭션 바인딩 시스템 구조 샘플 (Sample Structure for Async Transaction Binding)

## 📋 프로젝트 개요

REST API를 통해 요청을 받아 JMS 메시지 큐를 통해 비동기로 비즈니스 로직을 처리하고, 요청-응답을 매칭하여 동기화된 응답을 반환하는 시스템입니다. **포트와 어댑터 패턴(Port and Adapter Pattern)**을 적용하여 관심사의 분리와 확장성을 확보했습니다.

## 🎯 주요 특징

### 1. 포트와 어댑터 패턴 (Hexagonal Architecture)
- **포트(Port)**: 비즈니스 로직의 인터페이스 정의
- **어댑터(Adapter)**: 외부 시스템과의 통신 계층
  - REST API 어댑터: HTTP 요청/응답 처리
  - JMS 어댑터: 메시지 큐 송수신 처리

### 2. 비동기 트랜잭션 처리
- REST API 요청을 JMS 큐로 전송하여 비즈니스 로직을 비동기 처리
- **JMS Selector**를 활용하여 특정 서버 인스턴스로 메시지 필터링
- **CompletableFuture**와 **Callback 패턴**을 결합하여 비동기 응답 처리
- **TransactionBindingManager**를 통해 요청-응답 트랜잭션 ID 매칭

### 3. 트랜잭션 바인딩 메커니즘
- 요청 시점에 `CompletableFuture`와 `ReceiveCallback`을 생성하여 캐시에 저장
- JMS 메시지 수신 시 트랜잭션 ID를 기반으로 해당 Callback을 조회하여 응답 처리
- Caffeine Cache를 활용한 고성능 트랜잭션 바인딩 관리

## 🏗️ 아키텍처

```
┌─────────────────┐
│  REST API       │
│  (Controller)   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐      ┌──────────────────┐
│  EventService   │─────▶│ JmsSender        │
│  (Port)         │      │ (Adapter)        │
└────────┬────────┘      └────────┬─────────┘
         │                        │
         │                        ▼
         │                ┌──────────────────┐
         │                │   JMS Queue      │
         │                │  (ActiveMQ)      │
         │                └────────┬─────────┘
         │                         │
         │                         ▼
         │                ┌──────────────────┐
         │                │  JmsMsgListener  │
         │                │  (Adapter)       │
         │                └────────┬─────────┘
         │                         │
         │                         ▼
         │                ┌──────────────────┐
         │                │TransactionBinding│
         │                │    Manager       │
         │                └────────┬─────────┘
         │                         │
         ▼                         │
┌─────────────────┐                │
│ CompletableFuture│◀───────────────┘
│   + Callback     │
└─────────────────┘
```

## 🔑 핵심 기술 및 설계 패턴

### 기술 스택
- **Java 17**: 최신 Java 기능 활용
- **Spring Boot 3.5.8**: 애플리케이션 프레임워크
- **Apache ActiveMQ**: JMS 메시지 브로커
- **Caffeine Cache**: 고성능 인메모리 캐시
- **CompletableFuture**: 비동기 프로그래밍

### 설계 패턴
1. **포트와 어댑터 패턴**: 비즈니스 로직과 외부 시스템의 결합도 최소화
2. **Callback 패턴**: 비동기 응답 처리
3. **Strategy 패턴**: 다양한 이벤트 서비스 구현 가능
4. **Factory 패턴**: JMS Connection Factory 및 Listener Factory

## 📁 프로젝트 구조

```
src/main/java/com/jty/pf/bindasynctransaction/
├── restAPI/                    # REST API 어댑터
│   ├── controller/
│   │   └── EventController.java
│   ├── service/
│   │   ├── EventService.java          # 포트 인터페이스
│   │   └── ClientRegistService.java   # 포트 구현체
│   └── data/
│       ├── IpRequestDTO.java
│       └── IpResponseDTO.java
├── jms/                        # JMS 어댑터
│   ├── JmsSender.java         # 메시지 송신
│   ├── JmsMsgListener.java    # 메시지 수신 (Selector 활용)
│   ├── ReceiveCallback.java   # 콜백 인터페이스
│   └── TxCommonValue.java     # 트랜잭션 공통 값
├── transaction/               # 트랜잭션 바인딩 관리
│   └── TransactionBindingManager.java
├── config/                    # 설정
│   ├── JmsConfig.java         # JMS 설정 (Selector, Connection Pool)
│   └── AppConfig.java         # 캐시 설정
└── common/
    └── data/
        └── ResponseDTO.java
```

## 🔄 처리 흐름

### 1. 요청 처리 흐름
```
1. REST API 요청 수신 (EventController)
   ↓
2. EventService.registIp() 호출
   ↓
3. CompletableFuture 및 ReceiveCallback 생성
   ↓
4. TransactionBindingManager에 Callback 등록 (트랜잭션 ID 기반)
   ↓
5. JmsSender를 통해 JMS 큐로 메시지 전송
   ↓
6. CompletableFuture.get()으로 응답 대기 (타임아웃: 3초)
```

### 2. 응답 처리 흐름
```
1. 비즈니스 모듈에서 JMS 큐로 응답 메시지 전송
   ↓
2. JmsMsgListener가 JMS Selector로 메시지 필터링 수신
   ↓
3. 트랜잭션 ID 추출
   ↓
4. TransactionBindingManager.receiveMsg() 호출
   ↓
5. 캐시에서 해당 트랜잭션 ID의 Callback 조회
   ↓
6. Callback.recvResponse() 실행
   ↓
7. CompletableFuture.complete() 호출하여 요청 스레드에 응답 전달
```

## 💡 핵심 구현 내용

### 1. JMS Selector를 활용한 메시지 필터링
```java
// JmsConfig.java
@Bean
public JmsListenerContainerFactory<?> selectorListenerContainerFactory(...) {
    String selector = "server=" + Inet4Address.getLocalHost().getHostName();
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory() {
        @Override
        public DefaultMessageListenerContainer createListenerContainer(JmsListenerEndpoint endpoint) {
            DefaultMessageListenerContainer container = super.createListenerContainer(endpoint);
            container.setMessageSelector(selector);  // 특정 서버로만 메시지 수신
            return container;
        }
    };
    return factory;
}
```

### 2. 비동기 응답 처리 (CompletableFuture + Callback)
```java
// ClientRegistService.java
CompletableFuture<IpResponseDTO> outDtoCF = new CompletableFuture<>();
ReceiveCallback receiveCallback = (txId, responseDTO) -> {
    if(!sessionId.equals(txId))
        throw new IllegalStateException("not match transactionId");
    outDtoCF.complete((IpResponseDTO) responseDTO);
};

transactionBindingManager.addResponseCallback(sessionId, receiveCallback);
jmsSender.send(IpRequestDTO.of(sessionId, clientIp, accessTime));

return outDtoCF.get(3L, TimeUnit.SECONDS);  // 타임아웃 처리
```

### 3. 트랜잭션 바인딩 관리
```java
// TransactionBindingManager.java
public void receiveMsg(String txId, ResponseDTO responseDTO) {
    ReceiveCallback receiveCallback = cache.get(txId, ReceiveCallback.class);
    if(receiveCallback == null) {
        log.error("timeout!! {}", txId);
    }
    receiveCallback.recvResponse(txId, responseDTO);
}
```

## 🚀 실행 방법

### 필수 요구사항
- Java 17 이상
- Gradle 7.x 이상
- Apache ActiveMQ 실행 중

### 설정
`src/main/resources/application.yaml` 파일에 ActiveMQ 연결 정보를 설정하세요.

### 빌드 및 실행
```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun
```

### API 호출 예시
```bash
GET http://localhost:8080/enter.do
```

## 📊 성능 최적화

1. **Connection Pooling**: JMS Connection Pool을 통한 연결 재사용
2. **Caffeine Cache**: 고성능 인메모리 캐시로 트랜잭션 바인딩 관리
3. **비동기 처리**: CompletableFuture를 통한 논블로킹 응답 처리
4. **JMS Selector**: 불필요한 메시지 수신 방지로 성능 향상


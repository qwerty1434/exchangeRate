# 환율계산 서비스 API

오픈 API를 이용한 "환율계산 서비스"입니다.

## 사용 버전

- Java: 11
- Spring-boot: 2.7.12
- Spring-webflux: 5.3.27
- JUnit: 5.8.2
- H2-Database: 2.1.214

## 실행 파일

- 파일명: exchange.jar
- 위치: 프로젝트 최상단
- 실행 방법: `java -jar exchange.jar`

## 핵심 작업

### 1. 외부 API를 요청하는 ExchangeRateClient를 singleton객체로 구성했습니다.

- 적용한 이유
  - 인스턴스 변수인 requestUrl이 대부분 동일한데, 모든 유저의 요청마다 ExchangeRateClient객체를 생성하는 건 메모리 낭비라고 생각했습니다.
- 적용 방법
  - 인스턴스 변수가 다르면 다른 객체를 가지도록 정적 변수인 ConcurrentHashMap에 singleton 객체를 보관했습니다.
  - 인스턴스를 획득하는 getInstance메서드에 synchronized키워드를 선언해 동시에 여러 사용자가 접근해 여러 singleton객체가 생성되는 걸 방지했습니다.

### 2. 단기간에 많은 API서버 요청을 방지하기 위한 cache 활용했습니다.

- 적용한 이유
  - 단기간에 다수의 요청을 보내면 API서버가 응답을 거부하는 사실을 확인했습니다.
  - 대량의 사용자가 서비스를 사용했을 때 API응답이 거부되는 상황이 자주 발생할 거라 판단해 데이터의 실시간성을 포기하고 이전 요청을 일부 재활용하는 방식으로 시스템을 설계했습니다.
- 적용 방법
  - ExchangeRateClient에 API요청 정보와 요청 결과를 담는 Map변수를 선언했습니다.
  - 요청하려는 값에 대한 정보가 cache에 존재하고, CACHE_EXPIRATION_SECONDS을 초과하지 않았다면 API서버에 요청하지 않고 캐싱된 값을 활용합니다. 현재 CACHE_EXPIRATION_SECONDS는 60초로 설정되어 있습니다.

<hr>

# API 명세

## 환율 조회 API

두 통화 간의 환율을 조회합니다.

### 요청

**URL**

```java
GET /rate/{source}/{target}
```

**파라미터**

| 파라미터 | 타입   | 설명      | 비고                            |
| -------- | ------ | --------- | ------------------------------- |
| source   | String | 송금 국가 | 이용 가능한 통화: USD           |
| target   | String | 수취 국가 | 이용 가능한 통화: KRW, JPY, PHP |

### **응답**

환율을 반환합니다.

```java
0.85
```

## 수취금액 계산 API

원본 통화로 주어진 환전 금액을 대상 통화로 계산합니다.

### 요청

**URL**

```java
GET /calculate/{source}/{target}?remittance={remittance}
```

**파라미터**

| 파라미터   | 타입   | 설명      | 비고                            |
| ---------- | ------ | --------- | ------------------------------- |
| source     | String | 송금 국가 | 이용 가능한 통화: USD           |
| target     | String | 수취 국가 | 이용 가능한 통화: KRW, JPY, PHP |
| remittance | Number | 송금액    | 입력 가능한 금액: 0~10000       |

### **응답**

변환된 수취금액을 반환합니다.

```java
1277303.792
```

## 환전 API

고객에게 환전 금액이 주어지며 DB에 거래 내용이 기록됩니다.

### 요청

**URL**

```java
GET /exchange/{source}/{target}?remittance={remittance}
```

**파라미터**

| 파라미터   | 타입   | 설명      | 비고                            |
| ---------- | ------ | --------- | ------------------------------- |
| source     | String | 송금 국가 | 이용 가능한 통화: USD           |
| target     | String | 수취 국가 | 이용 가능한 통화: KRW, JPY, PHP |
| remittance | Number | 송금액    | 입력 가능한 금액: 0~10000       |

### **응답**

환전된 금액과 화폐를 반환합니다.

```java
{
    remittance: 1277303.792,
    currency: KRW
}
```

### 주요 에러 사항

| 원인                                              | 에러 코드 | 에러 메시지                                                    |
| ------------------------------------------------- | --------- | -------------------------------------------------------------- |
| source 또는 target에 잘못된 값을 입력한 경우      | 400       | 화폐 단위가 올바르지 않습니다.                                 |
| source에 USD 외의 Currency를 넣은 경우            | 400       | 올바른 송금국가가 아닙니다.                                    |
| target에 KRW, JPY, PHP 외의 Currnecy를 넣은 경우  | 400       | 올바른 수취국가가 아닙니다.                                    |
| source와 target에 동일한 값을 넣은 경우           | 400       | 송금국가는 수취국가가 동일할 수 없습니다.                      |
| remittance에 0~10000 사이의 숫자를 넣지 않은 경우 | 400       | 송금액이 올바르지 않습니다.                                    |
| 서버 내부에 문제가 발생한 경우                    | 500       | 서버 내부에 문제가 발생했습니다. 담당자에게 문의 부탁드립니다. |

<hr>

# 환율계산 서비스 API
오픈 API를 이용한 "환율계산 서비스"입니다.

## 실행 파일
- 파일명: exchange.jar
- 위치: 프로젝트 최상단
- 실행 방법: `java -jar exchange.jar`


# API 명세
## 환율 조회 API

두 통화 간의 환율을 조회합니다.

### 요청

**URL**

```java
GET /rate/{source}/{target}
```

**파라미터**

| 파라미터 | 타입 | 설명 | 비고 |
| --- | --- | --- | --- |
| source | String | 송금 국가 | 이용 가능한 통화: USD |
| target | String | 수취 국가 | 이용 가능한 통화: KRW, JPY, PHP |

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

| 파라미터 | 타입 | 설명 | 비고 |
| --- | --- | --- | --- |
| source | String | 송금 국가 | 이용 가능한 통화: USD |
| target | String | 수취 국가 | 이용 가능한 통화: KRW, JPY, PHP |
| remittance | Number | 송금액 | 입력 가능한 금액: 0~10000 |

### **응답**

변환된 수취금액을 반환합니다.

```java
1277303.792
```

## 환전 API

고객에게 환전 금액이 주어지며  DB에 거래 내용이 기록됩니다.

### 요청

**URL**

```java
GET /exchange/{source}/{target}?remittance={remittance}
```

**파라미터**

| 파라미터 | 타입 | 설명 | 비고 |
| --- | --- | --- | --- |
| source | String | 송금 국가 | 이용 가능한 통화: USD |
| target | String | 수취 국가 | 이용 가능한 통화: KRW, JPY, PHP |
| remittance | Number | 송금액 | 입력 가능한 금액: 0~10000 |

### **응답**

환전된 금액과 화폐를 반환합니다.

```java
{
    remittance: 1277303.792,
    currency: KRW
}
```

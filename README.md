## 시연 영상
- 차트: https://youtu.be/sH0vQYZbY-s

## 아키텍처
![스크린샷 2024-11-18 204533](https://github.com/user-attachments/assets/0f2e1eb6-d5bb-4f71-b973-383cf0c3398c)

## 프로젝트 목표
- 가상 거래소 시스템을 구현하는 것이 목표 입니다.
- scale-out 구조로 설계하여, 대용량 트래픽 처리가 가능하도록 구현하는 것이 목표입니다.
- 서버의 성능을 최적화하는 것이 목표 입니다.

## 기술적 이슈 해결
- [#1] Redis에 JSON 포멧 데이터 저장의 비효율성 해결
- [#2] 주문 체결 시 많은 양의 주문에 대한 쓰기락을 청크 단위의 쓰기락과 병렬 처리로 병목 개선
- [#3] 과거 모든 캔들 조회 시 Lazy Loading을 적용하여 최초 로딩 속도 개선
- [#4] API 서버에 최근 캔들에 대한 캐시를 적용하여 최초 로딩 속도 개선 (성능 테스트 과정)

## TODO
- 로드밸런싱을 사용하여 http 요청 성능 테스트
- 대용량 웹 소켓 커넥션 처리

package io.hhplus.tdd.point;
// 테스트에서 객체 생성의 반복코드를 줄이고 유연하게 대웅하기 위해 Factory 패턴 사용
public class UserPointFactory {

    public static UserPoint defaultUserPoint() {
        return new UserPoint(1L, 0L, System.currentTimeMillis());
    }

    public static UserPoint withPoints(long points) {
        return new UserPoint(1L, points, System.currentTimeMillis());
    }
}

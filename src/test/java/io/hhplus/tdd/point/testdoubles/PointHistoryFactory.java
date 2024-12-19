package io.hhplus.tdd.point.testdoubles;

import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.pointhistory.PointHistory;

// 테스트에서 객체 생성의 반복코드를 줄이고 유연하게 대웅하기 위해 Factory 패턴 사용
public class PointHistoryFactory {

    public static PointHistory defaultPointHistory_CHARGE() {
        return new PointHistory(1L, 1L,1000, TransactionType.CHARGE, System.currentTimeMillis());
    }

    public static PointHistory defaultPointHistory_USE() {
        return new PointHistory(2L, 1L,1000,TransactionType.USE, System.currentTimeMillis());
    }

    public static PointHistory withUserIdAndPoint_CHARGE(long userId , long point) {
        return new PointHistory(3L, userId, point, TransactionType.CHARGE,System.currentTimeMillis());
    }
    public static PointHistory withUserIdAndPoint_USE(long userId , long point) {
        return new PointHistory(4L, userId, point, TransactionType.USE,System.currentTimeMillis());
    }
}

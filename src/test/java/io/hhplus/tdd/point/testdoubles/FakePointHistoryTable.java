package io.hhplus.tdd.point.testdoubles;


import io.hhplus.tdd.database.PointHistoryRepository;
import io.hhplus.tdd.point.pointhistory.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * stub을 사용하기 위해 대역 클래스 생성
 */
@Component
public class FakePointHistoryTable implements PointHistoryRepository {
    private final List<PointHistory> table = new ArrayList<>();
    private long cursor = 1;

    public PointHistory insert(long userId, long amount, TransactionType type, long updateMillis) {
        throttle(300L);
        PointHistory pointHistory = new PointHistory(cursor++, userId, amount, type, updateMillis);
        table.add(pointHistory);
        return pointHistory;
    }

    public List<PointHistory> selectAllByUserId(long userId) {
        return table.stream().filter(pointHistory -> pointHistory.userId() == userId).toList();
    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {

        }
    }
}

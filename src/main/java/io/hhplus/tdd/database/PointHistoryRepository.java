package io.hhplus.tdd.database;

import io.hhplus.tdd.point.pointhistory.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PointHistoryRepository {

    public PointHistory insert(long userId, long amount, TransactionType type, long updateMillis);
    public List<PointHistory> selectAllByUserId(long userId);

}

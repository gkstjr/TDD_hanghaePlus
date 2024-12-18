package io.hhplus.tdd.point.pointhistory;

import io.hhplus.tdd.database.PointHistoryRepository;
import io.hhplus.tdd.point.TransactionType;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public interface PointHistoryService {

    public List<PointHistory> selectAllByUserId(Long userId);
    public PointHistory insert(long userId, long point, TransactionType transactionType, long updateTime);

}

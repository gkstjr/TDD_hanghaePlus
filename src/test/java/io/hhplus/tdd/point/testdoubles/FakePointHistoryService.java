package io.hhplus.tdd.point.testdoubles;

import io.hhplus.tdd.database.PointHistoryRepository;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.pointhistory.PointHistory;
import io.hhplus.tdd.point.pointhistory.PointHistoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;


public class FakePointHistoryService implements PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;

    public FakePointHistoryService(PointHistoryRepository pointHistoryRepository) {
        this.pointHistoryRepository = pointHistoryRepository;
    }

    public List<PointHistory> selectAllByUserId(Long userId) {
        validateNotNull(userId,"userId");

        List<PointHistory> histories = pointHistoryRepository.selectAllByUserId(userId);
        if(histories.isEmpty()) throw new NoSuchElementException("포인트 내역 조회 결과가 없습니다.");


        return histories;
    }
    public PointHistory insert(long userId, long point, TransactionType transactionType, long updateTime) {
        return pointHistoryRepository.insert(userId , point,transactionType , updateTime);
    }

    private  void validateNotNull(Object param , String paramName) {
        if(Objects.isNull(param)) throw  new IllegalArgumentException(paramName + "은 NULL이면 안됩니다.");
    }


}

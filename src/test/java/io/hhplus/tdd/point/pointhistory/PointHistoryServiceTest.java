package io.hhplus.tdd.point.pointhistory;

import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.testdoubles.FakePointHistoryTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

/**
 * Repository의 의존하지 않게 하기 위해, 인터페이스를 사용하여 추상화한 후 테스트에서 Mock 또는 Stub을 사용
 * 학습을 위해 UserPoint 관련 테스트는 Mock <-> PointHistory 관련 테스트는 stub 으로 작성해보자
 */
public class PointHistoryServiceTest {

    private final FakePointHistoryTable fakePointHistoryTable = new FakePointHistoryTable();
    private final PointHistoryServiceImpl pointHistoryService = new PointHistoryServiceImpl(fakePointHistoryTable);


    @Test
    @DisplayName("포인트 내역 조회 결과가 없을 때 ")
    public void selectById_zero() {
        //given
        long userId = 1;
        //when
        //then
        assertThatThrownBy(() -> pointHistoryService.selectAllByUserId(userId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("포인트 내역 조회 결과가 없습니다.");
    }

    @Test
    @DisplayName("포인트 내역 추가 조회 성공테스트")
    public void insert_pointHistory() {
        //given
        long userId = 1;
        //when
        PointHistory pointHistory1 = pointHistoryService.insert(userId , 1000 , TransactionType.CHARGE , System.currentTimeMillis());
        PointHistory pointHistory2 = pointHistoryService.insert(userId , 1000 , TransactionType.CHARGE , System.currentTimeMillis());
        //데이터 조회
        List<PointHistory> histories = pointHistoryService.selectAllByUserId(userId);

        //then
        assertThat(histories).contains(pointHistory1,pointHistory2);
    }
}

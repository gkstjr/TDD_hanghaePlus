package io.hhplus.tdd.point.userpoint;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.UserPointService;
import io.hhplus.tdd.point.pointhistory.PointHistory;
import io.hhplus.tdd.point.pointhistory.PointHistoryServiceImpl;
import io.hhplus.tdd.point.testdoubles.FakePointHistoryService;
import io.hhplus.tdd.point.testdoubles.FakePointHistoryTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository의 의존하지 않게 하기 위해, 인터페이스를 사용하여 추상화한 후 테스트에서 Mock 또는 Stub을 사용
 * 학습을 위해 UserPoint 관련 테스트는 Mock <-> PointHistory 관련 테스트는 stub 으로 작성해보자
 */
public class UserPointServiceTest {

    //
    private final UserPointTable userPointTable = mock(UserPointTable.class);
    private final FakePointHistoryService pointHistoryService = new FakePointHistoryService(new FakePointHistoryTable());
    private final UserPointService userPointService = new UserPointService(userPointTable,pointHistoryService);

    //특정 id 의 UserPoint가 없을 때 구현되어 있는 UserPointTable에 맞춰 포인트가 0인 UserPoint 반환
    @Test
    @DisplayName("포인트 조회 데이터가 없을 때(포인트가 0인 UserPoint반환)")
    public void findById_returnZeroPoint() {
        //given
        Long userId = 5L;
        given(userPointTable.selectById(userId))
                .willReturn(UserPoint.empty(userId));
        //when
        UserPoint userPoint = userPointService.selectById(userId);

        //then
        assertEquals(0 , userPoint.point());
    }
    //다른 서비스와의 연결 시 null이 파라미터인 조회 메소드를 호출할 수도 있을거라고 생각해서 null체크 테스트 작성
    @Test
    @DisplayName("id가 null인 pointUser조회 시")
    public void findByIdWithNull() {
        //then
        assertThrows(
                Exception.class,
                () -> userPointService.selectById(null)
        );
    }
    //PointUser 데이터 조회 기능 테스트
    @Test
    @DisplayName("포인트 조회 성공 테스트")
    public void findById() {
        //given
        long userId = 1L;
        long mount = 10000;
        long current = System.currentTimeMillis();

        given(userPointTable.selectById(userId))
                .willReturn(new UserPoint(userId , mount , current));

        //when
        UserPoint findUserPoint = userPointService.selectById(userId);
        //then
        assertEquals(findUserPoint.id() , userId);
    }

    //PointUser 충전 기능 테스트
    @Test
    @DisplayName("PointUser 충전 서비스 성공 로직 테스트")
    public void charge_point() {
        //given
        long chargePoint = 100000;
        UserPoint userPoint = new UserPoint(1L , 10000 , 100000);

        long expectSum = userPoint.point() + chargePoint;


        given(userPointTable.selectById(userPoint.id()))
                .willReturn(userPoint);
        given(userPointTable.insertOrUpdate(userPoint.id() , expectSum))
                .willReturn(new UserPoint(userPoint.id(), expectSum, userPoint.updateMillis()));

        //when
        UserPoint chargedPoint = userPointService.charge(userPoint.id() , chargePoint);

        //then
        assertEquals(expectSum,chargedPoint.point());
    }

    @Test
    @DisplayName("PointUser 사용 서비스 로직 성공 테스트")
    public void use_point() {
        //given
        long point1 = 5000;
        long point2 = 10000;
        UserPoint userPoint = new UserPoint(1L , 10000 , 100000);

        given(userPointTable.selectById(userPoint.id()))
                .willReturn(userPoint);
        given(userPointTable.insertOrUpdate(userPoint.id() , userPoint.point() - point1))
                .willReturn(new UserPoint(userPoint.id(), userPoint.point() - point1, userPoint.updateMillis()));

        //when
        UserPoint userPoint1 = userPointService.use(userPoint.id() , point1);

        given(userPointTable.insertOrUpdate(userPoint.id() , userPoint.point() - point2))
                .willReturn(new UserPoint(userPoint.id(), userPoint.point() - point2, userPoint.updateMillis()));

        UserPoint userPoint2 = userPointService.use(userPoint.id() , point2);

        //then
        assertEquals(userPoint.point() - point1 , userPoint1.point());
        assertEquals(userPoint.point() - point2 , userPoint2.point());
    }

    @Test
    @DisplayName("포인트 충전 후 포인트 내역 추가 테스트")
    public void charge_history(){
        //given
        long chargePoint = 100000;
        UserPoint userPoint = new UserPoint(1L , 10000 , 100000);

        long expectSum = userPoint.point() + chargePoint;


        given(userPointTable.selectById(userPoint.id()))
                .willReturn(userPoint);
        given(userPointTable.insertOrUpdate(userPoint.id() , expectSum))
                .willReturn(new UserPoint(userPoint.id(), expectSum, userPoint.updateMillis()));

        //when
        UserPoint chargedPoint = userPointService.charge(userPoint.id() , chargePoint);
        List<PointHistory> histories = pointHistoryService.selectAllByUserId(userPoint.id());

        //then
        assertEquals(histories.get(0).amount() , chargedPoint.point());
    }
}

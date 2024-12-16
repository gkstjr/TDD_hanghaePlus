package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository의 의존하지 않게 하기 위해, 인터페이스를 사용하여 추상화한 후 테스트에서 Mock 또는 Stub을 사용
 * UserPoint 관련 테스트는 Mock <-> PointHistory 관련 테스트는 stub 으로 작성해보자
 */
public class UserPointServiceTest {

    //
    private final UserPointTable userPointTable = mock(UserPointTable.class);
    private final UserPointService userPointService = new UserPointService(userPointTable);

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
    //PointUser 데이터 추가 후 조회 기능 테스트
    @Test
    @DisplayName("포인트 조회 성공 테스트")
    public void findById() {
        //given
        long userId = 1L;
        long mount = 10000;
        long current = System.currentTimeMillis();
        given(userPointTable.insertOrUpdate(userId , mount))
                .willReturn(new UserPoint(userId , mount , current));

        given(userPointTable.selectById(userId))
                .willReturn(new UserPoint(userId , mount , current));


        //when
        UserPoint userPoint = userPointService.insert(userId , mount);
        UserPoint findUserPoint = userPointService.selectById(userId);
        //then
        assertEquals(findUserPoint.id() , userPoint.id());
    }

}

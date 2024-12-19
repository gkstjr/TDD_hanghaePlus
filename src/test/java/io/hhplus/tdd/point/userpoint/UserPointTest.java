package io.hhplus.tdd.point.userpoint;

import io.hhplus.tdd.point.UserPoint;
import org.apache.catalina.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

public class UserPointTest {

    //포인트 충전 시 최대 한도(100만포인트)를 넘어섰을 때
    @Test
    @DisplayName("충전 시 최대한도를 초과할 때")
    public void charge_maxLimit_over() {
        //given
        UserPoint userPoint = new UserPoint(1L , 10000 , 100000);
        long chargePoint = 1000000;
        //when
        //then
        assertThrows(
                IllegalArgumentException.class,
                () -> userPoint.charge(chargePoint)
        );
    }

    //포인트 충전 시 0 미만일 때 Controller 요청 검증과 별개로 userPoint 객체에서 검증
    @Test
    @DisplayName("충전 포인트가 0미만일 때")
    public void charge_zero_under() {
        //given
        UserPoint userPoint = new UserPoint(1L , 10000 , 100000);
        //when
        //then
        assertThrows(
                IllegalArgumentException.class,
                () -> userPoint.charge(0)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> userPoint.charge(-100)
        );
    }

    @Test
    @DisplayName("포인트 충전 성공 테스트")
    public void charge_success() {
        //given
        UserPoint userPoint = new UserPoint(1L , 500 ,10000);
        long chargePoint = 100000L;

        //when
        UserPoint chargedPoint = userPoint.charge(chargePoint);

        //then
        assertEquals(userPoint.point() + chargePoint ,chargedPoint.point());
    }

    @Test
    @DisplayName("[포인트 사용] 사용한 포인트가 0 미만 일 때 예외 처리")
    public void use_zero_under() {
        //given
        UserPoint userPoint = new UserPoint(1L , 500 , 10000);
        long usePoint = -1;

        //when
        //then
        assertThatThrownBy(() -> userPoint.use(usePoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용할 포인트는 0이상이어야 합니다.");
    }

    @Test
    @DisplayName("[포인트 사용] 사용한 포인트가 보유 포인트보다  클 때(잔고 부족 정책)")
    public void use_overPoint() {
        //given
        long usePoint = 1000;
        UserPoint userPoint = new UserPoint(1L , 500 , 10000);
        //when
        //then
        assertThatThrownBy(() -> userPoint.use(usePoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용할 포인트(1000)가 보유 포인트(500)보다 큽니다.");
    }
    //!! 추가정책 - 1회 포인트 사용 한도 -10만포인트
    @Test
    @DisplayName("[포인트 사용] 1회 포인트 사용한도(10만 포인트)를 초과 했을 때" )
    public void use_ExceedsLimit() {
        //given
        long usePoint = 100001;
        UserPoint userPoint = new UserPoint(1L , 1000000 , System.currentTimeMillis());
        //when
        //then
        assertThatThrownBy(() -> userPoint.use(usePoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1회 포인트 사용한도(100000)를 초과했습니다.");
    }

    @Test
    @DisplayName("[포인트 사용] 포인트 사용 성공 케이스")
    public void use_success() {
        //given
        long point1 = 5000;
        long point2 = 100000;
        UserPoint userPoint = new UserPoint(1L , 1000000 , System.currentTimeMillis());

        //when
        UserPoint userPoint1 = userPoint.use(point1);
        UserPoint userPoint2 = userPoint.use(point2);

        //then
        assertEquals(userPoint.point() - point1 , userPoint1.point());
        assertEquals(userPoint.point() - point2 , userPoint2.point());
    }
}

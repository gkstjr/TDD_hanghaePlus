package io.hhplus.tdd.point.userpoint;

import io.hhplus.tdd.point.UserPoint;
import org.apache.catalina.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
                Exception.class,
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
                Exception.class,
                () -> userPoint.charge(0)
        );
        assertThrows(
                Exception.class,
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
//
//    @Test
//    @DisplayName("[포인트 사용] 사용한 포인트가 0 미만 일 때 예외 처리")
//    public
}

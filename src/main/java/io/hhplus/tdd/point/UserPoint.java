package io.hhplus.tdd.point;

import org.apache.catalina.User;

public record UserPoint(
        long id,
        long point,
        long updateMillis

) {
    //TODO 어떻게 하면 유연성있게 관리할 수 있을까?
    private static final long MAX_POINT = 1000000L; //최대 100만 포인트
    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    //유저포인트에 관한 검증 책임
    public UserPoint {
        if(point < 0) throw new IllegalArgumentException("포인트는 0 이상이어야 합니다.");
        if(point > MAX_POINT) throw new IllegalArgumentException("포인트가 최대 한도 " + MAX_POINT +"을 초과하면 안됩니다.");
    }


    public UserPoint charge(long chargePoint) {
        if(chargePoint <= 0) throw new IllegalArgumentException("충전 할 포인트는 0보다 커야 합니다.");
        long sum = chargePoint + point;
        //overflow 발생 가능성 처리
        if(sum > MAX_POINT || sum < 0) throw new IllegalArgumentException("충전 후 포인트가 최대 한도 " + MAX_POINT +"을 초과하면 안됩니다.");

        return new UserPoint(this.id , sum , this.updateMillis);
    }
}

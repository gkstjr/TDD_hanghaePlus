package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
/**
 * Repository의 의존하지 않게 하기 위해, 인터페이스를 사용하여 추상화한 후 테스트에서 Mock 또는 Stub을 사용
 */
public class UserPointTest {
    /**
        특정 id 의 UserPoint가 없을 때 구현되어 있는 UserPointTable에 맞춰 포인트가 0인 UserPoint 반환
     */
    @Test
    @DisplayName("포인트 조회 데이터가 없을 때")
    public void findById_returnZeroPoint() {

    }

}

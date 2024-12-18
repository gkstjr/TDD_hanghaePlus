package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.pointhistory.PointHistoryService;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserPointService {

    private final UserPointTable userPointTable;
    private final PointHistoryService pointHistoryService;
    public UserPointService(UserPointTable userPointTable , PointHistoryService pointHistoryService) {
        this.userPointTable = userPointTable;
        this.pointHistoryService = pointHistoryService;
    }

    public UserPoint selectById(Long userId) {
        validateNotNull(userId , "userId");

        return userPointTable.selectById(userId);
    }

    public UserPoint insert(Long userId, Long mount) {
        validateNotNull(userId , "userId");

        return userPointTable.insertOrUpdate(userId , mount);
    }

    public UserPoint charge(Long userId, long chargePoint) {
        validateNotNull(userId , "userId");

        UserPoint findUserPoint = userPointTable.selectById(userId);
        //userPoint객체에서 유효값 검증 책임
        UserPoint chargedUserPoint = findUserPoint.charge(chargePoint);
        pointHistoryService.insert(chargedUserPoint.id(), chargedUserPoint.point(),TransactionType.CHARGE , System.currentTimeMillis());

        return userPointTable.insertOrUpdate(chargedUserPoint.id(), chargedUserPoint.point());
    }

    public UserPoint use(Long userId, Long usePoint) {
        validateNotNull(userId , "userId");
        validateNotNull(usePoint , "usePoint");

        UserPoint userPoint = Optional.ofNullable(userPointTable.selectById(userId))
                .orElseThrow(() -> new IllegalArgumentException("조회된 userPoint가 없습니다."));

        // userPoint가 record타입으로 불변객체이기 때문에 새로운 객체 선언
        userPoint = userPoint.use(usePoint);

        userPointTable.insertOrUpdate(userPoint.id() , userPoint.point());

        return userPoint;
    }

    private  void validateNotNull(Object param , String paramName) {
        if(Objects.isNull(param)) throw  new IllegalArgumentException(paramName + "은 NULL이면 안됩니다.");
    }
}

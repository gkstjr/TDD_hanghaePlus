package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.pointhistory.PointHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class UserPointService {
    private static final Logger log = LoggerFactory.getLogger(UserPointService.class);
    private final ConcurrentHashMap<Long , Lock> userLockMap = new ConcurrentHashMap<>();
    private final UserPointTable userPointTable;
    //포인트 내역에 관한 책임 분리를 위해 포인트 히스토리 DB가 아닌 Service 계층을 추상화 선택
    private final PointHistoryService pointHistoryService;
    public UserPointService(UserPointTable userPointTable , PointHistoryService pointHistoryService) {
        this.userPointTable = userPointTable;
        this.pointHistoryService = pointHistoryService;
    }
    //사용자별로 Lock 을 생성해서 동시성 제어(DB락 접근 시점 적용을 위해 공정성 부여)
    private Lock getLockUser(long userId) {
        return userLockMap.computeIfAbsent(userId , value -> new ReentrantLock(true));
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
        Lock lock = getLockUser(userId);
        lock.lock();
        try {

            UserPoint findUserPoint = Optional.ofNullable(userPointTable.selectById(userId))
                    .orElseThrow(() -> new IllegalArgumentException("조회된 UserPoint가 없습니다."));
            //유효값 검증 책임은 UserPoint 객체 책임
            UserPoint chargedUserPoint = findUserPoint.charge(chargePoint);
            pointHistoryService.insert(chargedUserPoint.id(), chargePoint,TransactionType.CHARGE , System.currentTimeMillis());
//            log.info(userId + "번 Id 사용자의 포인트 충전을 시작. charge : " + chargePoint + " 합계 : " + chargedUserPoint.point());

            return userPointTable.insertOrUpdate(chargedUserPoint.id(), chargedUserPoint.point());
        } finally {
            lock.unlock();
//            log.info(userId + "번 Id 사용자의 포인트 충전 lock 해제. charge : " + chargePoint);
        }
    }

    public UserPoint use(Long userId, Long usePoint) {
        validateNotNull(userId , "userId");
        validateNotNull(usePoint , "usePoint");
        Lock lock = getLockUser(userId);
        lock.lock();
        try {

            UserPoint userPoint = Optional.ofNullable(userPointTable.selectById(userId))
                    .orElseThrow(() -> new IllegalArgumentException("조회된 UserPoint가 없습니다."));

            // userPoint가 record타입으로 불변객체이기 때문에 새로운 객체 선언
            userPoint = userPoint.use(usePoint);

            userPointTable.insertOrUpdate(userPoint.id() , userPoint.point());
            pointHistoryService.insert(userId , usePoint,TransactionType.USE,System.currentTimeMillis());
//            log.info(userId + "번 Id 사용자의 포인트 사용 시작 use : " + usePoint + " 합계 : " + userPoint.point());

            return userPoint;
        }finally {
            lock.unlock();
//            log.info(userId + "번 Id 사용자의 포인트 사용 lock 해제. use : " + usePoint);
        }
    }

    private  void validateNotNull(Object param , String paramName) {
        if(Objects.isNull(param)) throw  new IllegalArgumentException(paramName + "은 NULL이면 안됩니다.");
    }
}

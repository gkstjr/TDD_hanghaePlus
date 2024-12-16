package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserPointService {

    private final UserPointTable userPointTable;
    public UserPointService(UserPointTable userPointTable) {
        this.userPointTable = userPointTable;
    }

    public UserPoint selectById(Long userId) {
        if(Objects.isNull(userId)) throw new IllegalArgumentException("userId는 null이면 안됩니다.");
        return userPointTable.selectById(userId);
    }

    public UserPoint insert(Long userId, Long mount) {
        if(Objects.isNull(userId)) throw new IllegalArgumentException("userId는 null이면 안됩니다.");
        return userPointTable.insertOrUpdate(userId , mount);
    }
}

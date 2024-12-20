package io.hhplus.tdd.point;

import io.hhplus.tdd.point.pointhistory.PointHistory;
import io.hhplus.tdd.point.pointhistory.PointHistoryService;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
@Validated
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final UserPointService userPointService;
    private final PointHistoryService pointHistoryService;
    public PointController(UserPointService userPointService , PointHistoryService pointHistoryService) {
        this.userPointService = userPointService;
        this.pointHistoryService = pointHistoryService;
    }
    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPoint point(
            @PathVariable @Min(value = 1, message = "ID는 1 이상의 값이어야 합니다.") long id
    ) {
        return userPointService.selectById(id);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable @Min(value = 1, message = "ID는 1 이상의 값이어야 합니다.") long id
    ) {
        return pointHistoryService.selectAllByUserId(id);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(
            @PathVariable  @Min(value = 1, message = "ID는 1 이상의 값이어야 합니다.") long id,
            @RequestBody long amount
    ) {
        return userPointService.charge(id , amount);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable @Min(value = 1, message = "ID는 1 이상의 값이어야 합니다.") long id,
            @RequestBody @Min(value = 1, message = "포인트 사용은 1 이상의 값이어야 합니다.") long amount
    ) {
        return userPointService.use(id , amount);
    }
}

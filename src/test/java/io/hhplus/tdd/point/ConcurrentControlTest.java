package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 * 동시성 제어 통합테스트
 * - 멀테스레드 환경을 가정하기 위해 ExecutorService를 사용해서 여러 스레드에서 동시 요청이 발생 하게끔 환경 만들기.
 */

@SpringBootTest
public class ConcurrentControlTest {

    @Autowired
    private UserPointService userPointService;
    private static final Logger log = LoggerFactory.getLogger(UserPointService.class);

    //동시성 제어 주석 처리하고 테스트 해보니 테스트가 실패 -> 충전 요청이 들어와서 처리 하는 도중에 사용 요청이 들어오면 충전 요청 처리 전 포인트에 사용을 넣게 되기 때문에 기대값이 달라지는구나!
    @Test
    @DisplayName("같은 사용자에 대한 동시 요청(충전(500) ,사용(100) 각각 10번)이 들어왔을 때 총합 포인트가 14000원을 반환한다.")
    void concurrentReqForSameUser() throws Exception {
        //given
        long userId = 1;
        long currentPoint = 10000;
        long chargePoint = 500;
        long usePoint = 100;
        userPointService.insert(userId, currentPoint);

        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        //when
        for (int i = 0; i < threads; i++) {
            int threadId = i; //log를 보면서 각 요청별 구분해서 보고 싶어서 포인트에 더해 뒷자리로 구분
            executor.submit(() -> userPointService.charge(userId, chargePoint + threadId));
            executor.submit(() -> userPointService.use(userId, usePoint + threadId));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        //then
        long expectPoint = currentPoint + (chargePoint * threads) - (usePoint * threads);
        UserPoint userPoint = userPointService.selectById(userId);

        assertEquals(expectPoint, userPoint.point());
    }

    //순차적 접근 테스트를 executor 로 사용하려고 하니 스레드 실행 순서를 제어하는 데 한계가 있는 거 같아 CountDownLatch를 같이 사용하여 스레드 실행 순서 제어 향상
    @Test
    void 같은사용자에게_충전또는사용_동시요청시_DB락접근시점순서대로반환() throws Exception {    //TODO 하헌우 코치님 멘토링을 듣고 테스트명에 대해 방향성이 잡혔다. (과제 제출날이라 이미 작성한 코드는.. 다음주부터 통일하자!)
        // given(충전 , 사용 포인트는  테스트의 흐름을 파악하기 위해 메소드 인자에 하드코딩
        long userId = 1;
        long currentPoint = 10000;

        userPointService.insert(userId, currentPoint);

        int threads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        //각 요청마다 대기 , 신호 처리를 위한 배열
        CountDownLatch[] latches = new CountDownLatch[threads];
        for (int i = 0; i < threads; i++) {
            latches[i] = new CountDownLatch(1);
        }
        //처리 순서 기록 리스트
        List<String> processOrder = Collections.synchronizedList(new ArrayList<>());

        // when
        for (int i = 0; i < threads; i++) {
            int threadId = i;

            // 충전 요청
            executor.submit(() -> {
                try {
                    if (threadId > 0){
                        latches[threadId - 1].await(); //이전 스레드 요청이 완료할 때까지 대기하자
                    }
                    userPointService.charge(userId, 500);
                    processOrder.add("충전 : " + threadId);

//                    log.info("충전 요청 처리: " + Thread.currentThread().getName() + "\n");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    //초기 설정을 1로 했기 때문에 각 스레드 요청별 한번의 countDown이 호출되면 대기가 풀리는 형태 -> 응용해서 똑같은 사용자에게 특정 요청에 대해 더 많은 대기를 정할 수 있겠네?
                    latches[threadId].countDown();
                }
            });
            // 사용 요청
            executor.submit(() -> {
                try {
                    latches[threadId].await(); // 현재 스레드의 충전 요청 완료될 때까지 대기
                    userPointService.use(userId, 100L);
                    processOrder.add("사용 : " + threadId);

//                    log.info("사용 요청 처리: " + Thread.currentThread().getName() + "\n");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    if (threadId + 1 < threads) {
                        latches[threadId + 1].countDown();
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // then
        long expectedPoint = currentPoint + (500 * threads) - (100 * threads);
        UserPoint userPoint = userPointService.selectById(userId);

        //사용자별 요청 하나씩 처리 검증
        assertEquals(expectedPoint, userPoint.point());

        // 순차적 실행 검증
        for (int i = 0; i < threads; i++) {
            assertEquals("충전 : " + i, processOrder.get(i * 2));
            assertEquals("사용 : " + i, processOrder.get(i * 2 + 1));
        }
    }
}

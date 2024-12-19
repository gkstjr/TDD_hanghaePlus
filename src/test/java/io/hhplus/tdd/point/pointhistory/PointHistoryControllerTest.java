package io.hhplus.tdd.point.pointhistory;

import io.hhplus.tdd.point.UserPointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 *  NOTE
 * - 비즈니스 로직 관련 검증이 아닌 단순 입력 검증(ex. 타입 , 음수값) 처리는 Controller에서 Validated 사용하여 검증
 *  (이유) 1.입력값 검증을 빠르게 하여 성능 향상
 *        2.컨트롤러의 책임이 확실해진다고 생각했습니다.
 *  (구현 후 의문점)
 *  - 컨트롤러에서 요청에 대한 유효성 검증만이 목표였던 테스트인데... Controller 구현 완료 후 의존하고 있는 Service까지 같이 테스트 하는
 *    통합 테스트의 형태로 변경 된 거 같다.. -> Controller의 메서드 레벨 테스트를 작성하는 방법이 더 내가 원했던 목표에 가까운 방법이였던 거 같다.
 *
 */
@WebMvcTest
public class PointHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PointHistoryServiceImpl pointHistoryService;
    @MockBean
    private UserPointService userPointService;
    //클라이언트의 ID 입력값이 0이하이면 예외 메시지 반환
    @Test
    @DisplayName("[포인트 내역 조회]클라이언트의 ID 입력값이 0이하이면 예외 메시지 반환")
    public void IdIsZeroOrNegative_ThrowException() throws Exception {
        long zeroId = 0L;
        long negativeId = -100L;
        //포인트 조회
        mockMvc.perform(get("/point/{id}/histories",zeroId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ID는 1 이상의 값이어야 합니다."));
        mockMvc.perform(get("/point/{id}/histories",negativeId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ID는 1 이상의 값이어야 합니다."));
    }
    //클라이언트의 ID 입력값이 정수 형태가 아니면 예외 메시지 반환 ex) 실수 , 문자열
    @Test
    @DisplayName("[포인트내역 조회]클라이언트의 ID 입력값이 정수 형태가 아니면 예외 메시지 반환")
    public void IdIsNotNumber_ThrowException() throws Exception {
        String userStrId = "dfsg";
        double userDoubleId = 0.02;

        mockMvc.perform(get("/point/{id}/histories",userStrId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("입력값 타입이 잘못 되었습니다."));

        mockMvc.perform(get("/point/{id}/histories",userDoubleId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("입력값 타입이 잘못 되었습니다."));
    }
}

package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 *  NOTE
 * - 비즈니스 로직 관련 검증이 아닌 단순 입력 검증(ex. 타입 , 음수값) 처리는 Controller에서 Validated 사용하여 검증
 *  (이유) 1.입력값 검증을 빠르게 하여 성능 향상
 *        2.컨트롤러의 책임이 확실해진다고 생각했습니다.
 */
@WebMvcTest(PointController.class)
public class UserPointControllerTest {

    @Autowired
    private MockMvc mockMvc;


    //클라이언트의 ID 입력값이 0이하이면 예외 메시지 반환
    @Test
    @DisplayName("클라이언트의 ID 입력값이 0이하이면 예외 메시지 반환")
    public void IdIsZeroOrNegative_ThrowException() throws Exception {
        mockMvc.perform(get("/point/0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ID는 1 이상의 값이어야 합니다."));
        mockMvc.perform(get("/point/-100"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ID는 1 이상의 값이어야 합니다."));
    }
    //클라이언트의 ID 입력값이 정수 형태가 아니면 예외 메시지 반환 ex) 실수 , 문자열
    @Test
    @DisplayName("클라이언트의 ID 입력값이 정수 형태가 아니면 예외 메시지 반환")
    public void IdIsNotNumber_ThrowException() throws Exception {
        mockMvc.perform(get("/point/dsfs"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("입력값 타입이 잘못 되었습니다."));

        mockMvc.perform(get("/point/0.02"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("입력값 타입이 잘못 되었습니다."));
    }

    //포인트 조회 Controller 요청 성공 테스트 케이스
    @Test
    @DisplayName("포인트 조회 Controller 요청 성공 테스트 케이스")
    public void UserPointController_SUCCESS() throws Exception {
        mockMvc.perform(get("/point/2"))
                .andExpect(status().is2xxSuccessful());


        mockMvc.perform(get("/point/5555555555555555"))
                .andExpect(status().is2xxSuccessful());
    }
}

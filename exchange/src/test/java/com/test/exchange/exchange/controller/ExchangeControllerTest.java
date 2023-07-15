package com.test.exchange.exchange.controller;

import com.test.exchange.exchange.domain.Currency;
import com.test.exchange.exchange.dto.ExchangeMoneyResponse;
import com.test.exchange.exchange.service.ExchangeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ExchangeController.class)
class ExchangeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeService exchangeService;

    @DisplayName("환율을 조회합니다.")
    @Test
    void getExchangeRate() throws Exception{
        // given
        Currency source = Currency.USD;
        Currency target = Currency.KRW;
        // stubbing
        double expectedValue = 1300;
        BDDMockito.given(exchangeService.getExchangeRate(source,target))
                        .willReturn(expectedValue);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/rate/{source}/{target}",source,target)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedValue));
    }

    @DisplayName("환전 금액을 계산합니다.")
    @Test
    void getExchangedRemittance() throws Exception{
        // given
        Currency source = Currency.USD;
        Currency target = Currency.KRW;
        // stubbing
        double expectedValue = 130_000;
        BDDMockito.given(exchangeService.getCalculatedRemittance(eq(source),eq(target),anyDouble()))
                .willReturn(expectedValue);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/calculate/{source}/{target}",source,target)
                        .param("remittance","100")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedValue));
    }

    @DisplayName("환전 거래를 실행합니다.")
    @Test
    void exchangeMoney() throws Exception{
        // given
        Currency source = Currency.USD;
        Currency target = Currency.KRW;
        // stubbing
        ExchangeMoneyResponse expectedValue = new ExchangeMoneyResponse(1000, target);
        BDDMockito.given(exchangeService.exchangeMoney(eq(source),eq(target),anyDouble()))
                .willReturn(expectedValue);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/exchange/{source}/{target}",source,target)
                        .param("remittance","100")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remittance").value(1000))
                .andExpect(jsonPath("$.currency").value(target.toString()));
    }
}
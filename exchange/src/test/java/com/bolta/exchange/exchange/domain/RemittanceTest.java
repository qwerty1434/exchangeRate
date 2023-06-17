package com.bolta.exchange.exchange.domain;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class RemittanceTest {

    @DisplayName("0이상 10_000이하의 값이 주어지면 Remittance 생성 성공")
    @ParameterizedTest
    @ValueSource(doubles = {0, 0.1, 134.567, 9_999.999, 10_000.0})
    void testCreatingRemittanceWithValidValue(double value){
        assertThat(Remittance.from(value)).isNotNull();
    }

    @DisplayName("0미만, 10_000초과의 값이 주어지면 IllegalArgumentException 발생")
    @ParameterizedTest
    @ValueSource(doubles = {-10, -0.001, 10_001, Double.MAX_VALUE})
    void testCreatingRemittanceWithInvalidValue(double value){
        assertThatThrownBy(() -> Remittance.from(value))
                        .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("환율이 주어졌을 때 Remittance의 calculate는 환율을 적용한 값을 반환")
    @Test
    void testCalculatingRemittance(){
        double remittanceValue = 10_000;
        double exchangeRate = 0.6;
        Remittance remittance = Remittance.from(remittanceValue);
        assertThat(remittance.calculate(exchangeRate)).isEqualTo(remittanceValue * exchangeRate);
    }

}
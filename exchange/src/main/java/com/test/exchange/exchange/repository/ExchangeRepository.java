package com.test.exchange.exchange.repository;

import com.test.exchange.exchange.domain.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRepository extends JpaRepository<Exchange,Long> {
}

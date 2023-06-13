package com.bolta.exchange.exchange.repository;

import com.bolta.exchange.exchange.domain.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRepository extends JpaRepository<Exchange,Long> {
}

package com.bolta.exchange.exchange.controller;

import com.bolta.exchange.exchange.domain.Currency;
import com.bolta.exchange.exchange.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ExchangeController {
    private final ExchangeService exchangeService;

    @GetMapping("/rate/{source}/{target}")
    public ResponseEntity getExchangeRate(@PathVariable Currency source, @PathVariable Currency target){
        return ResponseEntity.ok().body(exchangeService.getExchangeRate(source,target));
    }
    @GetMapping("/calculate/{source}/{target}")
    public ResponseEntity getExchangedRemittance(@PathVariable Currency source, @PathVariable Currency target, @RequestParam double price){
        return ResponseEntity.ok().body(exchangeService.getCalculatedRemittance(source,target,price));
    }
    @GetMapping("/exchange/{source}/{target}")
    public ResponseEntity exchangeMoney(@PathVariable Currency source, @PathVariable Currency target, @RequestParam double price){
        return ResponseEntity.ok().body(exchangeService.exchangeMoney(source,target,price));
    }
}

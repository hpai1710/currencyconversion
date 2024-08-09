package com.example.currency_conversion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface Repository extends JpaRepository<CurrencyConversion, Long>, JpaSpecificationExecutor<CurrencyConversion>, CrudRepository<CurrencyConversion,Long> {

//    List<CurrencyConversion> findOrderBytotal();
//    CurrencyConversion findByStatus();
}

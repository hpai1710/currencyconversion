package com.example.currency_exchange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CurrencyExchangeController {

    @Autowired
    Environment environment;

    @Autowired
    Repository repo;
    @GetMapping("/currency-exchange/{id}")
    public Optional<CurrencyExchange> getallEntries(@PathVariable int id)
    {
        String port = environment.getProperty("local.server.port");
//        currencyExchange.setEnvironment(port);
        Optional<CurrencyExchange> byId = repo.findById((long) id);
        return byId;
    }

    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public CurrencyExchange retriveExchangeValue(@PathVariable String  from, @PathVariable String to)
    {
        CurrencyExchange currencyExchange = repo.findByFromAndTo(from, to);
        if(currencyExchange==null)
        {
            return null;
        }
        String port = environment.getProperty("local.server.port");
        currencyExchange.setEnvironment(port);
        return currencyExchange;
    }
    @PostMapping("/currency-exchange/addcurrency")
    public void addCurrency(@RequestBody CurrencyExchange ce)
    {
        repo.save(ce);
    }
    @PutMapping("/currency-exchange/updatecurrency")
    public ResponseEntity<String> updateCurrency(@RequestBody CurrencyExchange ce)
    {
        repo.save(ce);
        ResponseEntity<String > tResponseEntity = new ResponseEntity<>("row updated successfully", HttpStatus.OK);
        return tResponseEntity;
    }
    @DeleteMapping("/currency-exchange/deletecurrency/{id}")
    public ResponseEntity<String > deleteCurrency(@PathVariable Long id)
    {
        repo.deleteById((long) id);
        return new ResponseEntity<>("Row deleted successfully", HttpStatus.OK);
    }

}

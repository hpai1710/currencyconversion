package com.example.currency_conversion;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@RestController
public class CurrencyConversionController {
    private static final Logger log = LoggerFactory.getLogger(CurrencyConversionController.class);

    @PersistenceContext
    EntityManager em;

    @Autowired
            Repository r1, r2;
    CurrencyConversion res, resfeign;
    @Autowired
    CurrencyExchangeProxy proxy;


    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public ResponseEntity<CurrencyConversion> convertFeign(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity)
    {
        BigDecimal conversionMultiple = null, result= null;

            CurrencyConversion cc = proxy.retriveExchangeValue(from, to);
        if(cc!=null) {
            conversionMultiple = cc.getConversionMultiple();
            BigDecimal quantityBigDecimal = quantity;
            result = conversionMultiple.multiply(quantityBigDecimal);
        }

        int id= (int) (Math.random()/10000);
        String status="SUCCESS";
        if(cc==null)
        {
            status= "FAILED";
            resfeign= new CurrencyConversion(id, from, to, null, quantity, null, null, status);
            r1.save(resfeign);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        resfeign= new CurrencyConversion( id, from, to, conversionMultiple ,quantity,  result, cc.getEnvironment(), status);
        r1.save(resfeign);
        return new ResponseEntity<>(resfeign, HttpStatus.OK);
    }

    @GetMapping("/currency-conversion")
    List<CurrencyConversion> filterStatus(@RequestParam(value = "status", required = false) String statusName, @RequestParam(value = "quantity", required = false) BigDecimal quantity) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CurrencyConversion> query = cb.createQuery(CurrencyConversion.class);
        Root<CurrencyConversion> root = query.from(CurrencyConversion.class);
        Predicate statusfilter = cb.equal(root.get("status"), statusName);
        Predicate quantityFilter = cb.greaterThan(root.get("quantity"), quantity);
        Predicate combine = cb.or(statusfilter, quantityFilter);
        query.select(root).where(combine);
        TypedQuery<CurrencyConversion> tq = em.createQuery(query);
        List<CurrencyConversion> res = tq.getResultList();
        return res;
    }


//        Specification<CurrencyConversion> spec = new Specification<CurrencyConversion>() {
//            @Override
//            public Predicate toPredicate(Root<CurrencyConversion> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
//                Predicate statusfilter = criteriaBuilder.equal(root.get("status"), statusName);
//                Predicate fromfilter = criteriaBuilder.greaterThan(root.get("quantity"), 5);
//                return criteriaBuilder.or(statusfilter, quantity);
//            }
//        };
//        return r1.findAll(spec);
//    }

        @PostMapping("/currency-conversion")
        List<CurrencyConversion> sortTotal(@RequestParam(value = "sort")String  sortField, @RequestParam(value = "order") String order)
        {
            Sort.Direction dir = Sort.Direction.fromString(order);
            Sort sorted = Sort.by(dir, sortField); //Sort.by(SortField).ascending();


            return r1.findAll(sorted);
        }
}

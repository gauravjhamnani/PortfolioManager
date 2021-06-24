package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  private RestTemplate restTemplate;


  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {

    String key = "4a59a723ec41549e4f8a093e470fd900a5ec4452";
    String url = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?startDate=" + startDate.toString()
        + "&endDate=" + endDate.toString() + "&token=" + key;
    return url;
  }

  @Override
  public List<Candle> getStockQuote(
      String symbol, LocalDate from, LocalDate to) throws JsonProcessingException, 
        StockQuoteServiceException {
    
    String url = buildUri(symbol, from, to);

    if (restTemplate == null) {
      throw new RuntimeException();
    
    }

    String resp = restTemplate.getForObject(url, String.class);
    //TiingoCandle[] resp = restTemplate.getForObject(url, TiingoCandle[].class);
    if (resp == null) {
      throw new StockQuoteServiceException("TIINGO SERVICE SERVER DOWN");
    }
    ObjectMapper ob = new ObjectMapper();
    ob.registerModule(new JavaTimeModule());

    TiingoCandle[] anotherResp;
    try {
      anotherResp = ob.readValue(resp, TiingoCandle[].class);
    } catch (JsonProcessingException e) {
      throw new StockQuoteServiceException("INVALID RESPONSE FROM TINNGO");
    }
    if (anotherResp == null) {
      throw new RuntimeException();
    } else {

      List<Candle> quotes = Arrays.asList(anotherResp);
      return quotes;
    }
    
  }




  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.





  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //  1. Update the method signature to match the signature change in the interface.
  //     Start throwing new StockQuoteServiceException when you get some invalid response from
  //     Tiingo, or if Tiingo returns empty results for whatever reason, or you encounter
  //     a runtime exception during Json parsing.
  //  2. Make sure that the exception propagates all the way from
  //     PortfolioManager#calculateAnnualisedReturns so that the external user's of our API
  //     are able to explicitly handle this exception upfront.

  //CHECKSTYLE:OFF


}


package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
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
      String symbol, LocalDate from, LocalDate to) throws JsonProcessingException {
    
    String url = buildUri(symbol, from, to);

    if (restTemplate == null) {
      throw new RuntimeException();
    
    }

    String resp = restTemplate.getForObject(url, String.class);
    //TiingoCandle[] resp = restTemplate.getForObject(url, TiingoCandle[].class);

    ObjectMapper ob = new ObjectMapper();
    ob.registerModule(new JavaTimeModule());
    TiingoCandle[] anotherResp = ob.readValue(resp, TiingoCandle[].class);
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

}

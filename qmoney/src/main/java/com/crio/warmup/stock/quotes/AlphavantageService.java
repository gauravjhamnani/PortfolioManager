
package com.crio.warmup.stock.quotes;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.client.RestTemplate;

public class AlphavantageService implements StockQuotesService {


  private String key = "DB0IP9Q50OSNB26L";
  private RestTemplate restTemplate;

  protected AlphavantageService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  private String buildUri(String symbol) {

    String urlTemplate = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&" 
        + "symbol=$SYMBOL&outputsize=full&apikey=$KEY";
    String url = urlTemplate.replace("$SYMBOL", symbol)
        .replace("$KEY", key);
    return url;
  }

  @Override
  public List<Candle> getStockQuote(
      String symbol, LocalDate from, LocalDate to) throws JsonProcessingException, 
        StockQuoteServiceException {
    
    String url = buildUri(symbol);
    String responseString = restTemplate.getForObject(
        url, String.class);
    
    if (responseString == null) {
      throw new StockQuoteServiceException(
          "No response recieved from 3rd party(Alphavantage)");
    }
    System.out.println(responseString);
    ObjectMapper ob = new ObjectMapper();
    ob.registerModule(new JavaTimeModule());
    AlphavantageDailyResponse response;
    
    try {
      response = ob.readValue(
          responseString, AlphavantageDailyResponse.class);
    } catch (Exception e) {
      throw new StockQuoteServiceException(
          "3rd party api response cannot be processed");
    }
    
    
    if (response == null) {
      throw new RuntimeException();
    }
    Map<LocalDate, AlphavantageCandle> mp = response.getCandles();

    if (mp == null) { 
      throw new StockQuoteServiceException("SERIVCE DOOMED");
    }
    List<Candle> list = new ArrayList<Candle>();
    Iterator<Map.Entry<LocalDate, AlphavantageCandle>> it = mp.entrySet().iterator();
    while (it.hasNext()) {
      
      //Candle tempobj = new AlphavantageCandle();
      Map.Entry<LocalDate, AlphavantageCandle> entry = it.next();
      //AlphavantageCandle tempobj = entry.getValue();
      LocalDate tempdDate = entry.getKey();
      
      if (tempdDate.equals(to)) {

        AlphavantageCandle tempobj = entry.getValue();
        tempobj.setDate(tempdDate);
        list.add(entry.getValue());
        break;
      }
    }
    
    while (it.hasNext()) {
      
      //Candle tempobj = new AlphavantageCandle();
      Map.Entry<LocalDate, AlphavantageCandle> entry = it.next();
      AlphavantageCandle tempobj = entry.getValue();
      LocalDate tempdDate = entry.getKey();
      tempobj.setDate(tempdDate);
      if (tempdDate.compareTo(from) < 0) {
        break;

      }

      list.add(tempobj);
    }
    Collections.reverse(list);
    return list;
  }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement the StockQuoteService interface as per the contracts. Call Alphavantage service
  //  to fetch daily adjusted data for last 20 years.
  //  Refer to documentation here: https://www.alphavantage.co/documentation/
  //  --
  //  The implementation of this functions will be doing following tasks:
  //    1. Build the appropriate url to communicate with third-party.
  //       The url should consider startDate and endDate if it is supported by the provider.
  //    2. Perform third-party communication with the url prepared in step#1
  //    3. Map the response and convert the same to List<Candle>
  //    4. If the provider does not support startDate and endDate, then the implementation
  //       should also filter the dates based on startDate and endDate. Make sure that
  //       result contains the records for for startDate and endDate after filtering.
  //    5. Return a sorted List<Candle> sorted ascending based on Candle#getDate
  // Note:
  // 1. Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
  // 2. Run the tests using command below and make sure it passes:
  //    ./gradlew test --tests AlphavantageServiceTest
  //CHECKSTYLE:OFF
    //CHECKSTYLE:ON
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  1. Write a method to create appropriate url to call Alphavantage service. The method should
  //     be using configurations provided in the {@link @application.properties}.
  //  2. Use this method in #getStockQuote.
  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //   1. Update the method signature to match the signature change in the interface.
  //   2. Start throwing new StockQuoteServiceException when you get some invalid response from
  //      Alphavantage, or you encounter a runtime exception during Json parsing.
  //   3. Make sure that the exception propagates all the way from PortfolioManager, so that the
  //      external user's of our API are able to explicitly handle this exception upfront.
  //CHECKSTYLE:OFF

}


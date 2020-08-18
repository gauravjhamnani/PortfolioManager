
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;

import org.springframework.web.client.RestTemplate;

public enum StockQuoteServiceFactory {

  // Note: (Recommended reading)
  // Pros and cons of implementing Singleton via enum.
  // https://softwareengineering.stackexchange.com/q/179386/253205

  INSTANCE;

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Make sure that you have implemented TiingoService and AlphavantageService
  //  as per the instructions and the tests are passing for them.
  //  Implement the factory function such that it will return TiingoService instance when
  //  provider == 'tiingo' (Case insensitive)
  //  Return new instance of AlphavantageService in all other cases.
  //  RestTemplate is passed as a parameter along, and you will have to pass the same to
  //  the constructor of corresponding class.
  //  Run the tests using command below and make sure it passes
  //  ./gradlew test --tests StockQuoteServiceFactory

  public StockQuotesService getService(
      String provider,  RestTemplate restTemplate) throws StockQuoteServiceException {

    /*if (provider == null) {
      throw new RuntimeException("Provider is not specified");
    }*/
    if (provider != null && provider.equalsIgnoreCase("TIINGO")) {
      return new TiingoService(restTemplate);
    } else {
      return new AlphavantageService(restTemplate);  
    }
  }
}

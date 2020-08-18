package com.crio.warmup.stock.portfolio;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuoteServiceFactory;
import com.crio.warmup.stock.quotes.StockQuotesService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.web.client.RestTemplate;

public class PortfolioManagerFactory {

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // Implement the method to return new instance of PortfolioManager.
  // Remember, pass along the RestTemplate argument that is provided to the new
  // instance.

  @Deprecated
  public static PortfolioManager getPortfolioManager(RestTemplate restTemplate) {

    return new PortfolioManagerImpl(restTemplate);
  }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Implement the method to return new instance of PortfolioManager.
  // Steps:
  // 1. Create appropriate instance of StoockQuoteService using
  // StockQuoteServiceFactory and then
  // use the same instance of StockQuoteService to create the instance of
  // PortfolioManager.
  // 2. Mark the earlier constructor of PortfolioManager as @Deprecated.
  // 3. Make sure all of the tests pass by using the gradle command below:
  // ./gradlew test --tests PortfolioManagerFactory

  public static PortfolioManager getPortfolioManager(String provider, RestTemplate restTemplate)
      throws StockQuoteServiceException {

    StockQuoteServiceFactory obj = StockQuoteServiceFactory.INSTANCE;
    StockQuotesService quoteObj = obj.getService(provider, restTemplate);
    PortfolioManager pfObject = new PortfolioManagerImpl(quoteObj);

    //StockQuotesService quoteObj = obj.getService(provider, restTemplate);
    //PortfolioManager pfObject = new PortfolioManagerImpl(obj, provider, restTemplate);
    
    return pfObject;
  }

}

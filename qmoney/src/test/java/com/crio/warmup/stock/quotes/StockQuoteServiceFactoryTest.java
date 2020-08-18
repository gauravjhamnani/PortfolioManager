
package com.crio.warmup.stock.quotes;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.crio.warmup.stock.exception.StockQuoteServiceException;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

class StockQuoteServiceFactoryTest {

  @Test
  void getServiceTiingo() throws StockQuoteServiceException {
    assertTrue(StockQuoteServiceFactory.INSTANCE.getService(
        "tiingo", new RestTemplate()) instanceof TiingoService);
  }

  @Test
  void getServiceTiingoUpperCase() throws StockQuoteServiceException {
    assertTrue(StockQuoteServiceFactory.INSTANCE.getService(
        "Tiingo", new RestTemplate()) instanceof TiingoService);
  }

  @Test
  void getServiceAlphavantage() throws StockQuoteServiceException {
    assertTrue(StockQuoteServiceFactory.INSTANCE.getService("alphavantage",
        new RestTemplate()) instanceof AlphavantageService);
  }

  @Test
  void getServiceDefault() throws StockQuoteServiceException {
    assertTrue(StockQuoteServiceFactory.INSTANCE.getService("", new RestTemplate())
        instanceof AlphavantageService);
  }
}

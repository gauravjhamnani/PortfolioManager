package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.Pair;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuoteServiceFactory;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/*import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;*/
import org.springframework.web.client.RestTemplate;

/*import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
*/

public class PortfolioManagerImpl implements PortfolioManager {

  private String key = "4a59a723ec41549e4f8a093e470fd900a5ec4452";
  //public String provider;
  StockQuotesService quoteObj = null;
  private RestTemplate restTemplate;

  // Caution: Do not delete or modify the constructor, or else your build will
  // break!
  // This is absolutely necessary for backward compatibility

  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /*protected PortfolioManagerImpl(StockQuoteServiceFactory quoteObj,
      String provider, RestTemplate restTemplate) 
      throws StockQuoteServiceException {
    
    //this.provider = provider;
    this.quoteObj = quoteObj.getService(provider, restTemplate);
    this.restTemplate = restTemplate;
  }
    */
    
  protected PortfolioManagerImpl(StockQuotesService quoteObj) {
    this.quoteObj = quoteObj;
  }

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from
  // main anymore.
  // Copy your code from Module#3
  // PortfolioManagerApplication#calculateAnnualizedReturn
  // into #calculateAnnualizedReturn function here and ensure it follows the
  // method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required
  // further as our
  // clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command
  // below:
  // ./gradlew test --tests PortfolioManagerTest

  // CHECKSTYLE:OFF

  private String getPriceJson(String url) throws IOException, URISyntaxException {

    /*
     * HttpHeaders headers = new HttpHeaders();
     * headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
     * HttpEntity<String> entity = new HttpEntity<String>(headers);
     * 
     * return restTemplate.exchange(url, HttpMethod.GET, entity,
     * String.class).getBody();
     */
    URL link = new URL(url);
    HttpURLConnection con = (HttpURLConnection) link.openConnection();
    con.setRequestMethod("GET");
    String readLine;
    int responseCode = con.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) {
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.defaultCharset()));
      StringBuffer response = new StringBuffer();
      while ((readLine = in.readLine()) != null) {
        response.append(readLine);
      }
      in.close();
      return response.toString();
    } else {
      return "";
    }

  }
  
  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  // CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // Extract the logic to call Tiingo third-party APIs to a separate function.
  // Remember to fill out the buildUri function and use that.

  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) 
      throws JsonProcessingException, StockQuoteServiceException {

    /*HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    HttpEntity<String> entity = new HttpEntity<String>(headers);

    String script = restTemplate.exchange(buildUri(symbol, from, to), HttpMethod.GET, entity, String.class).getBody();*/
    //RestTemplate resttemp = new RestTemplate();
    
    //Candle[] list = restTemplate.getForObject(buildUri(symbol, from, to), Candle[].class);
    if (quoteObj == null) {
      
      String script = buildUri(symbol, from, to);
      //Candle[] list = restTemplate.getForObject(script, Candle[].class); 
      //THIS DID NOT WORK > FIGURE IT OUT LATER. DONE!!YAYY

      TiingoCandle[] list = restTemplate.getForObject(script, TiingoCandle[].class);
      /**ObjectMapper obmapper = getObjectMapper();
      String script = getPriceJson(buildUri(symbol, from, to));
      Candle[] list = obmapper.readValue(script, Candle[].class);*/
      
      if (list == null) {
        return new ArrayList<Candle>();
      }
      else {

        List<Candle> purpose = Arrays.asList(list);
        //Collections.addAll(purpose, list);
        return purpose;
      }
    } else {

      List<Candle> list;
      try {
        list = quoteObj.getStockQuote(symbol, from, to);
      }
      catch (Exception e) {
        throw new StockQuoteServiceException("getStockQuote response is flawed");
      }
      
      if (list == null) {
        return new ArrayList<Candle>();
      } else {
        return list;
      }
    }

  }

  


  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String url = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?startDate=" + startDate.toString()
        + "&endDate=" + endDate.toString() + "&token=" + key;
    return url;
  }

  // return JSON obtained from API call as a string
  
  // Check this menthod later on the substring thing

  private String generateUrl(String stockName, LocalDate ldate) {

    String date = ldate.toString();
    String url = "https://api.tiingo.com/tiingo/daily/" + stockName + "/prices?startDate=" + date + "&endDate=" + date
        + "&token=" + key;
    return url;
  }

  private Pair getPrice(String stockName, String sdate) throws IOException, URISyntaxException {

    String url;
    String jsonIn;
    LocalDate date = LocalDate.parse(sdate);
    // long ctr = -1;
    do {

      url = generateUrl(stockName, date);
      jsonIn = getPriceJson(url);
      date = date.minusDays(1);
      // ctr++;
    } while (jsonIn.length() == 2);
    date = date.plusDays(1);
    //String json = jsonIn.substring(1, jsonIn.length() - 1); // to remove square brackets obtained in the string
    ObjectMapper obmapper = getObjectMapper();
    TiingoCandle[] pf = obmapper.readValue(jsonIn, TiingoCandle[].class);
    return new Pair(date.toString(), pf[0].getClose());
  }

  private Double holdingPeriod(LocalDate end, LocalDate start) {

    Period interval = Period.between(start, end);
    Double yearDiff = (double) interval.getYears() + (double) (interval.getMonths()) / 12
        + (double) interval.getDays() / 365;
    /*
     * if ((end.getMonthValue() - start.getMonthValue()) >= 10) { yearDiff++; }
     */
    return yearDiff;

  }

  private Pair getOpenPrice(String stockName, String sdate) throws IOException, URISyntaxException {

    String url;
    String jsonIn;
    LocalDate date = LocalDate.parse(sdate);
    // long ctr = -1;
    do {

      url = generateUrl(stockName, date);
      jsonIn = getPriceJson(url);
      date = date.minusDays(1);
      // ctr++;
    } while (jsonIn.length() == 2);
    date = date.plusDays(1);
    //String json = jsonIn.substring(1, jsonIn.length() - 1); // to remove square brackets obtained in the string
    ObjectMapper obmapper = getObjectMapper();
    TiingoCandle[] pf = obmapper.readValue(jsonIn, TiingoCandle[].class);
    return new Pair(date.toString(), pf[0].getOpen());
  }

  private boolean isValidDate(String inDate) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    dateFormat.setLenient(false);
    try {
      dateFormat.parse(inDate.trim());
    } catch (ParseException pe) {
      return false;
    }
    return true;
  }

  /*
   * private static File resolveFileFromResources( String filename) throws
   * IOException, URISyntaxException { return Paths.get(
   * Thread.currentThread().getContextClassLoader().getResource(filename).toURI())
   * .toFile(); }
   */

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  private AnnualizedReturn calculateAnnualizedReturnsHelper(LocalDate endDate, PortfolioTrade trade,
      Double buyPrice, Double sellPrice) {

    // double currVal=getPrice(trade.getSymbol(), endDate.toString());
    double diff = sellPrice - buyPrice;
    double totalReturn = diff / buyPrice;
    Double years = holdingPeriod(endDate, trade.getPurchaseDate());
    Double raisedTo = years;
    raisedTo = 1 / raisedTo;
    /*
     * if (years<=0) { throw new NullPointerException(); //Exact exception has to be
     * figured out yet }
     */
    double annualizedReturns = Math.pow((1 + totalReturn), raisedTo) - 1;
    // AnnualizedReturn finalret =
    return new AnnualizedReturn(trade.getSymbol(), annualizedReturns, totalReturn);

  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) throws StockQuoteServiceException {

    // String filename = args[0];
    //String sdate = endDate.toString();
    //LocalDate date = LocalDate.parse(sdate);

    /*
     * File fobject = resolveFileFromResources(filename); ObjectMapper obmapper =
     * getObjectMapper(); List<PortfolioTrade> pf = obmapper.readValue(fobject, new
     * TypeReference<List<PortfolioTrade>>(){});
     */

    List<PortfolioTrade> pf = portfolioTrades;

    List<AnnualizedReturn> list = new ArrayList<AnnualizedReturn>();

    /*for (PortfolioTrade it : pf) {

      if (!isValidDate(sdate)) {
        return Collections.emptyList();
      }
      
      Pair temp = getPrice(it.getSymbol(), sdate);
      LocalDate updatedDate = LocalDate.parse(temp.getName());
      if (updatedDate.isBefore(it.getPurchaseDate())) {
        //throw new NullPointerException();
        return Collections.emptyList();
      }
      list.add(calculateAnnualizedReturnsHelper(updatedDate, it,
          getOpenPrice(it.getSymbol(), it.getPurchaseDate().toString()).getPrice(), temp.getPrice()));
    }*/
    for (PortfolioTrade it:pf) {
      
      LocalDate purchaseDateIt = it.getPurchaseDate(); 

      if (purchaseDateIt.compareTo(endDate) > 0) {
        throw new RuntimeException("STOCKS PURCHASE AFTER QUERIED DATE");
      }
      List<Candle> quotes;
      try {
        quotes = getStockQuote(it.getSymbol(), purchaseDateIt, endDate); 
      } catch (Exception e) {
        throw new StockQuoteServiceException("Problem in resolving quotes");
      }
      
      
      
        //List<Candle> quotes = quoteObj.getStockQuote(it.getSymbol(), purchaseDateIt, endDate);
      if (quotes.size() <= 0) {
        throw new NullPointerException("Quotes not retrieved");
      }
      Double sellPrice = quotes.get(quotes.size() - 1).getClose();
      Double buyPrice = quotes.get(0).getOpen();
      list.add(calculateAnnualizedReturnsHelper(endDate, it, buyPrice, sellPrice));
      
    }



    //Collections.sort(list, AnnualizedReturn.name);
    Collections.sort(list, getComparator());

    return list;
  }

  

  








// Caution: Do not delete or modify the constructor, or else your build will break!
// This is absolutely necessary for backward compatibility
// ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
//  Modify the function #getStockQuote and start delegating to calls to
//  stockQuoteService provided via newly added constructor of the class.
//  You also have a liberty to completely get rid of that function itself, however, make sure
//  that you do not delete the #getStockQuote function.


  public AnnualizedReturn calculateAnnualizedReturnSingle(
      PortfolioTrade portfoliotrade,LocalDate endDate) 
      throws InterruptedException, StockQuoteServiceException {

    LocalDate purchaseDateIt = portfoliotrade.getPurchaseDate(); 

    if (purchaseDateIt.compareTo(endDate) > 0) {
          throw new RuntimeException("STOCKS PURCHASE AFTER QUERIED DATE");
    }
    List<Candle> quotes;
    try {
      quotes = getStockQuote(
          portfoliotrade.getSymbol(), purchaseDateIt, endDate); 
    } catch (Exception e) {
      throw new StockQuoteServiceException("Problem in resolving quotes");
    }
        
        
        
          //List<Candle> quotes = quoteObj.getStockQuote(it.getSymbol(), purchaseDateIt, endDate);
    if (quotes.size() <= 0) {
      throw new NullPointerException("Quotes not retrieved");
    }
    Double sellPrice = quotes.get(quotes.size() - 1).getClose();
    Double buyPrice = quotes.get(0).getOpen();
    AnnualizedReturn val = calculateAnnualizedReturnsHelper(
        endDate, portfoliotrade, buyPrice, sellPrice);

    return val;

    

  }


  public List<AnnualizedReturn> calculateAnnualizedReturnParallel( 
      List<PortfolioTrade> portfolioTrades,
      LocalDate endDate, int numThreads) throws InterruptedException,
      StockQuoteServiceException {

    //int numthreads = portfolioTrades.size();
    ExecutorService exserv = Executors.newFixedThreadPool(numThreads);

    List<Callable<AnnualizedReturn>> callableTasks = 
        new ArrayList<Callable<AnnualizedReturn>>();

    for (PortfolioTrade it:portfolioTrades) {

      Callable<AnnualizedReturn> callableTask = () -> {


        return calculateAnnualizedReturnSingle(it, endDate);
      };

      callableTasks.add(callableTask);
    }
    
    List<Future<AnnualizedReturn>> futures = exserv.invokeAll(callableTasks);

    List<AnnualizedReturn> solution = new  ArrayList<>();
    
    for (Future<AnnualizedReturn> it:futures) {

      AnnualizedReturn result = null;
      try {
        result = it.get();
        System.out.println(it.isDone());
      } catch (InterruptedException | ExecutionException e) {

        e.printStackTrace();
        throw new StockQuoteServiceException("Concurrency failed");
      }
      solution.add(result);

    }
    exserv.shutdown();
    Collections.sort(solution, getComparator());
    return solution;
    
  }

}

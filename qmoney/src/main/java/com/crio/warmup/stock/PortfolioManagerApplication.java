package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;





public class PortfolioManagerApplication {

  private static RestTemplate restTemplate;
  private static String key = "4a59a723ec41549e4f8a093e470fd900a5ec4452";


  public static Double holdingPeriod(
      LocalDate end,LocalDate start) {

    Period interval = Period.between(start, end);
    Double yearDiff = (double) interval.getYears() + (double) (
        interval.getMonths()) / 12 + (double) interval.getDays() / 366;
    /*if ((end.getMonthValue() - start.getMonthValue()) >= 10) {
      yearDiff++;
    }*/
    return yearDiff;


  }

  public static boolean isDateAfterPurchaseDate(
      LocalDate purDate, String enqDate) throws ParseException {

    LocalDate ed = LocalDate.parse(enqDate,DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    //Date ed = (Date) new SimpleDateFormat("yyyy-MM-dd").parse(localDate);
    return ed.isAfter(purDate);
  }


  public static boolean isValidDate(String inDate) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    dateFormat.setLenient(false);
    try {
      dateFormat.parse(inDate.trim());
    } catch (ParseException pe) {
      return false;
    }
    return true;
  }

  // return JSON obtained from API call as a string 
  public static String getPriceJson(String url) throws IOException, URISyntaxException {

    /*HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    HttpEntity<String> entity = new HttpEntity<String>(headers);

    return restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();*/
    URL link = new URL(url);
    HttpURLConnection con = (HttpURLConnection) link.openConnection();
    con.setRequestMethod("GET");
    String readLine;
    int responseCode = con.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) {
      BufferedReader in = new BufferedReader(
          new InputStreamReader(con.getInputStream(),Charset.defaultCharset()));
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
  // Check this menthod later on the substring thing 

  public static String generateUrl(String stockName, LocalDate ldate) {
    
    String date = ldate.toString();
    String url = "https://api.tiingo.com/tiingo/daily/" + stockName + "/prices?startDate=" 
        + date + "&endDate=" + date + "&token=" + key;
    return url;    
  }

  public static Pair getPrice(
      String stockName, String sdate) throws IOException, URISyntaxException {
  
    String url;
    String jsonIn;
    LocalDate date = LocalDate.parse(sdate);
    //long ctr = -1;
    do {

      url = generateUrl(stockName, date);
      jsonIn = getPriceJson(url);
      date = date.minusDays(1);
      //ctr++;
    } while (jsonIn.length() == 2);
    date = date.plusDays(1);
    String json = jsonIn.substring(
        1, jsonIn.length() - 1); //to remove square brackets obtained in the string
    ObjectMapper obmapper = getObjectMapper();
    TiingoCandle pf = obmapper.readValue(
        json,TiingoCandle.class);
    return new Pair(date.toString(), pf.getClose());
  }


  public static Pair getOpenPrice(
      String stockName, String sdate) throws IOException, URISyntaxException {
  
    String url;
    String jsonIn;
    LocalDate date = LocalDate.parse(sdate);
    //long ctr = -1;
    do {

      url = generateUrl(stockName, date);
      jsonIn = getPriceJson(url);
      date = date.minusDays(1);
      //ctr++;
    } while (jsonIn.length() == 2);
    date = date.plusDays(1);
    String json = jsonIn.substring(
        1, jsonIn.length() - 1); //to remove square brackets obtained in the string
    ObjectMapper obmapper = getObjectMapper();
    TiingoCandle pf = obmapper.readValue(
        json,TiingoCandle.class);
    return new Pair(date.toString(), pf.getOpen());
  }

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Read the json file provided in the argument[0]. The file will be available in the classpath.
  //    1. Use #resolveFileFromResources to get actual file from classpath.
  //    2. Extract stock symbols from the json file with ObjectMapper provided by #getObjectMapper.
  //    3. Return the list of all symbols in the same order as provided in json.

  //  Note:
  //  1. There can be few unused imports, you will need to fix them to make the build pass.
  //  2. You can use "./gradlew build" to check if your code builds successfully.

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    String filename = args[0];
    File fobject = resolveFileFromResources(filename);
    ObjectMapper obmapper = getObjectMapper();
    List<PortfolioTrade> pf
        = obmapper.readValue(fobject,new TypeReference<List<PortfolioTrade>>(){});
    List<String> returnlist = new ArrayList<String>();
    for (PortfolioTrade it:pf) {

      returnlist.add(it.getSymbol());
    
    }
    return returnlist;
  }




  //public class PortfolioManagerApplication {




  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.






  /*// TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>*/



  private static void printJsonObject(Object object) throws IOException, URISyntaxException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(
      String filename) throws IOException, URISyntaxException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  /*// TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Follow the instructions provided in the task documentation and fill up the correct values for
  //  the variables provided. First value is provided for your reference.
  //  A. Put a breakpoint on the first line inside mainReadFile() which says
  //    return Collections.emptyList();
  //  B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  //  following the instructions to run the test.
  //  Once you are able to run the test, perform following tasks and record the output as a
  //  String in the function below.
  //  Use this link to see how to evaluate expressions -
  //  https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  //  1. evaluate the value of "args[0]" and set the value
  //     to the variable named valueOfArgument0 (This is implemented for your reference.)
  //  2. In the same window, evaluate the value of expression below and set it
  //  to resultOfResolveFilePathArgs0
  //     expression ==> resolveFileFromResources(args[0])
  //  3. In the same window, evaluate the value of expression below and set it
  //  to toStringOfObjectMapper.
  //  You might see some garbage numbers in the output. Dont worry, its expected.
  //    expression ==> getObjectMapper().toString()
  //  4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  //  second place from top to variable functionNameFromTestFileInStackTrace
  //  5. In the same window, you will see the line number of the function in the stack trace window.
  //  assign the same to lineNumberFromTestFileInStackTrace
  //  Once you are done with above, just run the corresponding test and
  //  make sure its working as expected. use below command to do the same.
  //  ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues*/

  public static List<String> debugOutputs() {
    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0
        = "/home/crio-user/workspace/gaurav-jh-in-ME_QMONEY/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@46268f08";
    String functionNameFromTestFileInStackTrace = ".mainReadFile";
    String lineNumberFromTestFileInStackTrace = "22";


    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
      toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
      lineNumberFromTestFileInStackTrace});
  }


  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  public static List<String> mainReadQuotes(
      String[] args) throws IOException, URISyntaxException, ParseException {

    String filename = args[0];
    String date = args[1];  //storing date as a string
    if (!isValidDate(date)) {
      //throw new IllegalArgumentException("Given date is invalid and cannot be processed.");
      throw new NullPointerException();
    }
    File fobject = resolveFileFromResources(filename);
    ObjectMapper obmapper = getObjectMapper();
    List<PortfolioTrade> pf
        = obmapper.readValue(fobject,new TypeReference<List<PortfolioTrade>>(){});
    List<Pair> stocklist = new ArrayList<Pair>();
    for (PortfolioTrade it:pf) {

      String stockname = it.getSymbol();
      if (!isDateAfterPurchaseDate(it.getPurchaseDate(),date)) {
        throw new NullPointerException();
      }
      Double price = getPrice(stockname,date).getPrice();
      stocklist.add(new Pair(stockname,price));
    }
    Collections.sort(stocklist, new Sortbyprice());
    List<String> returnlist = new ArrayList<String>();
    for (Pair it:stocklist) {
      returnlist.add(it.getName());
    }

    return returnlist;
  }


  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
     
    String filename = args[0];
    String sdate = args[1];
    //LocalDate date = LocalDate.parse(sdate);

    File fobject = resolveFileFromResources(filename);
    ObjectMapper obmapper = getObjectMapper();
    List<PortfolioTrade> pf
        = obmapper.readValue(fobject, new TypeReference<List<PortfolioTrade>>(){});
    
    List<AnnualizedReturn> list = new ArrayList<AnnualizedReturn>();

    for (PortfolioTrade it:pf) {

      if (! isValidDate(sdate)) {
        return Collections.emptyList();
      }
      Pair temp = getPrice(it.getSymbol(),sdate);
      list.add(calculateAnnualizedReturns(LocalDate.parse(temp.getName()), it, getOpenPrice(
          it.getSymbol(), it.getPurchaseDate().toString()).getPrice(), temp.getPrice()));
    }

    Collections.sort(list, AnnualizedReturn.name);
    
    return list;
  }
  

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, 
        Double buyPrice, Double sellPrice) {
    

    try {
      //double currVal=getPrice(trade.getSymbol(), endDate.toString());
      double diff = sellPrice - buyPrice;
      double totalReturn = diff / buyPrice;
      Double years = holdingPeriod(endDate, trade.getPurchaseDate());
      Double raisedTo = years;
      raisedTo = 1 / raisedTo;
      /*if (years<=0) {
        throw new NullPointerException(); //Exact exception has to be figured out yet
      }*/
      double annualizedReturns = Math.pow((1 + totalReturn), raisedTo) - 1;
      AnnualizedReturn finalret = new AnnualizedReturn(
          trade.getSymbol(), annualizedReturns, totalReturn);
      return finalret;
    } catch (RuntimeException e) {
      e.printStackTrace();
      throw e;
    }
  }













  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());



    printJsonObject(mainCalculateSingleReturn(args));

  }
}


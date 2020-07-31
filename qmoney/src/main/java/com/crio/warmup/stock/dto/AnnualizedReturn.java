
package com.crio.warmup.stock.dto;

public class AnnualizedReturn implements Comparable<AnnualizedReturn> {

  private final String symbol;
  private final Double annualizedReturn;
  private final Double totalReturns;

  public AnnualizedReturn(String symbol, Double annualizedReturn, Double totalReturns) {
    this.symbol = symbol;
    this.annualizedReturn = annualizedReturn;
    this.totalReturns = totalReturns;
  }

  public String getSymbol() {
    return symbol;
  }

  public Double getAnnualizedReturn() {
    return annualizedReturn;
  }

  public Double getTotalReturns() {
    return totalReturns;
  }

  public int compareTo(AnnualizedReturn comparevar) {

    Double compareval = comparevar.getAnnualizedReturn();
    
    //return (compareval > this.annualizedReturn) ? 1 : 0;
    return (int) (compareval - this.annualizedReturn);



  }
}

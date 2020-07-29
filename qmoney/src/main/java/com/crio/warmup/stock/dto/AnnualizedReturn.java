
package com.crio.warmup.stock.dto;

public class AnnualizedReturn {

  private final String symbol;
  private final Double annualizedReturnvar;
  private final Double totalReturns;

  public AnnualizedReturn(String symbol, Double annualizedReturnvar, Double totalReturns) {
    this.symbol = symbol;
    this.annualizedReturnvar = annualizedReturnvar;
    this.totalReturns = totalReturns;
  }

  public String getSymbol() {
    return symbol;
  }

  public Double getAnnualizedReturnvar() {
    return annualizedReturnvar;
  }

  public Double getTotalReturns() {
    return totalReturns;
  }
}

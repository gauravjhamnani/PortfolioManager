package com.crio.warmup.stock.dto;

import java.util.Comparator;

public class AnnualizedReturn {

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

  /*@Override
  public int compareTo(AnnualizedReturn comparevar) {

    Double compareval = ((AnnualizedReturn)comparevar).getAnnualizedReturn();
    
    return (compareval > this.annualizedReturn) ? 1 : ((
        compareval == this.annualizedReturn) ? 0 : -1);
    //return (int) (compareval - this.annualizedReturn);



  }*/

  public static final Comparator<AnnualizedReturn> name = new Comparator<AnnualizedReturn>() {

    @Override
    public int compare(AnnualizedReturn o1, AnnualizedReturn o2) {

      return Double.compare(o2.getAnnualizedReturn(), o1.getAnnualizedReturn());
      /*return o1.getAnnualizedReturn() > o2.getAnnualizedReturn() ? 1 : (
          o1.getAnnualizedReturn() < o2.getAnnualizedReturn() ? -1 : 0);*/
    }
  };


}

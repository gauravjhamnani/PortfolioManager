
package com.crio.warmup.stock.dto;

import java.util.Comparator;

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

  @Override
  public int compareTo (AnnualizedReturn comparevar) {

    Double compareval = ((AnnualizedReturn)comparevar).getAnnualizedReturn();
    
    return (compareval > this.annualizedReturn) ? 1 : ((
        compareval == this.annualizedReturn) ? 0 : -1);
    //return (int) (compareval - this.annualizedReturn);



  }

  /*public static Comparator<AnnualizedReturn> FruitNameComparator
                          = new Comparator<AnnualizedReturn() {

	    public int compare(AnnualizedReturn fruit1, AnnualizedReturn fruit2) {

	      String fruitName1 = fruit1.getFruitName().toUpperCase();
	      String fruitName2 = fruit2.getFruitName().toUpperCase();

	      //ascending order
	      return fruitName1.compareTo(fruitName2);

	      //descending order
	      //return fruitName2.compareTo(fruitName1);
	    }

	};*/
}

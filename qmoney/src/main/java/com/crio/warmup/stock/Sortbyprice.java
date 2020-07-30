package com.crio.warmup.stock;

import java.io.Serializable;
import java.util.Comparator;

public class Sortbyprice implements Comparator<Pair>,Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public int compare(Pair a, Pair b) {
    return a.getPrice() < b.getPrice() ? -1 : 1;
  }

    
}
package com.crio.warmup.stock;

import java.io.Serializable;
import java.util.Comparator;

public class Sortbyprice implements Comparator<Pair>,Serializable {
  public int compare(Pair a,Pair b) {
    return a.getPrice() < b.getPrice() ? -1 : 1;
  }

    
}
package com.crio.warmup.stock;

import java.util.Comparator;

public class Sortbyprice implements Comparator<Pair> {
    public int compare(Pair a,Pair b) {
      return a.price<b.price?-1:1;
    }

    
  }
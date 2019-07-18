package com.infotarget.rx.java.book.chapter4;

import io.reactivex.Observable;

import java.math.BigDecimal;

class RxGroceries {

  private final long start = System.currentTimeMillis();

  private void log(Object label) {
    System.out.println(
        System.currentTimeMillis() - start + "\t| " +
            Thread.currentThread().getName() + "\t| " +
            label);
  }

  Observable<BigDecimal> purchase(String productName, long quantity) {
    return Observable.fromCallable(() ->
        doPurchase(productName, quantity));
  }

  BigDecimal doPurchase(String productName, long quantity) {
    log("Purchasing " + quantity + " " + productName);
    //real logic here
    log("Done " + quantity + " " + productName);
    return BigDecimal.ONE;
  }
}

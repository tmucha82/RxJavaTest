package com.infotarget.rx.java.book.chapter3;

import io.reactivex.Observable;

class CassandraFactStore implements FactStore {

  @Override
  public Observable<ReservationEvent> observe() {
    return Observable.just(new ReservationEvent());
  }
}
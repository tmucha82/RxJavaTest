package com.infotarget.rx.java.book.chapter3;

import io.reactivex.Observable;

interface FactStore {

  Observable<ReservationEvent> observe();
}
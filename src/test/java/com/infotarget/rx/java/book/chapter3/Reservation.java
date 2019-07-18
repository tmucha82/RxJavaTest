package com.infotarget.rx.java.book.chapter3;

class Reservation {

  Reservation consume(ReservationEvent event) {
    //mutate myself
    return this;
  }

}
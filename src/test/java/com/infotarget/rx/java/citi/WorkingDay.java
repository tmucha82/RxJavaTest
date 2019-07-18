package com.infotarget.rx.java.citi;

class WorkingDay {

  final private String localDate;

  WorkingDay(final String localDate) {
    this.localDate = localDate;
  }

  public String getLocalDate() {
    return localDate;
  }

  @Override
  public String toString() {
    return "WorkingDay{" +
        "localDate='" + localDate + '\'' +
        '}';
  }
}

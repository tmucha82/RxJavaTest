package com.infotarget.rx.java.citi;

class CashRisk {

  final private double rate;
  final private String workingDay;

  public CashRisk(final double rate, final String workingDay) {
    this.rate = rate;
    this.workingDay = workingDay;
  }

  public double getRate() {
    return rate;
  }

  public String getWorkingDay() {
    return workingDay;
  }

  @Override
  public String toString() {
    return "CashRisk{" +
        "rate=" + rate +
        ", workingDay='" + workingDay + '\'' +
        '}';
  }
}

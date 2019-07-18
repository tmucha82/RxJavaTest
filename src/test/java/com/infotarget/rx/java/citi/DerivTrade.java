package com.infotarget.rx.java.citi;

public class DerivTrade {

  final private String id;
  final private int version;
  final private WorkingDay workingDay;

  DerivTrade(final DerivTrade derivTrade, final WorkingDay workingDay) {
    this(derivTrade.id, derivTrade.version, workingDay);
  }


  private DerivTrade(final String id, final int version, final WorkingDay workingDay) {
    this.id = id;
    this.version = version;
    this.workingDay = workingDay;
  }

  DerivTrade(final String id, final int version) {
    this(id, version, null);
  }

  public String getId() {
    return id;
  }

  public int getVersion() {
    return version;
  }

  public WorkingDay getWorkingDay() {
    return workingDay;
  }

  @Override
  public String toString() {
    return "DerivTrade{" +
        "id='" + id + '\'' +
        ", version=" + version +
        ", workingDay=" + workingDay +
        '}';
  }
}

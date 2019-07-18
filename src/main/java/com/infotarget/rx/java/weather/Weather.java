package com.infotarget.rx.java.weather;

final public class Weather {

  final private String name;

  public Weather(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Weather{" +
        "name='" + name + '\'' +
        '}';
  }
}

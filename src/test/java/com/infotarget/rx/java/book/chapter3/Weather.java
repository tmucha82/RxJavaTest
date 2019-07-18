package com.infotarget.rx.java.book.chapter3;

class Weather {

  private final Temperature temperature;

  public Weather(Temperature temperature, Wind wind) {
    //...
    this.temperature = temperature;
  }

  public boolean isSunny() {
    return true;
  }

  Temperature getTemperature() {
    return temperature;
  }
}
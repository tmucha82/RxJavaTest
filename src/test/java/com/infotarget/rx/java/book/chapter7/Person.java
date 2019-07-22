package com.infotarget.rx.java.book.chapter7;

class Person {
}

class Health {
}

class Score {
  boolean isHigh() {
    return true;
  }
}

class InsuranceContract {
}

class Income {
  final private int _i;

  Income(int i) {
    _i = i;
  }

  static Income no() {
    return new Income(0);
  }

  @Override
  public String toString() {
    return "Income{" +
        "_i=" + _i +
        '}';
  }
}
package com.infotarget.rx.java.book.chapter3;

import io.reactivex.Observable;
import org.junit.Test;

public class OperatorMapTest {

  class StringWrapper {
    String value;

    StringWrapper(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return "StringWrapper{" +
          "value='" + value + '\'' +
          '}';
    }
  }

  @Test
  public void testOperatorMap() {
    Observable
        .range(1, 4)
        .repeat()
        .lift(new OperatorMap<>(integer -> new StringWrapper(integer.toString())))
        .take(3)
        .subscribe(
            System.out::println,
            Throwable::printStackTrace,
            () -> System.out.println("Completed")
        );
  }


}

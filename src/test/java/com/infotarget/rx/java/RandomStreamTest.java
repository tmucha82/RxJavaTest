package com.infotarget.rx.java;

import java.util.stream.StreamSupport;
import org.junit.Test;

public class RandomStreamTest {

  @Test
  public void takeSomeRandomIntegers() {
    StreamSupport
        .stream(new RandomStream().spliterator(), false)
        .limit(5).
        forEach(integer -> System.out.println("Next = " + integer));
  }

}

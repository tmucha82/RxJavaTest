package com.infotarget.rx.java;

import org.junit.Test;

import java.util.stream.StreamSupport;

public class RandomStreamTest {

  @Test
  public void takeSomeRandomIntegers() {
    StreamSupport
        .stream(new RandomStream().spliterator(), true)
        .limit(5)
        .forEach(integer -> System.out.println("Next = " + integer));
  }
}

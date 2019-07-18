package com.infotarget.rx.java;

import java.util.Iterator;
import java.util.Random;

public class RandomStream implements Iterable<Integer> {

  @Override
  public Iterator<Integer> iterator() {
    final Random random = new Random();
    return new Iterator<Integer>() {
      @Override
      public boolean hasNext() {
        return true;
      }

      @Override
      public Integer next() {
        return random.nextInt();
      }
    };
  }
}

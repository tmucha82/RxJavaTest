package com.infotarget.rx.java.book.chapter4;

class Person {

  private final String name;

  Person(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Person{" +
        "name='" + name + '\'' +
        '}';
  }
}

package com.infotarget.rx.java.person;

final public class Person {

  private final int id;

  public Person(int id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Person{" +
        "id=" + id +
        '}';
  }
}

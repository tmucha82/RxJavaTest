package com.infotarget.rx.java.book.chapter4;

import io.reactivex.Observable;

import java.util.Arrays;
import java.util.List;

class PersonDao {

  private static final int PAGE_SIZE = 10;

  private Observable<Person> allPeople(int initialPage) {
    return Observable.defer(() -> Observable.fromIterable(listPeople(initialPage)))
        .concatWith(Observable.defer(() ->
            allPeople(initialPage + 1)));
  }

  void allPeople() {
    Observable<Person> allPages = Observable
        .range(0, Integer.MAX_VALUE)
        .map(this::listPeople)
        .takeWhile(list -> !list.isEmpty())
        .concatMap(Observable::fromIterable);
  }

  List<Person> listPeople() {
    return query("SELECT * FROM PEOPLE");
  }

  private List<Person> listPeople(int initialPage) {
    return query("SELECT * FROM PEOPLE OFFSET ? MAX ?", initialPage * 10, PAGE_SIZE);
  }

  Observable<Person> listPeople2() {
    final List<Person> people = query("SELECT * FROM PEOPLE");
    return Observable.fromIterable(people);
  }

  public Observable<Person> listPeople3() {
    return Observable.defer(() ->
        Observable.fromIterable(query("SELECT * FROM PEOPLE")));
  }

  private List<Person> query(String sql, Object... args) {
    //...
    return Arrays.asList(new Person("Tomasz"), new Person("Slawek"));
  }

}

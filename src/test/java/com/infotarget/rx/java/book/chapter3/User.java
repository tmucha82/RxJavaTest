package com.infotarget.rx.java.book.chapter3;

import io.reactivex.Observable;

class User {
  Observable<Profile> loadProfile() {
    //Make HTTP request...
    return Observable.just(new Profile());
  }
}
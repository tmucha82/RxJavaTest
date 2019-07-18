package com.infotarget.rx.java.book.chapter2;

class Tweet {

  private final String text;

  Tweet(String text) {
    this.text = text;
  }

  String getText() {
    return text;
  }

  @Override
  public String toString() {
    return "Tweet{" +
        "text='" + text + '\'' +
        '}';
  }
}
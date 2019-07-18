package com.infotarget.rx.java.book.chapter3;

import com.infotarget.rx.java.sleeper.Sleeper;
import io.reactivex.Observable;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.time.Duration;
import java.util.Random;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Shakespeare {

  private Observable<String> speak(String quote, long millisPerChar) {
    String[] tokens = quote.replaceAll("[:,]", "").split(" ");
    Observable<String> words = Observable.fromArray(tokens);
    Observable<Long> absoluteDelay = words
        .map(String::length)
        .map(len -> len * millisPerChar)
        .scan((total, current) -> total + current);
    return words
        .zipWith(absoluteDelay.startWith(0L), Pair::of)
        .flatMap(pair -> Observable.just(pair.getLeft())
            .delay(pair.getRight(), MILLISECONDS));
  }

  @Test
  public void sample_27() {
    Observable<String> alice = speak(
        "To be, or not to be: that is the question", 110);
    Observable<String> bob = speak(
        "Though this be madness, yet there is method in't", 90);
    Observable<String> jane = speak(
        "There are more things in Heaven and Earth, " +
            "Horatio, than are dreamt of in your philosophy", 100);

    alice.map(w -> "Alice: " + w).subscribe(System.out::println);
    bob.map(w -> "Bob:   " + w).subscribe(System.out::println);
    jane.map(w -> "Jane:  " + w).subscribe(System.out::println);

    Sleeper.sleep(Duration.ofSeconds(10));
  }

  @Test
  public void sample_28() {
    Observable<String> alice = speak(
        "To be, or not to be: that is the question", 110);
    Observable<String> bob = speak(
        "Though this be madness, yet there is method in't", 90);
    Observable<String> jane = speak(
        "There are more things in Heaven and Earth, " +
            "Horatio, than are dreamt of in your philosophy", 100);

    Observable
        .merge(
            alice.map(w -> "Alice: " + w),
            bob.map(w -> "Bob:   " + w),
            jane.map(w -> "Jane:  " + w)
        )
        .subscribe(System.out::println);

    Sleeper.sleep(Duration.ofSeconds(10));
  }

  @Test
  public void sample_29() {
    Observable<String> alice = speak(
        "To be, or not to be: that is the question", 110);
    Observable<String> bob = speak(
        "Though this be madness, yet there is method in't", 90);
    Observable<String> jane = speak(
        "There are more things in Heaven and Earth, " +
            "Horatio, than are dreamt of in your philosophy", 100);

    Observable
        .concat(
            alice.map(w -> "Alice: " + w),
            bob.map(w -> "Bob:   " + w),
            jane.map(w -> "Jane:  " + w)
        )
        .subscribe(System.out::println);

    Sleeper.sleep(Duration.ofSeconds(10));
  }

  @Test
  public void sample_52() {
    Observable<String> alice = speak(
        "To be, or not to be: that is the question", 110);
    Observable<String> bob = speak(
        "Though this be madness, yet there is method in't", 90);
    Observable<String> jane = speak(
        "There are more things in Heaven and Earth, " +
            "Horatio, than are dreamt of in your philosophy", 100);

    Random rnd = new Random();
    Observable<Observable<String>> quotes = Observable.just(
        alice.map(w -> "Alice: " + w),
        bob.map(w -> "Bob:   " + w),
        jane.map(w -> "Jane:  " + w))
        .flatMap(innerObs -> Observable.just(innerObs)
            .delay(rnd.nextInt(5), SECONDS));

    Observable
        .switchOnNext(quotes)
        .subscribe(System.out::println);
    Sleeper.sleep(Duration.ofSeconds(10));
  }

  @Test
  public void sample_53() {
    Observable<String> alice = speak(
        "To be, or not to be: that is the question", 110);
    Observable<String> bob = speak(
        "Though this be madness, yet there is method in't", 90);
    Observable<String> jane = speak(
        "There are more things in Heaven and Earth, " +
            "Horatio, than are dreamt of in your philosophy", 100);

    Random rnd = new Random();
    Observable<Observable<String>> quotes = Observable.just(
        alice.map(w -> "Alice: " + w),
        bob.map(w -> "Bob:   " + w),
        jane.map(w -> "Jane:  " + w))
        .map(innerObs -> innerObs.delay(rnd.nextInt(5), SECONDS));

    Observable
        .switchOnNext(quotes)
        .subscribe(System.out::println);
    Sleeper.sleep(Duration.ofSeconds(10));
  }
}
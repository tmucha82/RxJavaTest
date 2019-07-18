package com.infotarget.rx.java.book.chapter2;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.*;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Ignore
public class TwitterSample {

  private static final Logger log = LoggerFactory.getLogger(TwitterSample.class);

  @Test
  public void sample_18() throws Exception {
    TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
    twitterStream.addListener(new twitter4j.StatusListener() {
      @Override
      public void onStatus(Status status) {
        log.info("Status: {}", status);
      }

      @Override
      public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        //...
      }

      @Override
      public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        //...
      }

      @Override
      public void onScrubGeo(long userId, long upToStatusId) {
        //...
      }

      @Override
      public void onStallWarning(StallWarning warning) {
        //...
      }

      @Override
      public void onException(Exception ex) {
        log.error("Error callback", ex);
      }

      //other callbacks
    });
    twitterStream.sample();
    TimeUnit.SECONDS.sleep(10);
    twitterStream.shutdown();
  }

  private void consume(
      Consumer<Status> onStatus,
      Consumer<Exception> onException) {
    TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
    twitterStream.addListener(new StatusListener() {
      @Override
      public void onStatus(Status status) {
        onStatus.accept(status);
      }

      @Override
      public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        //...
      }

      @Override
      public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        //...
      }

      @Override
      public void onScrubGeo(long userId, long upToStatusId) {
        //...
      }

      @Override
      public void onStallWarning(StallWarning warning) {
        //...
      }

      @Override
      public void onException(Exception ex) {
        onException.accept(ex);
      }
    });
    twitterStream.sample();
  }

  @Test
  public void sample_99() {
    consume(
        status -> log.info("Status: {}", status),
        ex -> log.error("Error callback", ex)
    );
  }

  private Observable<Status> observe() {
    return Observable.create(subscriber -> {
      TwitterStream twitterStream =
          new TwitterStreamFactory().getInstance();
      twitterStream.addListener(new StatusListener() {
        @Override
        public void onStatus(Status status) {
          if (subscriber.isDisposed()) {
            twitterStream.shutdown();
          } else {
            subscriber.onNext(status);
          }
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
          //...
        }

        @Override
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
          //...
        }

        @Override
        public void onScrubGeo(long userId, long upToStatusId) {
          //...
        }

        @Override
        public void onStallWarning(StallWarning warning) {
          //...
        }

        @Override
        public void onException(Exception ex) {
          if (subscriber.isDisposed()) {
            twitterStream.shutdown();
          } else {
            subscriber.onError(ex);
          }
        }
      });
      subscriber.setCancellable(twitterStream::shutdown);
    });
  }

  @Test
  public void sample_150() {
    observe().subscribe(
        status -> log.info("Status: {}", status),
        ex -> log.error("Error callback", ex)
    );
  }

  @Test
  public void sample_162() {
    Observable<Status> observable = status();

    Disposable sub1 = observable.subscribe();
    System.out.println("Subscribed 1");
    Disposable sub2 = observable.subscribe();
    System.out.println("Subscribed 2");
    sub1.dispose();
    System.out.println("Unsubscribed 1");
    sub2.dispose();
    System.out.println("Unsubscribed 2");
  }

  private Observable<Status> status() {
    return Observable.create(subscriber -> {
      System.out.println("Establishing connection");
      TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
      //...
      subscriber.setCancellable(() -> {
        System.out.println("Disconnecting");
        twitterStream.shutdown();
      });
      twitterStream.sample();
    });
  }

  @Test
  public void sample_186() {
    Observable<Status> observable = status();
    Observable<Status> lazy = observable.publish().refCount();
    //...
    System.out.println("Before subscribers");
    Disposable sub1 = lazy.subscribe();
    System.out.println("Subscribed 1");
    Disposable sub2 = lazy.subscribe();
    System.out.println("Subscribed 2");
    sub1.dispose();
    System.out.println("Unsubscribed 1");
    sub2.dispose();
    System.out.println("Unsubscribed 2");
  }

  @Test
  public void sample_206() {
    final Observable<Status> tweets = status();
    ConnectableObservable<Status> published = tweets.publish();
    published.connect();
  }
}
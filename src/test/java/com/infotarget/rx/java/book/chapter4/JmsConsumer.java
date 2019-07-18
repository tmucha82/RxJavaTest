package com.infotarget.rx.java.book.chapter4;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
class JmsConsumer {

  private final PublishSubject<Message> subject = PublishSubject.create();

  @JmsListener(destination = "orders", concurrency = "1")
  public void newOrder(Message msg) {
    subject.onNext(msg);
  }

  Observable<Message> observe() {
    return subject;
  }

}

package com.infotarget.rx.java.book.chapter4;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

@Ignore
class Messaging {

  private static final Logger log = LoggerFactory.getLogger(Messaging.class);

  void connect() {
    ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    Observable<String> txtMessages = observe(connectionFactory, new ActiveMQTopic("orders"))
        .cast(TextMessage.class)
        .flatMap(m -> {
          try {
            return Observable.just(m.getText());
          } catch (JMSException e) {
            return Observable.error(e);
          }
        });
  }

  private Observable<Message> observe(ConnectionFactory connectionFactory, Topic topic) {
    return Observable.create(subscriber -> {
      try {
        subscribeThrowing(subscriber, connectionFactory, topic);
      } catch (JMSException e) {
        subscriber.onError(e);
      }
    });
  }

  private void subscribeThrowing(ObservableEmitter<? super Message> subscriber, ConnectionFactory connectionFactory,
                                 Topic orders) throws JMSException {
    Connection connection = connectionFactory.createConnection();
    Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
    MessageConsumer consumer = session.createConsumer(orders);
    consumer.setMessageListener(subscriber::onNext);
    subscriber.setDisposable(onUnsubscribe(connection));
    connection.start();
  }

  private Disposable onUnsubscribe(Connection connection) {
    return Disposables.fromAction(() -> {
      try {
        connection.close();
      } catch (Exception e) {
        log.error("Can't close", e);
      }
    });
  }
}

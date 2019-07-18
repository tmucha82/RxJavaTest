package com.infotarget.rx.java.wjug;

import io.reactivex.Flowable;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreamsService;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Ignore;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Ignore
public class RxCamelTest {

  private static final Logger log = LoggerFactory.getLogger(RxCamelTest.class);

  @Test
  public void wjug_184() throws Exception {
    final DefaultCamelContext context = new DefaultCamelContext();
    CamelReactiveStreamsService camel = CamelReactiveStreams.get(context);

    // Get a stream from all the files in a directory
    Publisher<String> files = camel.from("file:C", String.class);
//    Flowable.just(new File("file1.txt"), new File("file2.txt"))
//        .flatMap(file -> camel.toStream("readAndMarshal", String.class))

    Flowable.fromPublisher(files)
        .doOnNext(this::print)
        .subscribe();
  }

  //Retrofit - HTTP
  @Test
  public void wjug_298() throws Exception {
    /*
    final DefaultCamelContext camel = new DefaultCamelContext();
    new ReactiveCamel(camel)
        .toObservable("activemq:queue:wjug")
//				.toObservable("gmail:user@password...")
//				.toObservable("ftp:192.168.0.170/foo/bar")
        .map(Message::getBody)
        .toBlocking()
        .subscribe(this::print);
*/
  }


  void print(Object obj) {
    log.info("Got: {}", obj);
  }
}

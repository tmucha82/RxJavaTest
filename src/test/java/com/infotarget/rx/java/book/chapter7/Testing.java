package com.infotarget.rx.java.book.chapter7;

import com.google.common.io.Files;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.AssertionsForClassTypes.failBecauseExceptionWasNotThrown;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@Ignore
public class Testing {

	@Test
	public void sample_9() throws Exception {
		TestScheduler sched = new TestScheduler();
		Observable<String> fast = Observable
				.interval(10, MILLISECONDS, sched)
				.map(x -> "F" + x)
				.take(3);
		Observable<String> slow = Observable
				.interval(50, MILLISECONDS, sched)
				.map(x -> "S" + x);

		Observable<String> stream = Observable.concat(fast, slow);
		stream.subscribe(System.out::println);
		System.out.println("Subscribed");
	}

	@Test
	public void sample_31() throws Exception {
		TestScheduler sched = new TestScheduler();

		TimeUnit.SECONDS.sleep(1);
		System.out.println("After one second");
		sched.advanceTimeBy(25, MILLISECONDS);

		TimeUnit.SECONDS.sleep(1);
		System.out.println("After one more second");
		sched.advanceTimeBy(75, MILLISECONDS);

		TimeUnit.SECONDS.sleep(1);
		System.out.println("...and one more");
		sched.advanceTimeTo(200, MILLISECONDS);
	}

	@Test
	public void shouldApplyConcatMapInOrder() throws Exception {
		List<String> list = Observable
				.range(1, 3)
				.concatMap(x -> Observable.just(x, -x))
				.map(Object::toString)
				.toList()
				.blockingGet();

		assertThat(list).containsExactly("1", "-1", "2", "-2", "3", "-3");
	}

	@Test
	public void sample_65() throws Exception {
		File file = new File("404.txt");
		Observable<String> fileContents =
				Observable
						.fromCallable(() -> Files.toString(file, UTF_8));

		try {
			fileContents.singleOrError();
			failBecauseExceptionWasNotThrown(FileNotFoundException.class);
		} catch (RuntimeException expected) {
			assertThat(expected)
					.hasCauseInstanceOf(FileNotFoundException.class);
		}
	}

	enum NotificationKind {
		OnNext, OnError  , OnComplete
	}

	@Test
	public void sample_87() throws Exception {

		Observable<Notification<Integer>> notifications = Observable
				.just(3, 0, 2, 0, 1, 0)
				.concatMapDelayError(x -> Observable.fromCallable(() -> 100 / x))
				.materialize();


		List<NotificationKind> kinds = notifications
				.map(notification -> notification.isOnNext()
						? NotificationKind.OnNext : notification.isOnError() ?
						NotificationKind.OnError : NotificationKind.OnComplete)
				.toList()
				.blockingGet();

		assertThat(kinds)
				.containsExactly(
						NotificationKind.OnNext, NotificationKind.OnNext, NotificationKind.OnNext, NotificationKind.OnError);
	}

	@Test
	public void sample_107() throws Exception {
		Observable<Integer> obs = Observable
				.just(3, 0, 2, 0, 1, 0)
				.concatMapDelayError(x -> Observable.fromCallable(() -> 100 / x));

		TestSubscriber<Integer> ts = new TestSubscriber<>();
		obs.toFlowable(BackpressureStrategy.BUFFER)
				.subscribe(ts);

		ts.assertValues(33, 50, 100);
		ts.assertError(ArithmeticException.class);  //Fails (!)
	}

	private MyServiceWithTimeout mockReturning(
			Observable<LocalDate> result,
			TestScheduler testScheduler) {
		MyService mock = mock(MyService.class);
		given(mock.externalCall()).willReturn(result);
		return new MyServiceWithTimeout(mock, testScheduler);
	}

	@Test
	public void timeoutWhenServiceNeverCompletes() throws Exception {
		//given
		TestScheduler testScheduler = new TestScheduler();
		MyService mock = mockReturning(
				Observable.never(), testScheduler);
		TestSubscriber<LocalDate> ts = new TestSubscriber<>();

		//when
		mock.externalCall().toFlowable(BackpressureStrategy.BUFFER).subscribe(ts);

		//then
		testScheduler.advanceTimeBy(950, MILLISECONDS);
		ts.assertNotTerminated();
		testScheduler.advanceTimeBy(100, MILLISECONDS);
		ts.assertComplete();
		ts.assertNoValues();
	}

	@Test
	public void valueIsReturnedJustBeforeTimeout() throws Exception {
		//given
		TestScheduler testScheduler = new TestScheduler();
		Observable<LocalDate> slow = Observable
				.timer(950, MILLISECONDS, testScheduler)
				.map(x -> LocalDate.now());
		MyService myService = mockReturning(slow, testScheduler);
		TestSubscriber<LocalDate> ts = new TestSubscriber<>();

		//when
		myService.externalCall()
				.toFlowable(BackpressureStrategy.BUFFER)
				.subscribe(ts);

		//then
		testScheduler.advanceTimeBy(930, MILLISECONDS);
		ts.assertNotComplete();
		ts.assertNoValues();
		testScheduler.advanceTimeBy(50, MILLISECONDS);
		ts.assertComplete();
		ts.assertValueCount(1);
	}

	private final TestScheduler testScheduler = new TestScheduler();

	@Before
	public void alwaysUseTestScheduler() {
		RxJavaPlugins.setInitIoSchedulerHandler(s -> new TestScheduler());
		RxJavaPlugins.setInitComputationSchedulerHandler(s -> new TestScheduler());
		RxJavaPlugins.setInitNewThreadSchedulerHandler(s -> new TestScheduler());
	}

	Observable<Long> naturals1() {
		return Observable.create(subscriber -> {
			long i = 0;
			while (!subscriber.isDisposed()) {
				subscriber.onNext(i++);
			}
		});
	}

	Observable<Long> naturals2() {
		Flowable<Long> onSubscribe = Flowable.generate(
				() -> 0L,
				(cur, observer) -> {
					observer.onNext(cur);
					return cur + 1;
				}
		);
		return onSubscribe.toObservable();
	}

	@Test
	public void sample_222() throws Exception {
		TestSubscriber<Long> ts = new TestSubscriber<>(0);

		naturals1()
				.take(10)
				.toFlowable(BackpressureStrategy.BUFFER)
				.subscribe(ts);

		ts.assertNoValues();
		ts.requestMore(100);
		ts.assertValueCount(10);
		ts.assertComplete();
	}

}
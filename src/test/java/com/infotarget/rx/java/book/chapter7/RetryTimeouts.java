package com.infotarget.rx.java.book.chapter7;

import io.reactivex.Observable;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

@Ignore
public class RetryTimeouts {

	private static final Logger log = LoggerFactory.getLogger(RetryTimeouts.class);

	Observable<String> risky() {
		return Observable.fromCallable(() -> {
			if (Math.random() < 0.1) {
				Thread.sleep((long) (Math.random() * 2000));
				return "OK";
			} else {
				throw new RuntimeException("Transient");
			}
		});
	}

	@Test
	public void sample_281() {
		risky()
				.timeout(1, SECONDS)
				.doOnError(th -> log.warn("Will retry", th))
				.retry()
				.subscribe(log::info);
	}

	@Test
	public void sample_291() {
		risky().cache().retry();  //BROKEN
	}

	@Test
	public void sample_296() {
		Observable
				.defer(() -> risky())
				.retry();
	}

	@Test
	public void sample_303() {
		risky()
				.timeout(1, SECONDS)
				.retry(10);
	}

	@Test
	public void sample_310() {
		risky()
				.timeout(1, SECONDS)
				.retry((attempt, e) ->
						attempt <= 10 && !(e instanceof TimeoutException));
	}

	@Test
	public void sample_66() {
		risky()
				.timeout(1, SECONDS)
//				.retryWhen(failures -> failures.take(10))
				.retryWhen(failures -> failures.delay(1, SECONDS));
	}

	private static final int ATTEMPTS = 11;

	@Test
	public void sample_74() {
		risky()
				.timeout(1, SECONDS)
				.retryWhen(failures -> failures
						.zipWith(Observable.range(1, ATTEMPTS), (err, attempt) ->
								attempt < ATTEMPTS ?
										Observable.timer(1, SECONDS) :
										Observable.error(err))
						.flatMap(x -> x)
				);
	}

	@Test
	public void sample_89() {
		risky()
				.timeout(1, SECONDS)
				.retryWhen(failures -> failures
						.zipWith(Observable.range(1, ATTEMPTS),
								this::handleRetryAttempt)
						.flatMap(x -> x)
				);
	}

	Observable<Long> handleRetryAttempt(Throwable err, int attempt) {
		switch (attempt) {
			case 1:
				return Observable.just(42L);
			case ATTEMPTS:
				return Observable.error(err);
			default:
				long expDelay = (long) Math.pow(2, attempt - 2);
				return Observable.timer(expDelay, SECONDS);
		}
	}



}
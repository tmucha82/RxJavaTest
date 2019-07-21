package com.infotarget.rx.java.book.chapter7;

import io.reactivex.Observable;

class PrintHouse {

	Observable<TrackingId> deliver(Agreement agreement) {
		return Observable.just(new TrackingId());
	}

}
package com.infotarget.rx.java.book.chapter7;

import io.reactivex.Observable;

import java.time.LocalDate;

interface MyService {
    Observable<LocalDate> externalCall();
}
package com.infotarget.rx.java.book.chapter8.rxbinding;

import android.widget.TextView;
import com.infotarget.rx.java.book.chapter8.rxbinding.internal.Functions;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import javax.annotation.Nonnull;

import static com.infotarget.rx.java.book.chapter8.rxbinding.internal.Preconditions.checkNotNull;


/**
 * Static factory methods for creating {@linkplain Observable observables} and {@linkplain Runnable
 * actions} for {@link TextView}.
 */
public final class RxTextView {
  /**
   * Create an observable of editor actions on {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Warning:</em> The created observable uses {@link TextView.OnEditorActionListener} to
   * observe actions. Only one observable can be used for a view at a time.
   */
  @Nonnull
  public static Observable<Integer> editorActions(@Nonnull TextView view) {
    checkNotNull(view, "view == null");
    return editorActions(view, Functions.FUNC1_ALWAYS_TRUE);
  }

  /**
   * Create an observable of editor actions on {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Warning:</em> The created observable uses {@link TextView.OnEditorActionListener} to
   * observe actions. Only one observable can be used for a view at a time.
   *
   * @param handled Function invoked each occurrence to determine the return value of the
   *                underlying {@link TextView.OnEditorActionListener}.
   */
  @Nonnull
  public static Observable<Integer> editorActions(@Nonnull TextView view,
                                                  @Nonnull Function<? super Integer, Boolean> handled) {
    checkNotNull(view, "view == null");
    checkNotNull(handled, "handled == null");
    return Observable.create(new TextViewEditorActionOnSubscribe(view, handled));
  }

  /**
   * Create an observable of editor action events on {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Warning:</em> The created observable uses {@link TextView.OnEditorActionListener} to
   * observe actions. Only one observable can be used for a view at a time.
   */
  @Nonnull
  public static Observable<TextViewEditorActionEvent> editorActionEvents(@Nonnull TextView view) {
    checkNotNull(view, "view == null");
    return editorActionEvents(view, Functions.FUNC1_ALWAYS_TRUE);
  }

  /**
   * Create an observable of editor action events on {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Warning:</em> The created observable uses {@link TextView.OnEditorActionListener} to
   * observe actions. Only one observable can be used for a view at a time.
   *
   * @param handled Function invoked each occurrence to determine the return value of the
   *                underlying {@link TextView.OnEditorActionListener}.
   */
  @Nonnull
  public static Observable<TextViewEditorActionEvent> editorActionEvents(@Nonnull TextView view,
                                                                         @Nonnull Function<? super TextViewEditorActionEvent, Boolean> handled) {
    checkNotNull(view, "view == null");
    checkNotNull(handled, "handled == null");
    return Observable.create(new TextViewEditorActionEventOnSubscribe(view, handled));
  }

  /**
   * Create an observable of character sequences for text changes on {@code view}.
   * <p>
   * <em>Warning:</em> Values emitted by this observable are <b>mutable</b> and owned by the host
   * {@code TextView} and thus are <b>not safe</b> to cache or delay reading (such as by observing
   * on a different thread). If you want to cache or delay reading the items emitted then you must
   * map values through a function which calls {@link String#valueOf} or
   * {@link CharSequence#toString() .toString()} to create a copy.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Note:</em> A value will be emitted immediately on subscribe.
   */
  @Nonnull
  public static Observable<CharSequence> textChanges(@Nonnull TextView view) {
    checkNotNull(view, "view == null");
    return Observable.create(new TextViewTextOnSubscribe(view));
  }

  /**
   * Create an observable of text change events for {@code view}.
   * <p>
   * <em>Warning:</em> Values emitted by this observable contain a <b>mutable</b>
   * {@link CharSequence} owned by the host {@code TextView} and thus are <b>not safe</b> to cache
   * or delay reading (such as by observing on a different thread). If you want to cache or delay
   * reading the items emitted then you must map values through a function which calls
   * {@link String#valueOf} or {@link CharSequence#toString() .toString()} to create a copy.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Note:</em> A value will be emitted immediately on subscribe.
   */
  @Nonnull
  public static Observable<TextViewTextChangeEvent> textChangeEvents(@Nonnull TextView view) {
    checkNotNull(view, "view == null");
    return Observable.create(new TextViewTextChangeEventOnSubscribe(view));
  }

  /**
   * Create an observable of before text change events for {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Note:</em> A value will be emitted immediately on subscribe.
   */
  @Nonnull
  public static Observable<TextViewBeforeTextChangeEvent> beforeTextChangeEvents(
      @Nonnull TextView view) {
    checkNotNull(view, "view == null");
    return Observable.create(new TextViewBeforeTextChangeEventOnSubscribe(view));
  }

  /**
   * Create an observable of after text change events for {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Note:</em> A value will be emitted immediately on subscribe.
   */
  @Nonnull
  public static Observable<TextViewAfterTextChangeEvent> afterTextChangeEvents(
      @Nonnull TextView view) {
    checkNotNull(view, "view == null");
    return Observable.create(new TextViewAfterTextChangeEventOnSubscribe(view));
  }

  /**
   * An action which sets the text property of {@code view} with character sequences.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  @Nonnull
  public static Consumer<? super CharSequence> text(@Nonnull final TextView view) {
    checkNotNull(view, "view == null");
    return new Consumer<CharSequence>() {
      @Override
      public void accept(CharSequence charSequence) throws Exception {
        view.setText(charSequence);
      }
    };
  }

  /**
   * An action which sets the text property of {@code view} string resource IDs.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  @Nonnull
  public static Consumer<? super Integer> textRes(@Nonnull final TextView view) {
    checkNotNull(view, "view == null");
    return new Consumer<Integer>() {
      @Override
      public void accept(Integer textRes) {
        view.setText(textRes);
      }
    };
  }

  /**
   * An action which sets the error property of {@code view} with character sequences.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  @Nonnull
  public static Consumer<? super CharSequence> error(@Nonnull final TextView view) {
    checkNotNull(view, "view == null");
    return new Consumer<CharSequence>() {
      @Override
      public void accept(CharSequence text) {
        view.setError(text);
      }
    };
  }

  /**
   * An action which sets the error property of {@code view} string resource IDs.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  @Nonnull
  public static Consumer<? super Integer> errorRes(@Nonnull final TextView view) {
    checkNotNull(view, "view == null");
    return new Consumer<Integer>() {
      @Override
      public void accept(Integer textRes) {
        view.setError(view.getContext().getResources().getText(textRes));
      }
    };
  }

  /**
   * An action which sets the hint property of {@code view} with character sequences.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  @Nonnull
  public static Consumer<? super CharSequence> hint(@Nonnull final TextView view) {
    checkNotNull(view, "view == null");
    return new Consumer<CharSequence>() {
      @Override
      public void accept(CharSequence hint) {
        view.setHint(hint);
      }
    };
  }

  /**
   * An action which sets the hint property of {@code view} string resource IDs.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  @Nonnull
  public static Consumer<? super Integer> hintRes(@Nonnull final TextView view) {
    checkNotNull(view, "view == null");
    return new Consumer<Integer>() {
      @Override
      public void accept(Integer hintRes) {
        view.setHint(hintRes);
      }
    };
  }

  /**
   * An action which sets the color property of {@code view} with color integer.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  @Nonnull
  public static Consumer<? super Integer> color(@Nonnull final TextView view) {
    checkNotNull(view, "view == null");
    return new Consumer<Integer>() {
      @Override
      public void accept(Integer color) {
        view.setTextColor(color);
      }
    };
  }

  private RxTextView() {
    throw new AssertionError("No instances.");
  }
}
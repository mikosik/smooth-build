package org.smoothbuild.common.collect;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.collect.List.list;

import java.util.NoSuchElementException;
import java.util.Objects;
import org.smoothbuild.common.collect.Maybe.None;
import org.smoothbuild.common.collect.Maybe.Some;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.common.function.Function2;

public abstract sealed class Maybe<E> permits Some, None {

  public static <E> Some<E> some(E element) {
    return new Some<>(element);
  }

  public static <E> None<E> none() {
    return new None<>();
  }

  public static <E> Maybe<E> maybe(E element) {
    return element == null ? none() : some(element);
  }

  public abstract E get();

  public abstract E getOr(E b);

  public abstract <T extends Throwable> E getOrGet(Function0<E, T> supplier) throws T;

  public abstract <T1 extends Throwable, T2 extends Throwable> E getOrThrow(
      Function0<T1, T2> exceptionSupplier) throws T1, T2;

  public abstract <T extends Throwable> Maybe<E> ifPresent(Consumer1<E, T> consumer) throws T;

  public abstract <R, T extends Throwable> Maybe<R> map(Function1<E, R, T> mapper) throws T;

  public abstract <R, T extends Throwable> Maybe<R> flatMap(Function1<E, Maybe<R>, T> mapper)
      throws T;

  public <D, R, T extends Throwable> Maybe<R> mapWith(
      Maybe<D> second, Function2<E, D, R, T> biFunction) throws T {
    return flatMap(f -> second.map(d -> biFunction.apply(f, d)));
  }

  public <D, R, T extends Throwable> Maybe<R> flatMapWith(
      Maybe<D> maybe, Function2<E, D, Maybe<R>, T> biFunction) throws T {
    return flatMap(e -> maybe.flatMap(d -> biFunction.apply(e, d)));
  }

  public abstract boolean isSome();

  public abstract boolean isNone();

  public abstract List<E> toList();

  public static final class Some<E> extends Maybe<E> {
    private final E element;

    private Some(E element) {
      this.element = element;
    }

    @Override
    public E get() {
      return element;
    }

    @Override
    public E getOr(E b) {
      return element;
    }

    @Override
    public <T extends Throwable> E getOrGet(Function0<E, T> supplier) {
      return element;
    }

    @Override
    public <T1 extends Throwable, T2 extends Throwable> E getOrThrow(
        Function0<T1, T2> exceptionSupplier) {
      return element;
    }

    @Override
    public <T extends Throwable> Maybe<E> ifPresent(Consumer1<E, T> consumer) throws T {
      consumer.accept(element);
      return this;
    }

    @Override
    public <R, T extends Throwable> Some<R> map(Function1<E, R, T> mapper) throws T {
      return some(mapper.apply(element));
    }

    @Override
    public <R, T extends Throwable> Maybe<R> flatMap(Function1<E, Maybe<R>, T> mapper) throws T {
      return requireNonNull(mapper.apply(element));
    }

    @Override
    public boolean isSome() {
      return true;
    }

    @Override
    public boolean isNone() {
      return false;
    }

    @Override
    public List<E> toList() {
      return list(element);
    }

    @Override
    public boolean equals(Object object) {
      if (this == object) {
        return true;
      }
      return object instanceof Some<?> that && Objects.equals(this.element, that.element);
    }

    @Override
    public int hashCode() {
      return Objects.hash(element);
    }

    @Override
    public String toString() {
      return "Some(" + element + ")";
    }
  }

  public static final class None<E> extends Maybe<E> {
    private None() {}

    @Override
    public E get() {
      throw new NoSuchElementException();
    }

    @Override
    public E getOr(E value) {
      return value;
    }

    @Override
    public <T extends Throwable> E getOrGet(Function0<E, T> supplier) throws T {
      return supplier.apply();
    }

    @Override
    public <T1 extends Throwable, T2 extends Throwable> E getOrThrow(
        Function0<T1, T2> exceptionSupplier) throws T2, T1 {
      throw exceptionSupplier.apply();
    }

    @Override
    public <T extends Throwable> Maybe<E> ifPresent(Consumer1<E, T> consumer) {
      return this;
    }

    @Override
    public <R, T extends Throwable> None<R> map(Function1<E, R, T> mapper) throws T {
      return cast();
    }

    @Override
    public <R, T extends Throwable> None<R> flatMap(Function1<E, Maybe<R>, T> mapper) {
      return cast();
    }

    private <R> None<R> cast() {
      @SuppressWarnings("unchecked")
      var result = (None<R>) this;
      return result;
    }

    @Override
    public boolean isSome() {
      return false;
    }

    @Override
    public boolean isNone() {
      return true;
    }

    @Override
    public List<E> toList() {
      return list();
    }

    @Override
    public boolean equals(Object object) {
      return object instanceof None<?>;
    }

    @Override
    public int hashCode() {
      // Arbitrary prime number
      return 7993;
    }

    @Override
    public String toString() {
      return "None";
    }
  }
}

package org.smoothbuild.common.collect;

import static java.util.Objects.requireNonNull;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import org.smoothbuild.common.collect.Result.Err;
import org.smoothbuild.common.collect.Result.Ok;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.common.function.Function1;

public sealed interface Result<R> permits Err, Ok {
  public static <R> Ok<R> ok(R value) {
    return new Ok<>(value);
  }

  public static <R> Err<R> err(String message) {
    return new Err<>(message);
  }

  public R ok();

  public String err();

  public boolean isOk();

  public boolean isErr();

  public <T extends Throwable> Result<R> ifOk(Consumer1<R, T> consumer) throws T;

  public <T extends Throwable> Result<R> ifErr(Consumer1<? super String, T> consumer) throws T;

  public R okOr(R or);

  public <T extends Throwable> R okOrGet(Function0<R, T> supplier) throws T;

  public <T extends Throwable> String errOrGet(Function0<String, T> supplier) throws T;

  public <E extends Throwable> R okOrThrow(Function<? super String, E> supplier) throws E;

  public <S, T extends Throwable> Result<S> mapOk(Function1<? super R, S, T> mapper) throws T;

  public <T extends Throwable> Result<R> mapErr(Function1<? super String, String, T> mapper)
      throws T;

  public <S, T extends Throwable> Result<S> flatMapOk(Function1<? super R, Result<S>, T> mapper)
      throws T;

  public <T extends Throwable> Result<R> flatMapErr(Function1<? super String, Result<R>, T> mapper)
      throws T;

  public static final class Ok<R> implements Result<R> {
    private final R value;

    public Ok(R value) {
      this.value = value;
    }

    @Override
    public String err() {
      throw new NoSuchElementException();
    }

    @Override
    public boolean isOk() {
      return true;
    }

    @Override
    public boolean isErr() {
      return false;
    }

    @Override
    public <T extends Throwable> Result<R> ifOk(Consumer1<R, T> consumer) throws T {
      consumer.accept(value);
      return this;
    }

    @Override
    public <T extends Throwable> Result<R> ifErr(Consumer1<? super String, T> consumer) {
      return this;
    }

    @Override
    public R okOr(R or) {
      return value;
    }

    @Override
    public <T extends Throwable> R okOrGet(Function0<R, T> supplier) {
      return value;
    }

    @Override
    public <T extends Throwable> String errOrGet(Function0<String, T> supplier) throws T {
      return supplier.apply();
    }

    @Override
    public <E extends Throwable> R okOrThrow(Function<? super String, E> supplier) {
      return value;
    }

    @Override
    public <S, T extends Throwable> Ok<S> mapOk(Function1<? super R, S, T> mapper) throws T {
      return new Ok<>(mapper.apply(value));
    }

    @Override
    public <T extends Throwable> Ok<R> mapErr(Function1<? super String, String, T> mapper) {
      return this;
    }

    @Override
    public <S, T extends Throwable> Result<S> flatMapOk(Function1<? super R, Result<S>, T> mapper)
        throws T {
      return requireNonNull(mapper.apply(value));
    }

    @Override
    public <T extends Throwable> Result<R> flatMapErr(
        Function1<? super String, Result<R>, T> mapper) {
      return this;
    }

    @Override
    public R ok() {
      return value;
    }

    @Override
    public boolean equals(Object object) {
      return object != null
          && object.getClass() == this.getClass()
          && Objects.equals(this.value, ((Ok<?>) object).value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }

    @Override
    public String toString() {
      return "Ok[" + "value=" + value + ']';
    }
  }

  public static final class Err<R> implements Result<R> {
    private final String message;

    public Err(String message) {
      this.message = message;
    }

    @Override
    public R ok() {
      throw new NoSuchElementException();
    }

    @Override
    public boolean isOk() {
      return false;
    }

    @Override
    public boolean isErr() {
      return true;
    }

    @Override
    public <T extends Throwable> Result<R> ifOk(Consumer1<R, T> consumer) {
      return this;
    }

    @Override
    public <T extends Throwable> Err<R> ifErr(Consumer1<? super String, T> consumer) throws T {
      consumer.accept(message);
      return this;
    }

    @Override
    public R okOr(R or) {
      return or;
    }

    @Override
    public <T extends Throwable> R okOrGet(Function0<R, T> supplier) throws T {
      return supplier.apply();
    }

    @Override
    public <T extends Throwable> String errOrGet(Function0<String, T> supplier) {
      return message;
    }

    @Override
    public <E extends Throwable> R okOrThrow(Function<? super String, E> supplier) throws E {
      throw supplier.apply(message);
    }

    @Override
    public <S, T extends Throwable> Result<S> mapOk(Function1<? super R, S, T> mapper) {
      return Result.err(message);
    }

    @Override
    public <T extends Throwable> Err<R> mapErr(Function1<? super String, String, T> mapper)
        throws T {
      return new Err<>(mapper.apply(message));
    }

    @Override
    public <S, T extends Throwable> Result<S> flatMapOk(Function1<? super R, Result<S>, T> mapper) {
      return Result.err(message);
    }

    @Override
    public <T extends Throwable> Result<R> flatMapErr(
        Function1<? super String, Result<R>, T> mapper) throws T {
      return requireNonNull(mapper.apply(message));
    }

    @Override
    public String err() {
      return message;
    }

    @Override
    public boolean equals(Object obj) {
      return obj != null
          && obj.getClass() == this.getClass()
          && Objects.equals(this.message, ((Err) obj).message);
    }

    @Override
    public int hashCode() {
      return Objects.hash(message);
    }

    @Override
    public String toString() {
      return "Err[" + "message=" + message + ']';
    }
  }
}

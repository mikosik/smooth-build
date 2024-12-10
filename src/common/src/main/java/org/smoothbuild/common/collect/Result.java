package org.smoothbuild.common.collect;

public sealed interface Result<R> extends Either<String, R> {
  public static <R> Ok<R> ok(R result) {
    return new Ok<>(result);
  }

  public static final class Ok<R> extends Either.Right<String, R> implements Result<R> {
    public Ok(R value) {
      super(value);
    }
  }

  public static <R> Error<R> error(String message) {
    return new Error<>(message);
  }

  public static final class Error<R> extends Either.Left<String, R> implements Result<R> {
    public Error(String message) {
      super(message);
    }
  }
}

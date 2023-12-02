package org.smoothbuild.common.collect;

import static java.util.Objects.requireNonNull;

import java.util.NoSuchElementException;
import org.smoothbuild.common.collect.Either.Left;
import org.smoothbuild.common.collect.Either.Right;
import org.smoothbuild.common.function.ThrowingFunction;

public sealed interface Either<L, R> permits Left, Right {
  public static <L, R> Right<L, R> right(R right) {
    return new Right<>(right);
  }

  public static <L, R> Left<L, R> left(L left) {
    return new Left<>(left);
  }

  public boolean isRight();

  public boolean isLeft();

  public R right();

  public L left();

  public <S, T extends Throwable> Either<L, S> mapRight(ThrowingFunction<R, S, T> mapper) throws T;

  public <S, T extends Throwable> Either<S, R> mapLeft(ThrowingFunction<L, S, T> mapper) throws T;

  public <S, T extends Throwable> Either<L, S> flatMapRight(
      ThrowingFunction<R, Either<L, S>, T> mapper) throws T;

  public <S, T extends Throwable> Either<S, R> flatMapLeft(
      ThrowingFunction<L, Either<S, R>, T> mapper) throws T;

  public record Right<L, R>(R right) implements Either<L, R> {

    @Override
    public boolean isRight() {
      return true;
    }

    @Override
    public boolean isLeft() {
      return false;
    }

    @Override
    public L left() {
      throw new NoSuchElementException();
    }

    @Override
    public <S, T extends Throwable> Right<L, S> mapRight(ThrowingFunction<R, S, T> mapper)
        throws T {
      return new Right<>(mapper.apply(right));
    }

    @Override
    public <S, T extends Throwable> Right<S, R> mapLeft(ThrowingFunction<L, S, T> mapper) {
      @SuppressWarnings("unchecked")
      var cast = (Right<S, R>) this;
      return cast;
    }

    @Override
    public <S, T extends Throwable> Either<L, S> flatMapRight(
        ThrowingFunction<R, Either<L, S>, T> mapper) throws T {
      return requireNonNull(mapper.apply(right));
    }

    @Override
    public <S, T extends Throwable> Either<S, R> flatMapLeft(
        ThrowingFunction<L, Either<S, R>, T> mapper) {
      @SuppressWarnings("unchecked")
      var cast = (Right<S, R>) this;
      return cast;
    }
  }

  public record Left<L, R>(L left) implements Either<L, R> {
    @Override
    public boolean isRight() {
      return false;
    }

    @Override
    public boolean isLeft() {
      return true;
    }

    @Override
    public R right() {
      throw new NoSuchElementException();
    }

    @Override
    public <S, T extends Throwable> Either<L, S> mapRight(ThrowingFunction<R, S, T> mapper) {
      @SuppressWarnings("unchecked")
      var left = (Either<L, S>) this;
      return left;
    }

    @Override
    public <S, T extends Throwable> Left<S, R> mapLeft(ThrowingFunction<L, S, T> mapper) throws T {
      return new Left<>(mapper.apply(left));
    }

    @Override
    public <S, T extends Throwable> Either<L, S> flatMapRight(
        ThrowingFunction<R, Either<L, S>, T> mapper) {
      @SuppressWarnings("unchecked")
      var left = (Either<L, S>) this;
      return left;
    }

    @Override
    public <S, T extends Throwable> Either<S, R> flatMapLeft(
        ThrowingFunction<L, Either<S, R>, T> mapper) throws T {
      return requireNonNull(mapper.apply(left));
    }
  }
}

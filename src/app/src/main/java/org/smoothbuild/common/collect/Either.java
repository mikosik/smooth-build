package org.smoothbuild.common.collect;

import static java.util.Objects.requireNonNull;

import java.util.NoSuchElementException;
import org.smoothbuild.common.collect.Either.Left;
import org.smoothbuild.common.collect.Either.Right;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.common.function.Function1;

public sealed interface Either<L, R> permits Left, Right {
  public static <L, R> Right<L, R> right(R right) {
    return new Right<>(right);
  }

  public static <L, R> Left<L, R> left(L left) {
    return new Left<>(left);
  }

  public R right();

  public L left();

  public boolean isRight();

  public boolean isLeft();

  public <T extends Throwable> Either<L, R> ifRight(Consumer1<R, T> consumer) throws T;

  public <T extends Throwable> Either<L, R> ifLeft(Consumer1<L, T> consumer) throws T;

  public <T extends Throwable> R rightOrGet(Function0<R, T> supplier) throws T;

  public <T extends Throwable> L leftOrGet(Function0<L, T> supplier) throws T;

  public <S, T extends Throwable> Either<L, S> mapRight(Function1<R, S, T> mapper) throws T;

  public <S, T extends Throwable> Either<S, R> mapLeft(Function1<L, S, T> mapper) throws T;

  public <S, T extends Throwable> Either<L, S> flatMapRight(Function1<R, Either<L, S>, T> mapper)
      throws T;

  public <S, T extends Throwable> Either<S, R> flatMapLeft(Function1<L, Either<S, R>, T> mapper)
      throws T;

  public record Right<L, R>(R right) implements Either<L, R> {
    @Override
    public L left() {
      throw new NoSuchElementException();
    }

    @Override
    public boolean isRight() {
      return true;
    }

    @Override
    public boolean isLeft() {
      return false;
    }

    @Override
    public <T extends Throwable> Either<L, R> ifRight(Consumer1<R, T> consumer) throws T {
      consumer.accept(right);
      return this;
    }

    @Override
    public <T extends Throwable> Either<L, R> ifLeft(Consumer1<L, T> consumer) {
      return this;
    }

    @Override
    public <T extends Throwable> R rightOrGet(Function0<R, T> supplier) {
      return right;
    }

    @Override
    public <T extends Throwable> L leftOrGet(Function0<L, T> supplier) throws T {
      return supplier.get();
    }

    @Override
    public <S, T extends Throwable> Right<L, S> mapRight(Function1<R, S, T> mapper) throws T {
      return new Right<>(mapper.apply(right));
    }

    @Override
    public <S, T extends Throwable> Right<S, R> mapLeft(Function1<L, S, T> mapper) {
      @SuppressWarnings("unchecked")
      var cast = (Right<S, R>) this;
      return cast;
    }

    @Override
    public <S, T extends Throwable> Either<L, S> flatMapRight(Function1<R, Either<L, S>, T> mapper)
        throws T {
      return requireNonNull(mapper.apply(right));
    }

    @Override
    public <S, T extends Throwable> Either<S, R> flatMapLeft(Function1<L, Either<S, R>, T> mapper) {
      @SuppressWarnings("unchecked")
      var cast = (Right<S, R>) this;
      return cast;
    }
  }

  public record Left<L, R>(L left) implements Either<L, R> {
    @Override
    public R right() {
      throw new NoSuchElementException();
    }

    @Override
    public boolean isRight() {
      return false;
    }

    @Override
    public boolean isLeft() {
      return true;
    }

    @Override
    public <T extends Throwable> Either<L, R> ifRight(Consumer1<R, T> consumer) {
      return null;
    }

    @Override
    public <T extends Throwable> Left<L, R> ifLeft(Consumer1<L, T> consumer) throws T {
      consumer.accept(left);
      return this;
    }

    @Override
    public <T extends Throwable> R rightOrGet(Function0<R, T> supplier) throws T {
      return supplier.get();
    }

    @Override
    public <T extends Throwable> L leftOrGet(Function0<L, T> supplier) {
      return left;
    }

    @Override
    public <S, T extends Throwable> Either<L, S> mapRight(Function1<R, S, T> mapper) {
      @SuppressWarnings("unchecked")
      var left = (Either<L, S>) this;
      return left;
    }

    @Override
    public <S, T extends Throwable> Left<S, R> mapLeft(Function1<L, S, T> mapper) throws T {
      return new Left<>(mapper.apply(left));
    }

    @Override
    public <S, T extends Throwable> Either<L, S> flatMapRight(
        Function1<R, Either<L, S>, T> mapper) {
      @SuppressWarnings("unchecked")
      var left = (Either<L, S>) this;
      return left;
    }

    @Override
    public <S, T extends Throwable> Either<S, R> flatMapLeft(Function1<L, Either<S, R>, T> mapper)
        throws T {
      return requireNonNull(mapper.apply(left));
    }
  }
}

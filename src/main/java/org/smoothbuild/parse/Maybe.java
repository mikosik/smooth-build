package org.smoothbuild.parse;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.util.TriFunction;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class Maybe<E> {
  private final E result;
  private final ImmutableList<Error> errors;

  public static class Error {
    public final CodeLocation codeLocation;
    public final String message;

    public Error(CodeLocation codeLocation, String message) {
      this.codeLocation = requireNonNull(codeLocation);
      this.message = requireNonNull(message);
    }
  }

  public static <E> Maybe<E> element(E result) {
    return new Maybe<>(requireNonNull(result), ImmutableList.of());
  }

  public static <E> Maybe<E> error(CodeLocation location, String message) {
    return error(new Error(location, message));
  }

  public static <E> Maybe<E> error(Error error) {
    return new Maybe<E>(null, ImmutableList.of(error));
  }

  public static <E> Maybe<E> errors(List<Error> errors) {
    return new Maybe<E>(null, ImmutableList.copyOf(errors));
  }

  private Maybe(E result, ImmutableList<Error> errors) {
    this.result = result;
    this.errors = errors;
  }

  public Maybe<E> addError(CodeLocation location, String message) {
    return addError(new Error(location, message));
  }

  public Maybe<E> addError(Error error) {
    return new Maybe<>(null, concatErrors(errors, ImmutableList.of(error)));
  }

  public Maybe<E> addErrors(List<Error> errors) {
    return new Maybe<>(null, concatErrors(this.errors, errors));
  }

  public boolean hasResult() {
    return result != null;
  }

  public E result() {
    checkState(hasResult());
    return result;
  }

  public ImmutableList<Error> errors() {
    return errors;
  }

  public static <S, R> Maybe<R> invoke(Maybe<S> s, Function<S, R> function) {
    return new Maybe<>(invokeRaw(s, function), s.errors);
  }

  private static <S, R> R invokeRaw(Maybe<S> s, Function<S, R> function) {
    if (s.hasResult()) {
      return function.apply(s.result);
    } else {
      return null;
    }
  }

  public static <S, T, R> Maybe<R> invoke(Maybe<S> s, Maybe<T> t, BiFunction<S, T, R> function) {
    return new Maybe<>(
        invokeRaw(s, t, function),
        concatErrors(s.errors, t.errors));
  }

  private static <S, T, R> R invokeRaw(Maybe<S> s, Maybe<T> t, BiFunction<S, T, R> function) {
    if (s.hasResult() && t.hasResult()) {
      return function.apply(s.result, t.result);
    } else {
      return null;
    }
  }

  public static <S, T, U, R> Maybe<R> invoke(Maybe<S> s,
      Maybe<T> t, Maybe<U> u, TriFunction<S, T, U, R> function) {
    return new Maybe<>(
        invokeRaw(s, t, u, function),
        concatErrors(s.errors, t.errors, u.errors));
  }

  private static <S, T, U, R> R invokeRaw(Maybe<S> s,
      Maybe<T> t, Maybe<U> u, TriFunction<S, T, U, R> function) {
    if (s.hasResult() && t.hasResult() && u.hasResult()) {
      return function.apply(s.result, t.result, u.result);
    } else {
      return null;
    }
  }

  private static ImmutableList<Error> concatErrors(List<Error>... lists) {
    Builder<Error> builder = ImmutableList.builder();
    for (List<Error> list : lists) {
      builder.addAll(list);
    }
    return builder.build();
  }
}

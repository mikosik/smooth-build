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
  private final ImmutableList<Object> errors;

  public static <E> Maybe<E> result(E result) {
    return new Maybe<>(requireNonNull(result), ImmutableList.of());
  }

  public static <E> Maybe<E> error(CodeLocation location, String message) {
    return error(new ParseError(location, message));
  }

  public static <E> Maybe<E> error(Object error) {
    return new Maybe<E>(null, ImmutableList.of(error));
  }

  public static <E> Maybe<E> errors(List<? extends Object> errors) {
    return new Maybe<E>(null, ImmutableList.copyOf(errors));
  }

  private Maybe(E result, ImmutableList<Object> errors) {
    this.result = result;
    this.errors = errors;
  }

  public Maybe<E> addError(CodeLocation location, String message) {
    return addError(new ParseError(location, message));
  }

  public Maybe<E> addError(Object error) {
    return new Maybe<>(null, concatErrors(errors, ImmutableList.of(error)));
  }

  public Maybe<E> addErrors(List<? extends Object> errors) {
    return new Maybe<>(null, concatErrors(this.errors, errors));
  }

  public boolean hasResult() {
    return result != null;
  }

  public E result() {
    checkState(hasResult());
    return result;
  }

  public ImmutableList<Object> errors() {
    return errors;
  }

  public static <S, R> Maybe<R> invoke(Maybe<S> s, Function<S, Maybe<R>> function) {
    if (s.hasResult()) {
      return function.apply(s.result);
    } else {
      return errors(s.errors);
    }
  }

  public static <S, R> Maybe<R> invokeWrap(Maybe<S> s, Function<S, R> function) {
    if (s.hasResult()) {
      return result(function.apply(s.result));
    } else {
      return errors(s.errors);
    }
  }

  public static <S, T, R> Maybe<R> invoke(Maybe<S> s, Maybe<T> t,
      BiFunction<S, T, Maybe<R>> function) {
    if (s.hasResult() && t.hasResult()) {
      return function.apply(s.result, t.result);
    } else {
      return errors(concatErrors(s.errors, t.errors));
    }
  }

  public static <S, T, R> Maybe<R> invokeWrap(Maybe<S> s, Maybe<T> t,
      BiFunction<S, T, R> function) {
    if (s.hasResult() && t.hasResult()) {
      return result(function.apply(s.result, t.result));
    } else {
      return errors(concatErrors(s.errors, t.errors));
    }
  }

  public static <S, T, U, R> Maybe<R> invoke(Maybe<S> s,
      Maybe<T> t, Maybe<U> u, TriFunction<S, T, U, Maybe<R>> function) {
    if (s.hasResult() && t.hasResult() && u.hasResult()) {
      return function.apply(s.result, t.result, u.result);
    } else {
      return errors(concatErrors(s.errors, t.errors, u.errors));
    }
  }

  public static <S, T, U, R> Maybe<R> invokeWrap(Maybe<S> s,
      Maybe<T> t, Maybe<U> u, TriFunction<S, T, U, R> function) {
    if (s.hasResult() && t.hasResult() && u.hasResult()) {
      return result(function.apply(s.result, t.result, u.result));
    } else {
      return errors(concatErrors(s.errors, t.errors, u.errors));
    }
  }

  private static ImmutableList<Object> concatErrors(List<? extends Object>... lists) {
    Builder<Object> builder = ImmutableList.builder();
    for (List<? extends Object> list : lists) {
      builder.addAll(list);
    }
    return builder.build();
  }
}

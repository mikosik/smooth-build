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

public class Parsed<E> {
  private final E result;
  private final ImmutableList<ParseError> errors;

  public static <E> Parsed<E> parsed(E result) {
    return new Parsed<>(requireNonNull(result), ImmutableList.of());
  }

  public static <E> Parsed<E> error(CodeLocation location, String message) {
    return error(new ParseError(location, message));
  }

  public static <E> Parsed<E> error(ParseError error) {
    return new Parsed<E>(null, ImmutableList.of(error));
  }

  private Parsed(E result, ImmutableList<ParseError> errors) {
    this.result = result;
    this.errors = errors;
  }

  public Parsed<E> addError(CodeLocation location, String message) {
    return addError(new ParseError(location, message));
  }

  public Parsed<E> addError(ParseError error) {
    return new Parsed<>(null, concatErrors(errors, ImmutableList.of(error)));
  }

  public Parsed<E> addErrors(List<ParseError> errors) {
    return new Parsed<>(null, concatErrors(this.errors, errors));
  }

  public boolean hasResult() {
    return result != null;
  }

  public E result() {
    checkState(hasResult());
    return result;
  }

  public ImmutableList<ParseError> errors() {
    return errors;
  }

  public static <S, R> Parsed<R> invoke(Parsed<S> s, Function<S, R> function) {
    return new Parsed<>(invokeRaw(s, function), s.errors);
  }

  private static <S, R> R invokeRaw(Parsed<S> s, Function<S, R> function) {
    if (s.hasResult()) {
      return function.apply(s.result);
    } else {
      return null;
    }
  }

  public static <S, T, R> Parsed<R> invoke(Parsed<S> s, Parsed<T> t, BiFunction<S, T, R> function) {
    return new Parsed<>(
        invokeRaw(s, t, function),
        concatErrors(s.errors, t.errors));
  }

  private static <S, T, R> R invokeRaw(Parsed<S> s, Parsed<T> t, BiFunction<S, T, R> function) {
    if (s.hasResult() && t.hasResult()) {
      return function.apply(s.result, t.result);
    } else {
      return null;
    }
  }

  public static <S, T, U, R> Parsed<R> invoke(Parsed<S> s,
      Parsed<T> t, Parsed<U> u, TriFunction<S, T, U, R> function) {
    return new Parsed<>(
        invokeRaw(s, t, u, function),
        concatErrors(s.errors, t.errors, u.errors));
  }

  private static <S, T, U, R> R invokeRaw(Parsed<S> s,
      Parsed<T> t, Parsed<U> u, TriFunction<S, T, U, R> function) {
    if (s.hasResult() && t.hasResult() && u.hasResult()) {
      return function.apply(s.result, t.result, u.result);
    } else {
      return null;
    }
  }

  private static ImmutableList<ParseError> concatErrors(List<ParseError>... lists) {
    Builder<ParseError> builder = ImmutableList.builder();
    for (List<ParseError> list : lists) {
      builder.addAll(list);
    }
    return builder.build();
  }
}

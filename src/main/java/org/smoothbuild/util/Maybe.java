package org.smoothbuild.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.join;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.Lists.map;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class Maybe<E> {
  private final E value;
  private final ImmutableList<Object> errors;

  public static <E> Maybe<E> value(E value) {
    return new Maybe<>(requireNonNull(value), ImmutableList.of());
  }

  public static <E> Maybe<E> error(Object error) {
    return new Maybe<E>(null, ImmutableList.of(error));
  }

  public static <E> Maybe<E> errors(List<? extends Object> errors) {
    checkArgument(!errors.isEmpty(), "'errors' argument shouldn't be empty");
    return new Maybe<E>(null, ImmutableList.copyOf(errors));
  }

  public static <E> Maybe<E> maybe(E value, List<? extends Object> errors) {
    checkArgument(!(value == null && errors.isEmpty()));
    return new Maybe<>(errors.isEmpty() ? value : null, ImmutableList.copyOf(errors));
  }

  private Maybe(E value, ImmutableList<Object> errors) {
    this.value = value;
    this.errors = errors;
  }

  public Maybe<E> addError(Object error) {
    return new Maybe<>(null, concatErrors(errors, ImmutableList.of(error)));
  }

  public Maybe<E> addErrors(List<? extends Object> errors) {
    return new Maybe<>(errors.isEmpty() ? value : null, concatErrors(this.errors, errors));
  }

  public Maybe<E> addErrors(Function<E, List<? extends Object>> errorsSupplier) {
    if (hasValue()) {
      return addErrors(errorsSupplier.apply(value));
    } else {
      return this;
    }
  }

  public boolean hasValue() {
    return value != null;
  }

  public E value() {
    checkState(hasValue());
    return value;
  }

  public ImmutableList<Object> errors() {
    return errors;
  }

  public boolean equals(Object object) {
    return object instanceof Maybe && equals((Maybe<?>) object);
  }

  public boolean equals(Maybe<?> that) {
    return Objects.equal(value, value) && errors.equals(that.errors);
  }

  public int hashCode() {
    return Objects.hashCode(value, errors);
  }

  public String toString() {
    if (hasValue()) {
      return "Maybe.value(" + value + ")";
    } else {
      return "Maybe.error(" + join(", ", map(errors, Object::toString)) + ")";
    }
  }

  public static <E> Maybe<List<E>> pullUp(List<Maybe<E>> list) {
    Maybe<List<E>> result = value(new ArrayList<>());
    for (Maybe<E> element : list) {
      result = invokeWrap(result, element, Lists::concat);
    }
    return result;
  }

  public static <S, R> Maybe<R> invoke(Maybe<S> s, Function<S, Maybe<R>> function) {
    if (s.hasValue()) {
      return function.apply(s.value);
    } else {
      return errors(s.errors);
    }
  }

  public static <S, R> Maybe<R> invokeWrap(Maybe<S> s, Function<S, R> function) {
    if (s.hasValue()) {
      return value(function.apply(s.value));
    } else {
      return errors(s.errors);
    }
  }

  public static <S, T, R> Maybe<R> invoke(Maybe<S> s, Maybe<T> t,
      BiFunction<S, T, Maybe<R>> function) {
    if (s.hasValue() && t.hasValue()) {
      return function.apply(s.value, t.value);
    } else {
      return errors(concatErrors(s.errors, t.errors));
    }
  }

  public static <S, T, R> Maybe<R> invokeWrap(Maybe<S> s, Maybe<T> t,
      BiFunction<S, T, R> function) {
    if (s.hasValue() && t.hasValue()) {
      return value(function.apply(s.value, t.value));
    } else {
      return errors(concatErrors(s.errors, t.errors));
    }
  }

  public static <S, T, U, R> Maybe<R> invoke(Maybe<S> s,
      Maybe<T> t, Maybe<U> u, TriFunction<S, T, U, Maybe<R>> function) {
    if (s.hasValue() && t.hasValue() && u.hasValue()) {
      return function.apply(s.value, t.value, u.value);
    } else {
      return errors(concatErrors(s.errors, t.errors, u.errors));
    }
  }

  public static <S, T, U, R> Maybe<R> invokeWrap(Maybe<S> s,
      Maybe<T> t, Maybe<U> u, TriFunction<S, T, U, R> function) {
    if (s.hasValue() && t.hasValue() && u.hasValue()) {
      return value(function.apply(s.value, t.value, u.value));
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

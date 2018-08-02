package org.smoothbuild.util;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.join;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public abstract class Maybe<E> {

  public static <E> Maybe<E> value(E value) {
    return new ValueMaybe<E>(value);
  }

  public static <E> Maybe<E> error(Object error) {
    return new ErrorMaybe<>(ImmutableList.of(error));
  }

  public static <E> Maybe<E> errors(List<? extends Object> errors) {
    return new ErrorMaybe<>(errors);
  }

  public static <E> Maybe<E> maybe(E value, List<? extends Object> errors) {
    if (errors.isEmpty()) {
      return new ValueMaybe<>(value);
    } else {
      return new ErrorMaybe<>(errors);
    }
  }

  public static <E> Maybe<List<E>> pullUp(List<Maybe<E>> list) {
    Maybe<List<E>> result = value(new ArrayList<>());
    for (Maybe<E> element : list) {
      result = result.mapValue(element, Lists::concat);
    }
    return result;
  }

  public abstract Maybe<E> addError(Object error);

  public abstract Maybe<E> addErrors(List<? extends Object> errors);

  public abstract boolean hasValue();

  public abstract E value();

  public abstract ImmutableList<Object> errors();

  public abstract Maybe<E> invokeConsumer(Consumer<E> consumer);

  public abstract Maybe<E> invoke(Supplier<List<? extends Object>> consumer);

  public abstract Maybe<E> invoke(Function<E, List<? extends Object>> consumer);

  public abstract <F> Maybe<E> invoke(Maybe<F> param,
      BiFunction<E, F, List<? extends Object>> consumer);

  public abstract <R> Maybe<R> map(Function<E, Maybe<R>> function);

  public abstract <R> Maybe<R> mapValue(Function<E, R> function);

  public abstract <F, R> Maybe<R> map(Maybe<F> param, BiFunction<E, F, Maybe<R>> function);

  public abstract <F, R> Maybe<R> mapValue(Maybe<F> param, BiFunction<E, F, R> function);

  public static class ValueMaybe<E> extends Maybe<E> {
    private final E value;

    public ValueMaybe(E value) {
      this.value = value;
    }

    @Override
    public Maybe<E> addError(Object error) {
      return error(error);
    }

    @Override
    public Maybe<E> addErrors(List<? extends Object> errors) {
      if (errors.isEmpty()) {
        return this;
      } else {
        return Maybe.errors(errors);
      }
    }

    @Override
    public boolean hasValue() {
      return true;
    }

    @Override
    public E value() {
      return value;
    }

    @Override
    public ImmutableList<Object> errors() {
      return ImmutableList.of();
    }

    @Override
    public Maybe<E> invokeConsumer(Consumer<E> consumer) {
      consumer.accept(value);
      return this;
    }

    @Override
    public Maybe<E> invoke(Supplier<List<? extends Object>> supplier) {
      return appendErrorsIfExist(supplier.get());
    }

    @Override
    public Maybe<E> invoke(Function<E, List<? extends Object>> consumer) {
      return appendErrorsIfExist(consumer.apply(value));
    }

    @Override
    public <F> Maybe<E> invoke(Maybe<F> param,
        BiFunction<E, F, List<? extends Object>> consumer) {
      if (param.hasValue()) {
        return appendErrorsIfExist(consumer.apply(value, param.value()));
      } else {
        return (Maybe<E>) param;
      }
    }

    private Maybe<E> appendErrorsIfExist(List<? extends Object> newErrors) {
      if (newErrors.isEmpty()) {
        return this;
      } else {
        return Maybe.errors(newErrors);
      }
    }

    @Override
    public <R> Maybe<R> map(Function<E, Maybe<R>> function) {
      return function.apply(value);
    }

    @Override
    public <R> Maybe<R> mapValue(Function<E, R> function) {
      return value(function.apply(value));
    }

    @Override
    public <F, R> Maybe<R> map(Maybe<F> param, BiFunction<E, F, Maybe<R>> function) {
      if (param.hasValue()) {
        return function.apply(value, param.value());
      } else {
        return (Maybe<R>) param;
      }
    }

    @Override
    public <F, R> Maybe<R> mapValue(Maybe<F> param, BiFunction<E, F, R> function) {
      if (param.hasValue()) {
        return value(function.apply(value, param.value()));
      } else {
        return (Maybe<R>) param;
      }
    }

    @Override
    public boolean equals(Object object) {
      return object instanceof ValueMaybe && equals((ValueMaybe<E>) object);
    }

    public boolean equals(ValueMaybe<E> that) {
      return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(value);
    }

    @Override
    public String toString() {
      return "Maybe.value(" + value + ")";
    }
  }

  public static class ErrorMaybe<E> extends Maybe<E> {
    private final ImmutableList<Object> errors;

    public ErrorMaybe(List<? extends Object> errors) {
      checkArgument(!errors.isEmpty(), "'errors' argument shouldn't be empty");
      this.errors = ImmutableList.copyOf(errors);
    }

    @Override
    public Maybe<E> addError(Object error) {
      return new ErrorMaybe<>(concatErrors(errors, ImmutableList.of(error)));
    }

    @Override
    public Maybe<E> addErrors(List<? extends Object> errors) {
      return new ErrorMaybe<>(concatErrors(this.errors, errors));
    }

    @Override
    public boolean hasValue() {
      return false;
    }

    @Override
    public E value() {
      throw new UnsupportedOperationException();
    }

    @Override
    public ImmutableList<Object> errors() {
      return errors;
    }

    @Override
    public Maybe<E> invokeConsumer(Consumer<E> consumer) {
      return this;
    }

    @Override
    public Maybe<E> invoke(Supplier<List<? extends Object>> supplier) {
      return this;
    }

    @Override
    public Maybe<E> invoke(Function<E, List<? extends Object>> consumer) {
      return this;
    }

    @Override
    public <F> Maybe<E> invoke(Maybe<F> param,
        BiFunction<E, F, List<? extends Object>> consumer) {
      return errors(concatErrors(errors, param.errors()));
    }

    @Override
    public <R> Maybe<R> map(Function<E, Maybe<R>> function) {
      return (Maybe<R>) this;
    }

    @Override
    public <R> Maybe<R> mapValue(Function<E, R> function) {
      return (Maybe<R>) this;
    }

    @Override
    public <F, R> Maybe<R> map(Maybe<F> param, BiFunction<E, F, Maybe<R>> function) {
      return errors(concatErrors(errors, param.errors()));
    }

    @Override
    public <F, R> Maybe<R> mapValue(Maybe<F> param, BiFunction<E, F, R> function) {
      return errors(concatErrors(this.errors, param.errors()));
    }

    @Override
    public boolean equals(Object object) {
      return object instanceof ErrorMaybe && equals((ErrorMaybe<?>) object);
    }

    public boolean equals(ErrorMaybe<?> that) {
      return errors.equals(that.errors);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(errors);
    }

    @Override
    public String toString() {
      return "Maybe.error(" + join(", ", Lists.map(errors, Object::toString)) + ")";
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

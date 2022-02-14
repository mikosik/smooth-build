package org.smoothbuild.util.collect;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.function.Function;

public record Result<T>(T value, String error) {
  public static <T> Result<T> of(T value) {
    return new Result<>(value, null);
  }

  public static <T> Result<T> error(String error) {
    return new Result<>(null, error);
  }

  public Result {
    checkArgument((value == null) != (error == null), "Exactly one of arguments must be null.");
  }

  @Override
  public T value() {
    checkState(isPresent());
    return value;
  }

  @Override
  public String error() {
    checkState(!isPresent());
    return error;
  }

  public boolean isPresent() {
    return value != null;
  }

  public <U> Result<U> map(Function<? super T, U> mapper) {
    if (isPresent()) {
      return Result.of(mapper.apply(value));
    } else {
      return castError();
    }
  }

  public <U> Result<U> flatMap(Function<? super T, ? extends Result<? extends U>> mapper) {
    if (isPresent()) {
      @SuppressWarnings("unchecked")
      Result<U> result = (Result<U>) mapper.apply(value);
      return result;
    } else {
      return castError();
    }
  }

  public Result<T> mapError(Function<String, String> mapper) {
    if (isPresent()) {
      return this;
    } else {
      return Result.error(mapper.apply(error));
    }
  }

  public Result<T> validate(Function<? super T, String> validator) {
    if (isPresent()) {
      String validationError = validator.apply(value);
      if (validationError != null) {
        return Result.error(validationError);
      }
    }
    return this;
  }

  public T orElse(Function<String, ? extends T> otherSupplier) {
    if (isPresent()) {
      return value;
    } else {
      return otherSupplier.apply(error);
    }
  }

  private <U> Result<U> castError() {
    @SuppressWarnings("unchecked")
    Result<U> error = (Result<U>) this;
    return error;
  }
}

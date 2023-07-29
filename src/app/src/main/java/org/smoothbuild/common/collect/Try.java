package org.smoothbuild.common.collect;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.function.Function;

public record Try<T>(T result, String error) {
  public static <T> Try<T> result(T result) {
    return new Try<>(result, null);
  }

  public static <T> Try<T> error(String error) {
    return new Try<>(null, error);
  }

  public Try {
    checkArgument((result == null) != (error == null), "Exactly one of arguments must be null.");
  }

  @Override
  public T result() {
    checkState(isPresent());
    return result;
  }

  @Override
  public String error() {
    checkState(!isPresent());
    return error;
  }

  public boolean isPresent() {
    return result != null;
  }

  public <U> Try<U> map(Function<? super T, U> mapper) {
    if (isPresent()) {
      return Try.result(mapper.apply(result));
    } else {
      return castError();
    }
  }

  public <U> Try<U> flatMap(Function<? super T, ? extends Try<? extends U>> mapper) {
    if (isPresent()) {
      @SuppressWarnings("unchecked")
      Try<U> aTry = (Try<U>) mapper.apply(result);
      return aTry;
    } else {
      return castError();
    }
  }

  public Try<T> mapError(Function<String, String> mapper) {
    if (isPresent()) {
      return this;
    } else {
      return Try.error(mapper.apply(error));
    }
  }

  public Try<T> validate(Function<? super T, String> validator) {
    if (isPresent()) {
      String validationError = validator.apply(result);
      if (validationError != null) {
        return Try.error(validationError);
      }
    }
    return this;
  }

  public T orElse(Function<String, ? extends T> otherSupplier) {
    if (isPresent()) {
      return result;
    } else {
      return otherSupplier.apply(error);
    }
  }

  private <U> Try<U> castError() {
    @SuppressWarnings("unchecked")
    Try<U> error = (Try<U>) this;
    return error;
  }

  @Override
  public String toString() {
    if (isPresent()) {
      return "Try.result(" + result + ")";
    } else {
      return "Try.error(\"" + error + "\")";
    }
  }
}

package org.smoothbuild.common.concurrent;

import java.util.Objects;

public abstract class AbstractPromise<T> implements Promise<T> {
  @Override
  public String toString() {
    return "Promise(" + toMaybe().toString() + ")";
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof AbstractPromise<?> that
        && Objects.equals(this.toMaybe(), that.toMaybe());
  }

  @Override
  public int hashCode() {
    return Objects.hash(toMaybe());
  }
}

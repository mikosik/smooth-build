package org.smoothbuild.common.function;

public interface ThrowingSupplier<R, E extends Throwable> {
  public R get() throws E;
}

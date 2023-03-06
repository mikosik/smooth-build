package org.smoothbuild.util.function;

public interface ThrowingSupplier<R, E extends Throwable> {
  public R get() throws E;
}

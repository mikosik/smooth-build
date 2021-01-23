package org.smoothbuild.util;

public interface ThrowingSupplier<R, E extends Throwable> {
  public R get() throws E;
}

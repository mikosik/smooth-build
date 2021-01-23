package org.smoothbuild.util;

public class MemoizingSupplier<R, E extends Throwable> implements ThrowingSupplier<R, E> {
  private final ThrowingSupplier<R, E> supplier;
  private R result;

  public MemoizingSupplier(ThrowingSupplier<R, E> supplier) {
    this.supplier = supplier;
    this.result = null;
  }

  @Override
  public R get() throws E {
    if (result == null) {
      result = supplier.get();
    }
    return result;
  }
}

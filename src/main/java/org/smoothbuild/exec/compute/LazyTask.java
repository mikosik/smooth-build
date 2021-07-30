package org.smoothbuild.exec.compute;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * This class is thread-safe.
 */
public record LazyTask(Type type, Location location, Supplier<Task> supplier)
    implements Dependency {
  public LazyTask(Type type, Location location, Supplier<Task> supplier) {
    this.type = type;
    this.location = location;
    this.supplier = Suppliers.memoize(supplier);
  }

  @Override
  public Task task() {
    return supplier.get();
  }
}

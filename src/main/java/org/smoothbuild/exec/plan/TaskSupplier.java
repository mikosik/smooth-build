package org.smoothbuild.exec.plan;


import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * This class is thread-safe.
 */
public record TaskSupplier(Type type, Location location, Supplier<Task> supplier) {
  public TaskSupplier(Type type, Location location, Supplier<Task> supplier) {
    this.type = type;
    this.location = location;
    this.supplier = Suppliers.memoize(supplier);
  }

  public Task getTask() {
    return supplier.get();
  }
}

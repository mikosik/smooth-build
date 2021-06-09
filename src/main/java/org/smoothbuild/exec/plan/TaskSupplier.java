package org.smoothbuild.exec.plan;


import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public record TaskSupplier(Type type, Supplier<Task> supplier) {
  public TaskSupplier(Type type, Supplier<Task> supplier) {
    this.type = type;
    this.supplier = Suppliers.memoize(supplier);
  }

  public Task getTask() {
    return supplier.get();
  }
}

package org.smoothbuild.exec.compute;

import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.concurrent.Feeder;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public record LazyTask(Type type, Location location, Supplier<Task> supplier) implements Task {
  public LazyTask(Type type, Location location, Supplier<Task> supplier) {
    this.type = type;
    this.location = location;
    this.supplier = Suppliers.memoize(supplier);
  }

  @Override
  public String name() {
    return task().name();
  }

  @Override
  public ImmutableList<Task> dependencies() {
    return task().dependencies();
  }

  @Override
  public String description() {
    return task().description();
  }

  @Override
  public TaskKind kind() {
    return task().kind();
  }

  @Override
  public Feeder<Obj> compute(Worker worker) {
    return task().compute(worker);
  }

  private Task task() {
    return supplier.get();
  }
}

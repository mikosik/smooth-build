package org.smoothbuild.exec.job;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.concurrent.Promise;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public record LazyJob(TypeS type, Location location, Supplier<Job> supplier) implements Job {
  public LazyJob(TypeS type, Location location, Supplier<Job> supplier) {
    this.type = type;
    this.location = location;
    this.supplier = Suppliers.memoize(supplier);
  }

  @Override
  public String name() {
    return job().name();
  }

  @Override
  public ImmutableList<Job> dependencies() {
    return job().dependencies();
  }

  @Override
  public Promise<Val> schedule(Worker worker) {
    return job().schedule(worker);
  }

  private Job job() {
    return supplier.get();
  }
}

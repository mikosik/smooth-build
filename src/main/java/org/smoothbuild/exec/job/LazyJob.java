package org.smoothbuild.exec.job;

import org.smoothbuild.db.bytecode.obj.val.ValB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.util.concurrent.Promise;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public record LazyJob(TypeB type, Loc loc, Supplier<Job> supplier) implements Job {
  public LazyJob(TypeB type, Loc loc, Supplier<Job> supplier) {
    this.type = type;
    this.loc = loc;
    this.supplier = Suppliers.memoize(supplier);
  }

  @Override
  public String name() {
    return job().name();
  }

  @Override
  public ImmutableList<Job> deps() {
    return job().deps();
  }

  @Override
  public Promise<ValB> schedule(Worker worker) {
    return job().schedule(worker);
  }

  private Job job() {
    return supplier.get();
  }
}

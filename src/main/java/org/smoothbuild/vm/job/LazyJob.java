package org.smoothbuild.vm.job;

import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

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
  public Loc loc() {
    return job().loc();
  }

  @Override
  public Promise<ValB> schedule(Worker worker) {
    return job().schedule(worker);
  }

  private Job job() {
    return supplier.get();
  }
}

package org.smoothbuild.vm.job.job;

import org.smoothbuild.db.bytecode.obj.val.ValB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

import com.google.common.collect.ImmutableList;

public interface Job extends Nal {
  public TypeB type();

  public ImmutableList<Job> deps();

  public Promise<ValB> schedule(Worker worker);
}

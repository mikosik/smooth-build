package org.smoothbuild.exec.job;

import org.smoothbuild.db.bytecode.obj.val.ValB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.util.concurrent.Promise;

import com.google.common.collect.ImmutableList;

public interface Job extends Nal {
  public TypeB type();

  public ImmutableList<Job> deps();

  public Promise<ValB> schedule(Worker worker);
}

package org.smoothbuild.exec.job;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.util.concurrent.Promise;

import com.google.common.collect.ImmutableList;

public interface Job extends Nal {
  public Type type();

  public ImmutableList<Job> dependencies();

  public Promise<Val> schedule(Worker worker);
}

package org.smoothbuild.exec.compute;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.lang.base.define.Named;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.concurrent.Feeder;

import com.google.common.collect.ImmutableList;

public interface Job extends Named {
  public Type type();

  public ImmutableList<Job> dependencies();

  public Feeder<Val> schedule(Worker worker);
}

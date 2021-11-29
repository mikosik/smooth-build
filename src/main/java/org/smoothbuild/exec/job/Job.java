package org.smoothbuild.exec.job;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.util.concurrent.Promise;

import com.google.common.collect.ImmutableList;

public interface Job extends Nal {
  public TypeH type();

  public ImmutableList<Job> dependencies();

  public Promise<ValueH> schedule(Worker worker);
}

package org.smoothbuild.exec.compute;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.io.fs.space.Space;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.concurrent.Feeder;

import com.google.common.collect.ImmutableList;

public interface Task {
  public TaskKind kind();

  public Type type();

  public String name();

  public ImmutableList<Task> dependencies();

  public String description();

  public Location location();

  public default Space space() {
    return location().file().space();
  }

  public Feeder<Obj> compute(Worker worker);
}

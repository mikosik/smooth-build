package org.smoothbuild.exec.compute;

import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.Space;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.concurrent.Feeder;

import com.google.common.collect.ImmutableList;

/**
 * Subclasses of this class must be immutable.
 */
public abstract class Task {
  public static final int NAME_LENGTH_LIMIT = 40;

  private final Type type;
  private final String name;
  protected final ImmutableList<Task> dependencies;
  protected final Location location;

  public Task(Type type, String name, List<? extends Task> dependencies,
      Location location) {
    this.type = type;
    this.name = name;
    this.dependencies = ImmutableList.copyOf(dependencies);
    this.location = location;
  }

  public Type type() {
    return type;
  }

  public ImmutableList<Task> dependencies() {
    return dependencies;
  }

  public Location location() {
    return location;
  }

  public Space space() {
    return location.module().space();
  }

  public String name() {
    return name;
  }

  public String description() {
    return type.name() + " " + name;
  }

  public abstract Feeder<Obj> startComputation(Worker worker);

  public static List<Type> taskTypes(List<Task> tasks) {
    return map(tasks, Task::type);
  }

  public abstract TaskKind kind();
}

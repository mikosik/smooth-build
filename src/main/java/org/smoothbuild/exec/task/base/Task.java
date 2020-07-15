package org.smoothbuild.exec.task.base;

import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.exec.task.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Space;
import org.smoothbuild.lang.base.type.ConcreteType;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.util.concurrent.Feeder;

import com.google.common.collect.ImmutableList;

/**
 * Subclasses of this class must be immutable.
 */
public abstract class Task {
  public static final int NAME_LENGTH_LIMIT = 40;

  private final ConcreteType type;
  private final String name;
  protected final ImmutableList<Task> dependencies;
  protected final Location location;

  public Task(ConcreteType type, String name, List<? extends Task> dependencies,
      Location location) {
    this.type = type;
    this.name = name;
    this.dependencies = ImmutableList.copyOf(dependencies);
    this.location = location;
  }

  public ConcreteType type() {
    return type;
  }

  public ImmutableList<Task> children() {
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

  public abstract Feeder<SObject> startComputation(Worker worker);

  public static List<ConcreteType> taskTypes(List<Task> tasks) {
    return map(tasks, Task::type);
  }

  public abstract TaskKind kind();
}

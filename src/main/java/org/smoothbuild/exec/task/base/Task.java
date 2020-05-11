package org.smoothbuild.exec.task.base;

import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.ConvertAlgorithm;
import org.smoothbuild.exec.task.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Space;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.util.concurrent.Feeder;

import com.google.common.collect.ImmutableList;

/**
 * Subclasses of this class must be immutable.
 */
public abstract class Task {
  public static final int NAME_LENGTH_LIMIT = 40;

  protected final ImmutableList<Task> dependencies;
  protected final Location location;

  public Task(List<? extends Task> dependencies, Location location) {
    this.dependencies = ImmutableList.copyOf(dependencies);
    this.location = location;
  }

  public ImmutableList<Task> children() {
    return dependencies;
  }

  public Location location() {
    return location;
  }

  public Space space() {
    return location.path().space();
  }

  public Task convertIfNeeded(ConcreteType type) {
    if (type().equals(type)) {
      return this;
    } else {
      Algorithm algorithm = new ConvertAlgorithm(type, type());
      List<Task> dependencies = list(this);
      return new NormalTask(algorithm, dependencies, location(), true);
    }
  }

  public abstract String name();

  public abstract String description();

  public abstract ConcreteType type();

  public abstract Feeder<SObject> startComputation(Worker worker);

  public static List<ConcreteType> taskTypes(List<Task> tasks) {
    return map(tasks, Task::type);
  }

  public abstract TaskKind kind();
}

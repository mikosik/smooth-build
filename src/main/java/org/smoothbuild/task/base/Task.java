package org.smoothbuild.task.base;

import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.task.exec.Container;

import com.google.common.collect.ImmutableList;

public class Task {
  private final Computation computation;
  private final ImmutableList<Task> dependencies;
  private final String name;
  private final Location location;
  private final boolean isComputationCacheable;
  private TaskResult result;

  public Task(Computation computation, String name, boolean isComputationCacheable,
      List<? extends Task> dependencies, Location location) {
    this.computation = computation;
    this.dependencies = ImmutableList.copyOf(dependencies);
    this.name = name;
    this.location = location;
    this.isComputationCacheable = isComputationCacheable;
    this.result = null;
  }

  public ImmutableList<Task> dependencies() {
    return dependencies;
  }

  public String name() {
    return name;
  }

  public ConcreteType type() {
    return computation.type();
  }

  public Location location() {
    return location;
  }

  public void execute(Container container, Input input) {
    try {
      result = new TaskResult(computation.execute(input, container), false);
    } catch (ComputationException e) {
      result = new TaskResult(e);
    }
  }

  public Output output() {
    return result.output();
  }

  public boolean shouldCacheOutput() {
    return isComputationCacheable && result.hasOutput();
  }

  public Hash hash() {
    return computation.hash();
  }

  public void setResult(TaskResult result) {
    this.result = result;
  }

  public TaskResult result() {
    return result;
  }

  public boolean hasSuccessfulResult() {
    return result != null && result.hasOutputWithValue();
  }

  public Task convertIfNeeded(ConcreteType type) {
    if (type().equals(type)) {
      return this;
    } else {
      Computation computation = new ConvertComputation(type);
      List<Task> dependencies = list(this);
      return new Task(computation, "~conversion", true, dependencies, location());
    }
  }

  public static List<ConcreteType> taskTypes(List<Task> tasks) {
    return map(tasks, Task::type);
  }
}

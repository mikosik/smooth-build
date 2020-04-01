package org.smoothbuild.exec.task.base;

import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.comp.Computation;
import org.smoothbuild.exec.comp.ComputationException;
import org.smoothbuild.exec.comp.ConvertComputation;
import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.util.TreeNode;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Task implements TreeNode<Task> {
  private final Computation computation;
  private final ImmutableList<Task> dependencies;
  private final Location location;
  private final boolean isComputationCacheable;

  public Task(Computation computation, List<? extends Task> dependencies,
      Location location, boolean isComputationCacheable) {
    this.computation = computation;
    this.dependencies = ImmutableList.copyOf(dependencies);
    this.location = location;
    this.isComputationCacheable = isComputationCacheable;
  }

  @Override
  public ImmutableList<Task> children() {
    return dependencies;
  }

  public String name() {
    return computation.name();
  }

  public ConcreteType type() {
    return computation.type();
  }

  public Location location() {
    return location;
  }

  public TaskResult execute(Container container, Input input) {
    try {
      return new TaskResult(computation.execute(input, container), false);
    } catch (ComputationException e) {
      return new TaskResult(e);
    }
  }

  public boolean isComputationCacheable() {
    return isComputationCacheable;
  }

  public Hash hash() {
    return computation.hash();
  }

  public Task convertIfNeeded(ConcreteType type) {
    if (type().equals(type)) {
      return this;
    } else {
      Computation computation = new ConvertComputation(type);
      List<Task> dependencies = list(this);
      return new Task(computation, dependencies, location(), true);
    }
  }

  public static List<ConcreteType> taskTypes(List<Task> tasks) {
    return map(tasks, Task::type);
  }

  @Override
  public String toString() {
    return "Task(" + computation.name() + ")";
  }
}

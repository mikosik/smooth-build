package org.smoothbuild.exec.task;

import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.comp.Computation;
import org.smoothbuild.exec.comp.ComputationException;
import org.smoothbuild.exec.comp.ConvertComputation;
import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.exec.comp.Output;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.type.ConcreteType;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Task {
  private final Computation computation;
  private final ImmutableList<Task> dependencies;
  private final String name;
  private final Location location;
  private final boolean isComputationCacheable;

  public Task(Computation computation, String name, boolean isComputationCacheable,
      List<? extends Task> dependencies, Location location) {
    this.computation = computation;
    this.dependencies = ImmutableList.copyOf(dependencies);
    this.name = name;
    this.location = location;
    this.isComputationCacheable = isComputationCacheable;
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

  public Output execute(Container container, Input input) throws ComputationException {
    return computation.execute(input, container);
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
      return new Task(computation, "~conversion", true, dependencies, location());
    }
  }

  public static List<ConcreteType> taskTypes(List<Task> tasks) {
    return map(tasks, Task::type);
  }
}

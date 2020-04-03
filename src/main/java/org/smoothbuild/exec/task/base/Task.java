package org.smoothbuild.exec.task.base;

import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.ConvertAlgorithm;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.util.TreeNode;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Task implements TreeNode<Task> {
  private final Algorithm algorithm;
  private final ImmutableList<Task> dependencies;
  private final Location location;
  private final boolean isComputationCacheable;

  public Task(Algorithm algorithm, List<? extends Task> dependencies,
      Location location, boolean isComputationCacheable) {
    this.algorithm = algorithm;
    this.dependencies = ImmutableList.copyOf(dependencies);
    this.location = location;
    this.isComputationCacheable = isComputationCacheable;
  }

  @Override
  public ImmutableList<Task> children() {
    return dependencies;
  }

  public String name() {
    return algorithm.name();
  }

  public ConcreteType type() {
    return algorithm.type();
  }

  public Location location() {
    return location;
  }

  public Algorithm algorithm() {
    return algorithm;
  }

  public boolean isComputationCacheable() {
    return isComputationCacheable;
  }

  public Hash hash() {
    return algorithm.hash();
  }

  public Task convertIfNeeded(ConcreteType type) {
    if (type().equals(type)) {
      return this;
    } else {
      Algorithm algorithm = new ConvertAlgorithm(type);
      List<Task> dependencies = list(this);
      return new Task(algorithm, dependencies, location(), true);
    }
  }

  public static List<ConcreteType> taskTypes(List<Task> tasks) {
    return map(tasks, Task::type);
  }

  @Override
  public String toString() {
    return "Task(" + algorithm.name() + ")";
  }
}

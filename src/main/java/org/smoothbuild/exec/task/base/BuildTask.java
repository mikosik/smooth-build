package org.smoothbuild.exec.task.base;

import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.ConvertAlgorithm;
import org.smoothbuild.exec.task.parallel.ParallelTaskExecutor;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Space;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.util.concurrent.Feeder;

import com.google.common.collect.ImmutableList;

/**
 * Subclasses of this class must be immutable.
 */
public abstract class BuildTask implements Task {
  protected final ImmutableList<BuildTask> dependencies;
  protected final Location location;

  public BuildTask(List<? extends BuildTask> dependencies, Location location) {
    this.dependencies = ImmutableList.copyOf(dependencies);
    this.location = location;
  }

  public ImmutableList<BuildTask> children() {
    return dependencies;
  }

  public Location location() {
    return location;
  }

  @Override
  public Space space() {
    return location.path().space();
  }

  public BuildTask convertIfNeeded(ConcreteType type) {
    if (type().equals(type)) {
      return this;
    } else {
      Algorithm algorithm = new ConvertAlgorithm(type, type());
      List<BuildTask> dependencies = list(this);
      return new NormalTask(algorithm, dependencies, location(), true);
    }
  }

  public abstract String name();

  public abstract String description();

  public abstract ConcreteType type();

  public abstract Feeder<SObject> startComputation(ParallelTaskExecutor.Worker worker);

  public static List<ConcreteType> taskTypes(List<BuildTask> tasks) {
    return map(tasks, BuildTask::type);
  }
}

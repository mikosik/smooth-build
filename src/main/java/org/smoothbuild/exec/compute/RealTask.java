package org.smoothbuild.exec.compute;

import java.util.List;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableList;

/**
 * Subclasses of this class must be immutable.
 */
public abstract class RealTask implements Task {
  public static final int NAME_LENGTH_LIMIT = 40;

  private final TaskKind kind;
  private final Type type;
  private final String name;
  protected final ImmutableList<Task> dependencies;
  protected final Location location;

  public RealTask(TaskKind kind, Type type, String name, List<Task> dependencies,
      Location location) {
    this.kind = kind;
    this.type = type;
    this.name = name;
    this.dependencies = ImmutableList.copyOf(dependencies);
    this.location = location;
  }

  @Override
  public TaskKind kind() {
    return kind;
  }

  @Override
  public Type type() {
    return type;
  }

  @Override
  public ImmutableList<Task> dependencies() {
    return dependencies;
  }

  @Override
  public Location location() {
    return location;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String description() {
    return type.name() + " " + name;
  }
}

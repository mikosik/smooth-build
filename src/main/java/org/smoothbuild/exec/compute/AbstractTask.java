package org.smoothbuild.exec.compute;

import java.util.List;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableList;

public abstract class AbstractTask implements Task {
  protected final TaskKind kind;
  protected final Type type;
  protected final String name;
  protected final ImmutableList<Task> dependencies;
  protected final Location location;

  public AbstractTask(
      TaskKind kind, Type type, String name, List<Task> dependencies, Location location) {
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
  public String name() {
    return name;
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
  public String description() {
    return type().name() + " " + name();
  }
}

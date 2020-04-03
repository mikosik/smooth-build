package org.smoothbuild.exec.task.base;

import java.util.List;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.type.ConcreteType;

public abstract class NonComputableTask extends Task {
  private final String name;
  private final ConcreteType type;

  public NonComputableTask(String name, ConcreteType type, List<? extends Task> dependencies,
      Location location) {
    super(dependencies, location);
    this.name = name;
    this.type = type;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public ConcreteType type() {
    return type;
  }
}

package org.smoothbuild.exec.task.base;

import java.util.List;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.ConcreteType;

public abstract class NonComputableTask extends Task {
  private final String name;

  public NonComputableTask(String name, ConcreteType type, List<? extends Task> dependencies,
      Location location) {
    super(type, dependencies, location);
    this.name = name;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String sourceDescription() {
    return " " + name;
  }
}

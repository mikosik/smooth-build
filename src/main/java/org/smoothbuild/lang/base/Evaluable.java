package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.parse.ast.Named;

public abstract class Evaluable implements Named {
  private final Type type;
  private final Location location;
  private final String name;

  public Evaluable(Type type, String name, Location location) {
    this.type = checkNotNull(type);
    this.location = checkNotNull(location);
    this.name = checkNotNull(name);
  }

  @Override
  public Location location() {
    return location;
  }

  public Type type() {
    return type;
  }

  @Override
  public String name() {
    return name;
  }

  public String q() {
    return "`" + name() + "`";
  }

  public String extendedName() {
    return name();
  }
}

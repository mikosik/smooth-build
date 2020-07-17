package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.Type;

public class Node {
  private final Location location;
  private Optional<Type> type;

  public Node(Location location) {
    this.location = location;
  }

  public Location location() {
    return location;
  }

  public Optional<Type> type() {
    return type;
  }

  public void setType(Type type) {
    this.type = Optional.of(type);
  }

  public void setType(Optional<Type> type) {
    this.type = type;
  }
}

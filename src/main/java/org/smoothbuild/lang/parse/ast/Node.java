package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.TypeS;

public class Node {
  private final Location location;
  private Optional<TypeS> type;

  public Node(Location location) {
    this.location = location;
  }

  public Location location() {
    return location;
  }

  public Optional<TypeS> type() {
    return type;
  }

  public void setType(TypeS type) {
    setType(Optional.of(type));
  }

  public void setType(Optional<TypeS> type) {
    this.type = type;
  }
}

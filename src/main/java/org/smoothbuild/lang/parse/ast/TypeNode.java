package org.smoothbuild.lang.parse.ast;

import static org.smoothbuild.lang.base.type.Types.isGenericTypeName;
import static org.smoothbuild.lang.base.type.Types.nothing;

import org.smoothbuild.lang.base.Location;

public class TypeNode extends NamedNode {
  public TypeNode(String name, Location location) {
    super(name, location);
  }

  public boolean isArray() {
    return this instanceof ArrayTypeNode;
  }

  public boolean isNothing() {
    return name().equals(nothing().name());
  }

  public boolean isGeneric() {
    return isGenericTypeName(name());
  }

  public TypeNode coreType() {
    return this;
  }
}

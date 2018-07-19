package org.smoothbuild.parse.ast;

import static org.smoothbuild.lang.type.TypeNames.isGenericTypeName;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.type.TypeNames;

public class TypeNode extends NamedNode {
  public TypeNode(String name, Location location) {
    super(name, location);
  }

  public boolean isArray() {
    return this instanceof ArrayTypeNode;
  }

  public boolean isNothing() {
    return name().equals(TypeNames.NOTHING);
  }

  public boolean isGeneric() {
    return isGenericTypeName(name());
  }

  public TypeNode coreType() {
    return this;
  }
}

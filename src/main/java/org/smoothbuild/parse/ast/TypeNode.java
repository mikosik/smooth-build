package org.smoothbuild.parse.ast;

import static org.smoothbuild.lang.type.TypeNames.isGenericTypeName;

import org.smoothbuild.lang.message.Location;

public class TypeNode extends NamedNode {
  public TypeNode(String name, Location location) {
    super(name, location);
  }

  public boolean isGeneric() {
    return isGenericTypeName(name());
  }
}

package org.smoothbuild.lang.parse.ast;

import static org.smoothbuild.lang.base.type.Types.isVariableName;
import static org.smoothbuild.lang.base.type.Types.nothing;

import org.smoothbuild.lang.base.Location;

import com.google.common.collect.ImmutableSet;

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

  public boolean isPolytype() {
    return isVariableName(name());
  }

  public ImmutableSet<TypeNode> variables() {
    if (isVariableName(name())) {
      return ImmutableSet.of(this);
    } else {
      return ImmutableSet.of();
    }
  }
}

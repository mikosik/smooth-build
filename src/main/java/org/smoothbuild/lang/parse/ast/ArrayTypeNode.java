package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.util.collect.CountersMap;

public final class ArrayTypeNode extends TypeNode {
  private final TypeNode elementType;

  public ArrayTypeNode(TypeNode elementType, Location location) {
    super("[" + elementType.name() + "]", location);
    this.elementType = elementType;
  }

  @Override
  public boolean isPolytype() {
    return elementType.isPolytype();
  }

  @Override
  public void countVariables(CountersMap<String> countersMap) {
    elementType.countVariables(countersMap);
  }

  public TypeNode elementType() {
    return elementType;
  }
}

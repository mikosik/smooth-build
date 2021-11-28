package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.util.collect.CountersMap;

public final class ArrayTypeNode extends TypeNode {
  private final TypeNode elemType;

  public ArrayTypeNode(TypeNode elemType, Location location) {
    super("[" + elemType.name() + "]", location);
    this.elemType = elemType;
  }

  @Override
  public boolean isPolytype() {
    return elemType.isPolytype();
  }

  @Override
  public void countVariables(CountersMap<String> countersMap) {
    elemType.countVariables(countersMap);
  }

  public TypeNode elemType() {
    return elemType;
  }
}

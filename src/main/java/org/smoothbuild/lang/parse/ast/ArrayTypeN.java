package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.util.collect.CountersMap;

public final class ArrayTypeN extends TypeN {
  private final TypeN elemType;

  public ArrayTypeN(TypeN elemType, Loc loc) {
    super("[" + elemType.name() + "]", loc);
    this.elemType = elemType;
  }

  @Override
  public boolean isPolytype() {
    return elemType.isPolytype();
  }

  @Override
  public void countVars(CountersMap<String> countersMap) {
    elemType.countVars(countersMap);
  }

  public TypeN elemType() {
    return elemType;
  }
}

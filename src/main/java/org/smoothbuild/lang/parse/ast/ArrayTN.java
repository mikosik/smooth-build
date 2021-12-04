package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.util.collect.CountersMap;

public final class ArrayTN extends TypeN {
  private final TypeN elemT;

  public ArrayTN(TypeN elemT, Loc loc) {
    super("[" + elemT.name() + "]", loc);
    this.elemT = elemT;
  }

  @Override
  public boolean isPolytype() {
    return elemT.isPolytype();
  }

  @Override
  public void countVars(CountersMap<String> countersMap) {
    elemT.countVars(countersMap);
  }

  public TypeN elemT() {
    return elemT;
  }
}

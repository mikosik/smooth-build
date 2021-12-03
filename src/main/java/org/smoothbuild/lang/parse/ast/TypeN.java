package org.smoothbuild.lang.parse.ast;

import static org.smoothbuild.lang.base.type.api.TypeNames.isVarName;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.util.collect.CountersMap;

import com.google.common.collect.ImmutableList;

public sealed class TypeN extends NamedN permits ArrayTN, FuncTN {
  public TypeN(String name, Loc loc) {
    super(name, loc);
  }

  public boolean isPolytype() {
    return isVarName(name());
  }

  public ImmutableList<String> varsUsedOnce() {
    var counters = new CountersMap<String>();
    countVars(counters);
    return counters.keysWithCounter(1);
  }

  public void countVars(CountersMap<String> countersMap) {
    if (isVarName(name())) {
      countersMap.increment(name());
    }
  }
}

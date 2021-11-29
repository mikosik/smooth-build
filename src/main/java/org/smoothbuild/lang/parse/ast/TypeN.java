package org.smoothbuild.lang.parse.ast;

import static org.smoothbuild.lang.base.type.api.TypeNames.isVariableName;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.util.collect.CountersMap;

import com.google.common.collect.ImmutableList;

public sealed class TypeN extends NamedN permits ArrayTypeN, FuncTypeN {
  public TypeN(String name, Loc loc) {
    super(name, loc);
  }

  public boolean isPolytype() {
    return isVariableName(name());
  }

  public ImmutableList<String> variablesUsedOnce() {
    var counters = new CountersMap<String>();
    countVariables(counters);
    return counters.keysWithCounter(1);
  }

  public void countVariables(CountersMap<String> countersMap) {
    if (isVariableName(name())) {
      countersMap.increment(name());
    }
  }
}

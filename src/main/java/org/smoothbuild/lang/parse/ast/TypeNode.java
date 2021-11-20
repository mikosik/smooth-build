package org.smoothbuild.lang.parse.ast;

import static org.smoothbuild.lang.base.type.api.TypeNames.isVariableName;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.util.collect.CountersMap;

import com.google.common.collect.ImmutableList;

public sealed class TypeNode extends NamedNode permits ArrayTypeNode, FunctionTypeNode {
  public TypeNode(String name, Location location) {
    super(name, location);
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
